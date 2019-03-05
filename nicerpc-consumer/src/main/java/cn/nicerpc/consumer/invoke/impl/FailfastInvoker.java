package cn.nicerpc.consumer.invoke.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.core.TCPClient;
import cn.nicerpc.consumer.invoke.AbstractInvoker;

/**
 * 快速失败，只发起一次调用，失败立即报错
 * @param <T>
 */
public class FailfastInvoker<T> extends AbstractInvoker<T> {

    @Override
    protected Response doInvoke(ClientRequest invocation) throws Exception {
        try {
            TCPClient client = TCPClient.getInstance(invocation.getHost(),invocation.getPort(),1500);
            Response response = client.send(invocation);
            return response;
        } catch (Exception e) {
            throw e;
        }
    }
}
