/*
 * *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017-2019 nuls.io
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package io.nuls.kernel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nuls.kernel.constant.KernelErrorCode;
import io.nuls.kernel.constant.NulsConstant;
import io.nuls.kernel.context.NulsContext;
import io.nuls.kernel.exception.NulsException;
import io.nuls.kernel.exception.NulsRuntimeException;
import io.nuls.kernel.func.TimeService;
import io.nuls.kernel.script.Script;
import io.nuls.kernel.utils.AddressTool;
import io.nuls.kernel.utils.NulsByteBuffer;
import io.nuls.kernel.utils.NulsOutputStreamBuffer;
import io.nuls.kernel.utils.SerializeUtils;

import java.io.IOException;

/**
 * @author ln
 */
public class Coin extends BaseNulsData {

    private byte[] owner;

    private Na na;

    private long lockTime;

    private transient Coin from;
    /**
     * 合约组装CoinData时使用
     */
    private transient String key;

    private transient byte[] tempOwner;

    private transient String fromAddress;

    public Coin() {
    }

    public Coin(byte[] owner, Na na) {
        this.owner = owner;
        this.na = na;
    }

    public Coin(byte[] owner, Na na, long lockTime) {
        this(owner, na);
        this.lockTime = lockTime;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBytesWithLength(owner);
        stream.writeInt64(na.getValue());
        stream.writeUint48(lockTime);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.owner = byteBuffer.readByLengthByte();
        this.na = Na.valueOf(byteBuffer.readInt64());
        this.lockTime = byteBuffer.readUint48();
    }

    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfBytes(owner);
        size += SerializeUtils.sizeOfInt64();
        size += SerializeUtils.sizeOfUint48();
        return size;
    }


    public Na getNa() {
        return na;
    }

    public byte[] getOwner() {
        return owner;
    }

    public long getLockTime() {
        return lockTime;
    }

    public Coin getFrom() {
        return from;
    }

    public void setFrom(Coin from) {
        this.from = from;
    }

    public void setOwner(byte[] owner) {
        this.owner = owner;
    }

    public void setNa(Na na) {
        this.na = na;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public String getKey() {
        return key;
    }

    public Coin setKey(String key) {
        this.key = key;
        return this;
    }

    public byte[] getTempOwner() {
        return tempOwner;
    }

    public void setTempOwner(byte[] tempOwner) {
        this.tempOwner = tempOwner;
    }

    /**
     * 根据当前时间和当前最新高度，判断coin是否可用
     *
     * @return boolean
     */
    public boolean usable() {
        long bestHeight = NulsContext.getInstance().getBestHeight();
        return usable(bestHeight);
    }

    public boolean usable(Long bestHeight) {
        if (lockTime < 0) {
            return false;
        }
        if (lockTime == 0) {
            return true;
        }

        long currentTime = TimeService.currentTimeMillis();
        //long bestHeight = NulsContext.getInstance().getBestHeight();

        if (lockTime > NulsConstant.BlOCKHEIGHT_TIME_DIVIDE) {
            if (lockTime <= currentTime) {
                return true;
            } else {
                return false;
            }
        } else {
            if (lockTime <= bestHeight) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "Coin{" +
                "owner=" + AddressTool.getStringAddressByBytes(owner) +
                ", na=" + na.getValue() +
                ", lockTime=" + lockTime +
                ", from=" + from +
                ", key='" + key + '\'' +
                '}';
    }

    @JsonIgnore
    public byte[] getAddress() {
        byte[] address = new byte[23];
        //如果owner不是存放的脚本则直接返回owner
        if (owner == null || owner.length == 23) {
            return owner;
        } else {
            Script scriptPubkey = new Script(owner);
            //如果为P2PKH类型交易则从第四位开始返回23个字节
            if (scriptPubkey.isSentToAddress()) {
                System.arraycopy(owner, 3, address, 0, 23);
            }
            //如果为P2SH或multi类型的UTXO则从第三位开始返回23个字节
            else if (scriptPubkey.isPayToScriptHash()) {
                scriptPubkey.isSentToMultiSig();
                System.arraycopy(owner, 2, address, 0, 23);
            }else{
                throw new NulsRuntimeException(KernelErrorCode.ADDRESS_IS_NOT_BELONGS_TO_CHAIN);
            }
        }
        return address;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}