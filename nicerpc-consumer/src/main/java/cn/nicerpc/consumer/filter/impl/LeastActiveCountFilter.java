package cn.nicerpc.consumer.filter.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.filter.Filter;
import cn.nicerpc.consumer.invoke.Invoker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * LeastActive算法
 * 最小活跃数
 * 给每个invoker设置一个活跃数，
 * 当服务调用之前活跃数加一
 * 当服务调用完后活跃数减一
 * 配置上对应的LeastActiveLoadBalance
 * 我们会选择当前活跃数最小的Invoker来进行工作
 * 这样就可以将任务交给处理速度快的机器去做，少给慢的机器一些
 */
public class LeastActiveCountFilter implements Filter {
    @Override
    public Response invoke(Invoker<?> invoker, ClientRequest invocation) {
        Response result = null;
        increActiveCount(invoker.getInvocation(), invocation.getMethodName());
        try {
            result = invoker.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            decreActiveCount(invoker.getInvocation(), invocation.getMethodName());
        }
        return result;
    }

    private void decreActiveCount(ClientRequest invocation, String methodName) {
        AtomicInteger actives = invocation.getMethodActiveMap().get(methodName);
        if (actives == null) {
            actives = new AtomicInteger(0);
            invocation.getMethodActiveMap().put(methodName,actives);
        }
        actives.decrementAndGet();
    }

    private void increActiveCount(ClientRequest invocation, String methodName) {
        AtomicInteger actives = invocation.getMethodActiveMap().get(methodName);
        if (actives == null) {
            actives = new AtomicInteger(0);
            invocation.getMethodActiveMap().put(methodName,actives);
        }
        actives.incrementAndGet();
    }
}
