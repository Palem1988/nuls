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
package io.nuls.network.netty.task;

import io.nuls.core.tools.log.Log;
import io.nuls.kernel.context.NulsContext;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.constant.NetworkParam;
import io.nuls.network.model.Node;
import io.nuls.network.netty.broadcast.BroadcastHandler;
import io.nuls.network.netty.container.NodesContainer;
import io.nuls.network.netty.manager.NodeManager;
import io.nuls.network.protocol.message.GetVersionMessage;
import io.nuls.network.protocol.message.NetworkMessageBody;
import io.nuls.network.storage.service.NetworkStorageService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * 维护节点高度的定时任务
 *
 * @author: ln
 * @date: 2018/12/8
 */
public class SaveNodeInfoTask implements Runnable {

    private final NodesContainer nodesContainer = NodeManager.getInstance().getNodesContainer();


    private NetworkStorageService networkStorageService;

    public SaveNodeInfoTask() {

    }

    /**
     * 每5分钟一次，将整个NodeContainer对象存储到文件中
     */
    @Override
    public void run() {
        getNetworkStorageService().saveNodes(nodesContainer.getDisconnectNodes(),nodesContainer.getCanConnectNodes(),nodesContainer.getFailNodes(),nodesContainer.getUncheckNodes(),nodesContainer.getConnectedNodes());
    }

    private NetworkStorageService getNetworkStorageService() {
        if (networkStorageService == null) {
            networkStorageService = NulsContext.getServiceBean(NetworkStorageService.class);
        }
        return networkStorageService;
    }

}
