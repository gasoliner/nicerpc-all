package cn.nicerpc.consumer.invoke.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.core.TCPClient;
import cn.nicerpc.consumer.invoke.AbstractInvoker;

/**
 * failsafe，失败安全
 *  invoke异常后，忽略异常，常用于允许丢失一定信息量的操作，比如写入审计日志等操作
 */
public class FailsafeInvoker extends AbstractInvoker {
    @Override
    protected Response doInvoke(ClientRequest invocation) throws Exception {
        ClientRequest selfInfo = getInvocation();
        try {
            TCPClient client = TCPClient.getInstance(selfInfo.getHost(),selfInfo.getPort(),1500);
            Response response = client.send(invocation);
            return response;
        } catch (Exception e) {
            System.out.println("Failsafe throw e, ignore this exception , e :" + e.getCause());
            return new Response();
        }
    }
}
