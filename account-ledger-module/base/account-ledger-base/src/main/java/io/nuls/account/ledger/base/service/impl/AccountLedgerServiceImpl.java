/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nuls.account.ledger.base.service.impl;

import io.nuls.account.ledger.base.service.balance.BalanceProvider;
import io.nuls.account.ledger.base.util.CoinComparator;
import io.nuls.account.ledger.base.util.TxInfoComparator;
import io.nuls.account.ledger.service.AccountLedgerService;
import io.nuls.account.ledger.storage.po.TransactionInfoPo;
import io.nuls.account.model.Account;
import io.nuls.account.model.Address;
import io.nuls.account.model.Balance;
import io.nuls.account.service.AccountService;
import io.nuls.account.ledger.constant.AccountLedgerErrorCode;
import io.nuls.account.ledger.model.TransactionInfo;
import io.nuls.account.ledger.storage.service.AccountLedgerStorageService;

import io.nuls.account.ledger.model.CoinDataResult;
import io.nuls.core.tools.crypto.Base58;
import io.nuls.core.tools.log.Log;
import io.nuls.core.tools.param.AssertUtil;
import io.nuls.core.tools.str.StringUtils;
import io.nuls.kernel.cfg.NulsConfig;
import io.nuls.kernel.context.NulsContext;
import io.nuls.kernel.exception.NulsException;
import io.nuls.kernel.exception.NulsRuntimeException;
import io.nuls.kernel.func.TimeService;
import io.nuls.kernel.lite.annotation.Autowired;
import io.nuls.kernel.lite.annotation.Component;
import io.nuls.kernel.lite.core.bean.InitializingBean;
import io.nuls.kernel.model.*;
import io.nuls.kernel.script.P2PKHScriptSig;
import io.nuls.kernel.utils.AddressTool;
import io.nuls.kernel.utils.TransactionFeeCalculator;
import io.nuls.kernel.utils.VarInt;
import io.nuls.ledger.constant.LedgerErrorCode;

import io.nuls.ledger.service.LedgerService;
import io.nuls.protocol.model.tx.TransferTransaction;
import io.nuls.protocol.service.BlockService;
import io.nuls.protocol.service.TransactionService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Facjas
 * @date 2018/5/10.
 */
@Component
public class AccountLedgerServiceImpl implements AccountLedgerService, InitializingBean {

    @Autowired
    private AccountLedgerStorageService storageService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BalanceProvider balanceProvider;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private BlockService blockService;

    private static List<Account> localAccountList;

    @Override
    public void afterPropertiesSet() throws NulsException {
        init();
    }

    @Override
    public Result<Integer> saveConfirmedTransaction(Transaction tx) {
        return saveTransaction(tx, TransactionInfo.CONFIRMED);
    }

    @Override
    public Result<Integer> saveUnconfirmedTransaction(Transaction tx) {
        return saveTransaction(tx, TransactionInfo.UNCONFIRMED);
    }

    @Override
    public Result<Integer> saveConfirmedTransactionList(List<Transaction> txs) {
        List<Transaction> savedTxList = new ArrayList<>();
        Result result;
        for (int i = 0; i < txs.size(); i++) {
            result = saveConfirmedTransaction(txs.get(i));
            if (result.isSuccess()) {
                savedTxList.add(txs.get(i));
            } else {
                rollback(savedTxList, false);
                return result;
            }
        }
        return Result.getSuccess().setData(savedTxList.size());
    }

    @Override
    public Result<Integer> rollback(Transaction tx) {
        if (!isLocalTransaction(tx)) {
            return Result.getFailed().setData(new Integer(0));
        }

        TransactionInfoPo txInfoPo = new TransactionInfoPo(tx);
        Result result = storageService.deleteLocalTxInfo(txInfoPo);

        if (result.isFailed()) {
            return result;
        }
        result = storageService.deleteLocalTx(tx);

        return result;
    }

    @Override
    public Result<Integer> rollback(List<Transaction> txs) {
        return rollback(txs, true);
    }

    public Result<Integer> rollback(List<Transaction> txs, boolean isCheckMine) {
        List<Transaction> txListToRollback;
        if (isCheckMine) {
            txListToRollback = getLocalTransaction(txs);
        } else {
            txListToRollback = txs;
        }
        for (int i = 0; i < txListToRollback.size(); i++) {
            rollback(txListToRollback.get(i));
        }

        return Result.getSuccess().setData(new Integer(txListToRollback.size()));
    }

    @Override
    public Result<Balance> getBalance(byte[] address) throws NulsException {
        if (address == null || address.length != AddressTool.HASH_LENGTH) {
            return Result.getFailed(AccountLedgerErrorCode.PARAMETER_ERROR);
        }

        if (!isLocalAccount(address)) {
            return Result.getFailed(AccountLedgerErrorCode.ACCOUNT_NOT_EXIST);
        }

        Balance balance = balanceProvider.getBalance(address).getData();

        if (balance == null) {
            return Result.getFailed(AccountLedgerErrorCode.ACCOUNT_NOT_EXIST);
        }

        return Result.getSuccess().setData(balance);
    }

    @Override
    public CoinDataResult getCoinData(byte[] address, Na amount, int size) throws NulsException {
        CoinDataResult coinDataResult = new CoinDataResult();
        List<Coin> coinList = storageService.getCoinBytes(address);
        if (coinList.isEmpty()) {
            coinDataResult.setEnough(false);
            return coinDataResult;
        }
        Collections.sort(coinList, CoinComparator.getInstance());

        boolean enough = false;
        List<Coin> coins = new ArrayList<>();
        Na values = Na.ZERO;
        for (int i = 0; i < coinList.size(); i++) {
            Coin coin = coinList.get(i);
            if (!coin.usable()) {
                continue;
            }
            coins.add(coin);
            size += coin.size();
            Na fee = TransactionFeeCalculator.getFee(size);
            values = values.add(coin.getNa());
            if (values.isGreaterOrEquals(amount.add(fee))) {
                enough = true;
                coinDataResult.setEnough(true);
                coinDataResult.setFee(fee);
                coinDataResult.setCoinList(coins);

                Na change = values.subtract(amount.add(fee));
                if (change.isGreaterThan(Na.ZERO)) {
                    Coin changeCoin = new Coin();
                    changeCoin.setOwner(address);
                    changeCoin.setNa(change);
                    coinDataResult.setChange(changeCoin);
                }
                break;
            }
        }
        if (!enough) {
            coinDataResult.setEnough(false);
            return coinDataResult;
        }
        return coinDataResult;
    }

    @Override
    public boolean isLocalAccount(byte[] address) {
        if (localAccountList == null || localAccountList.size() == 0) {
            return false;
        }

        for (int i = 0; i < localAccountList.size(); i++) {
            if (Arrays.equals(localAccountList.get(i).getAddress().getBase58Bytes(), address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Result transfer(byte[] from, byte[] to, Na values, String password, String remark) {
        try {
            AssertUtil.canNotEmpty(from, "the from address can not be empty");
            AssertUtil.canNotEmpty(to, "the to address can not be empty");
            AssertUtil.canNotEmpty(values, "the amount can not be empty");

            if (values.isZero() || values.isLessThan(Na.ZERO)) {
                return Result.getFailed("amount error");
            }

            Result<Account> accountResult = accountService.getAccount(from);
            if (accountResult.isFailed()) {
                return accountResult;
            }
            Account account = accountResult.getData();

            if (accountService.isEncrypted(account).isSuccess()) {
                AssertUtil.canNotEmpty(password, "the password can not be empty");

                Result passwordResult = accountService.validPassword(account, password);
                if (passwordResult.isFailed()) {
                    return passwordResult;
                }
            }

            TransferTransaction tx = new TransferTransaction();
            if (StringUtils.isNotBlank(remark)) {
                try {
                    tx.setRemark(remark.getBytes(NulsConfig.DEFAULT_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            tx.setTime(TimeService.currentTimeMillis());
            CoinData coinData = new CoinData();
            Coin toCoin = new Coin(to, values);
            coinData.getTo().add(toCoin);

            CoinDataResult coinDataResult = getCoinData(from, values, tx.size() + P2PKHScriptSig.DEFAULT_SERIALIZE_LENGTH);
            if (!coinDataResult.isEnough()) {
                return Result.getFailed(LedgerErrorCode.BALANCE_NOT_ENOUGH);
            }
            coinData.setFrom(coinDataResult.getCoinList());
            if (coinDataResult.getChange() != null) {
                coinData.getTo().add(coinDataResult.getChange());
            }
            tx.setCoinData(coinData);

            tx.setHash(NulsDigestData.calcDigestData(tx.serialize()));
            P2PKHScriptSig sig = new P2PKHScriptSig();
            sig.setPublicKey(account.getPubKey());
            sig.setSignData(accountService.signData(tx.getHash().serialize(), account, password));
            tx.setScriptSig(sig.serialize());

            tx.verifyWithException();
            Result saveResult = saveUnconfirmedTransaction(tx);
            if (saveResult.isFailed()) {
                return saveResult;
            }
            Result sendResult = this.transactionService.broadcastTx(tx);
            if (sendResult.isFailed()) {
                return sendResult;
            }
            return Result.getSuccess().setData(tx.getHash().getDigestHex());
        } catch (IOException e) {
            Log.error(e);
            return Result.getFailed(e.getMessage());
        } catch (NulsException e) {
            Log.error(e);
            return Result.getFailed(e.getErrorCode());
        }
    }

    @Override
    public Result unlockCoinData(Transaction tx) {
        List<byte[]> addresses = getRelatedAddresses(tx);
        if (addresses == null || addresses.size() == 0) {
            return Result.getFailed().setData(new Integer(0));
        }
        byte status = TransactionInfo.CONFIRMED;
        TransactionInfoPo txInfoPo = new TransactionInfoPo(tx);
        txInfoPo.setStatus(status);

        byte[] txHashBytes = new byte[0];
        try {
            txHashBytes = tx.getHash().serialize();
        } catch (IOException e) {
            throw new NulsRuntimeException(e);
        }
        CoinData coinData = tx.getCoinData();
        if (coinData != null) {
            // unlock utxo - to
            List<Coin> tos = coinData.getTo();
            byte[] indexBytes;
            for (int i = 0, length = tos.size(); i < length; i++) {
                if(tos.get(i).getLockTime() == -1) {
                    tos.get(i).setLockTime(0);
                    try {
                        byte[] outKey = org.spongycastle.util.Arrays.concatenate(tos.get(i).getOwner(), tx.getHash().serialize(), new VarInt(i).encode());
                        storageService.saveOutPut(outKey, tos.get(i).serialize());
                    } catch (IOException e) {
                        throw new NulsRuntimeException(e);
                    }
                    //todo , think about weather to add a transaction history
                    //addresses.clear();
                    //addresses.add(tos.get(i).getOwner());
                    //Result result = storageService.saveLocalTxInfo(txInfoPo, addresses);
                }
            }
        }
        return Result.getSuccess();
    }

    @Override
    public Result rollbackUnlockTxCoinData(Transaction tx) {
        List<byte[]> addresses = getRelatedAddresses(tx);
        if (addresses == null || addresses.size() == 0) {
            return Result.getFailed().setData(new Integer(0));
        }
        byte status = TransactionInfo.CONFIRMED;
        TransactionInfoPo txInfoPo = new TransactionInfoPo(tx);
        txInfoPo.setStatus(status);

        byte[] txHashBytes = new byte[0];
        try {
            txHashBytes = tx.getHash().serialize();
        } catch (IOException e) {
            throw new NulsRuntimeException(e);
        }
        CoinData coinData = tx.getCoinData();
        if (coinData != null) {
            // lock utxo - to
            List<Coin> tos = coinData.getTo();
            for (int i = 0, length = tos.size(); i < length; i++) {
                if(tos.get(i).getLockTime() == -1) {
                    try {
                        byte[] outKey = org.spongycastle.util.Arrays.concatenate(tos.get(i).getOwner(), tx.getHash().serialize(), new VarInt(i).encode());
                        storageService.saveOutPut(outKey, tos.get(i).serialize());
                    } catch (IOException e) {
                        throw new NulsRuntimeException(e);
                    }
                }
            }
        }
        return Result.getSuccess();
    }

    @Override
    public Result importAccountLedger(String address) {
        if (address == null || !Address.validAddress(address)) {
            return Result.getFailed(AccountLedgerErrorCode.ADDRESS_ERROR);
        }

        byte[] addressBytes = null;
        try {
            addressBytes = Base58.decode(address);
        } catch (Exception e) {
            return Result.getFailed(AccountLedgerErrorCode.ADDRESS_ERROR);
        }

        // 确认先刷新账户是否就不存在这个问题了 todo, when the node is downloading blocks, the txs in newly downloaded blocks will miss
        reloadAccount();

        long height = NulsContext.getInstance().getBestHeight();
        for (int i = 0; i <= height; i++) {
            List<NulsDigestData> txs = blockService.getBlock(i).getData().getTxHashList();
            for (int j = 0; j < txs.size(); j++) {
                Transaction tx = ledgerService.getTx(txs.get(j));
                saveTransaction(tx, addressBytes, TransactionInfo.CONFIRMED);
            }
        }
        try {
            balanceProvider.refreshBalance(addressBytes);
        } catch (Exception e) {
            Log.info(address);
        }
        return Result.getSuccess();
    }

    @Override
    public Result<List<TransactionInfo>> getTxInfoList(byte[] address) {
        try {
            List<TransactionInfoPo> infoPoList = storageService.getTxInfoList(address);
            List<TransactionInfo> infoList = new ArrayList<>();
            for (TransactionInfoPo po : infoPoList) {
                infoList.add(po.toTransactionInfo());
            }

            Collections.sort(infoList, TxInfoComparator.getInstance());
            return Result.getSuccess().setData(infoList);
        } catch (NulsException e) {
            Log.error(e);
            return Result.getFailed(e.getErrorCode());
        }
    }

    protected Result<Integer> saveTransaction(Transaction tx, byte status) {

        List<byte[]> addresses = getRelatedAddresses(tx);
        if (addresses == null || addresses.size() == 0) {
            return Result.getFailed().setData(new Integer(0));
        }

        TransactionInfoPo txInfoPo = new TransactionInfoPo(tx);
        txInfoPo.setStatus(status);

        Result result = storageService.saveLocalTxInfo(txInfoPo, addresses);

        if (result.isFailed()) {
            return result;
        }
        result = storageService.saveLocalTx(tx);
        if (result.isFailed()) {
            storageService.deleteLocalTxInfo(txInfoPo);
        }

        if (status == TransactionInfo.UNCONFIRMED) {
            result = storageService.saveTempTx(tx);
        }
        for (int i = 0; i < addresses.size(); i++) {
            balanceProvider.refreshBalance(addresses.get(i));
        }
        return result;
    }

    protected Result<Integer> saveTransaction(Transaction tx, byte[] address, byte status) {
        List<byte[]> destAddresses = new ArrayList<byte[]>();
        destAddresses.add(address);
        List<byte[]> addresses = getRelatedAddresses(tx, destAddresses);
        if (addresses == null || addresses.size() == 0) {
            return Result.getFailed().setData(new Integer(0));
        }

        TransactionInfoPo txInfoPo = new TransactionInfoPo(tx);
        txInfoPo.setStatus(status);

        Result result = storageService.saveLocalTxInfo(txInfoPo, addresses);

        if (result.isFailed()) {
            return result;
        }
        result = storageService.saveLocalTx(tx);
        if (result.isFailed()) {
            storageService.deleteLocalTxInfo(txInfoPo);
        }
        return result;
    }


    protected List<Transaction> getLocalTransaction(List<Transaction> txs) {
        List<Transaction> resultTxs = new ArrayList<>();
        if (txs == null || txs.size() == 0) {
            return resultTxs;
        }
        if (localAccountList == null || localAccountList.size() == 0) {
            return resultTxs;
        }
        Transaction tmpTx;
        for (int i = 0; i < txs.size(); i++) {
            tmpTx = txs.get(i);
            if (isLocalTransaction(tmpTx)) {
                resultTxs.add(tmpTx);
            }
        }
        return resultTxs;
    }

    protected List<byte[]> getRelatedAddresses(Transaction tx) {
        List<byte[]> result = new ArrayList<>();
        if (tx == null) {
            return result;
        }
        if (localAccountList == null || localAccountList.size() == 0) {
            return result;
        }
        List<byte[]> destAddresses = new ArrayList<>();
        for (Account account : localAccountList) {
            destAddresses.add(account.getAddress().getBase58Bytes());
        }

        return getRelatedAddresses(tx, destAddresses);
    }

    protected List<byte[]> getRelatedAddresses(Transaction tx, List<byte[]> addresses) {
        List<byte[]> result = new ArrayList<>();
        if (tx == null) {
            return result;
        }
        if (addresses == null || addresses.size() == 0) {
            return result;
        }
        List<byte[]> sourceAddresses = tx.getAllRelativeAddress();
        if (sourceAddresses == null || sourceAddresses.size() == 0) {
            return result;
        }

        for (byte[] tempSourceAddress : sourceAddresses) {
            for (byte[] tempDestAddress : addresses) {
                if (Arrays.equals(tempDestAddress, tempSourceAddress)) {
                    result.add(tempSourceAddress);
                    continue;
                }
            }
        }
        return result;
    }

    protected boolean isLocalTransaction(Transaction tx) {
        if (tx == null) {
            return false;
        }
        if (localAccountList == null || localAccountList.size() == 0) {
            return false;
        }
        List<byte[]> addresses = tx.getAllRelativeAddress();
        for (int j = 0; j < addresses.size(); j++) {
            if (isLocalAccount(addresses.get(j))) {
                return true;
            }
        }
        return false;
    }

    public void reloadAccount() {
        localAccountList = accountService.getAccountList().getData();
    }

    public void init() {
        reloadAccount();
    }

}