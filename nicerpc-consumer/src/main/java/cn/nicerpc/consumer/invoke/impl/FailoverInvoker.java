package cn.nicerpc.consumer.invoke.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.core.TCPClient;
import cn.nicerpc.consumer.invoke.AbstractInvoker;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.invoke.InvokerManager;
import cn.nicerpc.consumer.loadBalance.LoadBalanceManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * not good
 * <p>
 * 每个invoker试一遍，
 * 如果出错，直接从没试过的list中选择一个重试
 */
public class FailoverInvoker extends AbstractInvoker {
    @Override
    protected Response doInvoke(ClientRequest invocation) throws Exception {
        ClientRequest selfInfo = getInvocation();
        Response response = null;
        try {
            TCPClient client = TCPClient.getInstance(selfInfo.getHost(), selfInfo.getPort(), 1500);
            response = client.send(invocation);
            System.out.println("FailoverInvoker " + this.id + " doInvoker 成功！");
        } catch (Exception e) {
            System.out.println("FailoverInvoker " + this.id + " doInvoker 异常...开始循环重试（如果还未开始的话）... 异常信息：" + e.getStackTrace());
            response = RetryHelper.beginRetryIfNeed(invocation, this);
        }
        return response;
    }

    private static class RetryHelper {

        private static final ConcurrentMap<String, Boolean> isStartMap =
                new ConcurrentHashMap<>();

        public static Response beginRetryIfNeed(ClientRequest invocation, FailoverInvoker failoverInvoker) throws Exception {
            String serviceName = invocation.getServiceType();
            Boolean isStart = isStartMap.get(serviceName);
            if (isStart != null && isStart == true) {
//                已经有其他invoker对当前service开始重试，只需要返回空代表着当前invoker重试失败即可
                System.out.println("RetryHelper 已经有其他invoker对当前service开始重试，只需要返回空代表着当前invoker重试失败即可");
                return null;
            } else {
//                开始重试
                isStart = true;
                isStartMap.put(serviceName, isStart);
                List<Invoker> copy = InvokerManager.get(serviceName);
                copy.remove(failoverInvoker);

//                todo retries需要配置进来
                int retries = 2;
                if (copy.size() < retries) {
//                    如果invoker数量不足以重试次数，
                    retries = copy.size();
                    if (retries <= 0) {
//                        重试失败
                        return null;
                    }
                }
                for (int i = 0; i < retries; i++) {
                    Invoker invoker = LoadBalanceManager.select(serviceName, copy, invocation);
                    Response response = null;
                    try {
                        response = invoker.invoke(invocation);
                    } catch (Exception e) {
                        System.out.println("RetryHelper 重试失败...");
                    }
                    if (response != null) {
//                        重试成功，返回给最初调用beginRetryIfNeed方法的invoker
                        return response;
                    }
                    copy.remove(invoker);
                }
            }
            return null;
        }
    }
}
