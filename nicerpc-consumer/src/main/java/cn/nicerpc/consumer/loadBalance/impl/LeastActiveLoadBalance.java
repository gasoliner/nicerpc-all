package cn.nicerpc.consumer.loadBalance.impl;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.loadBalance.AbstractLoadBalance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class LeastActiveLoadBalance extends AbstractLoadBalance {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * 最小活跃数负载均衡策略
     * 有可能有相同的最小活跃数的invoker
     * 这时候就需要使用其权重
     *
     * @param servers
     * @param request
     * @return
     */
    @Override
    protected Invoker doSelect(List<Invoker> servers, ClientRequest request) {
        int length = servers.size();
        int leastCount = 0;
        int leastActives = -1;
        int totalWeight = 0;
        int firstWeight = 0;
        int[] leastArray = new int[length];
        boolean sameWeight = true;

        for (int i = 0; i < length; i++) {
            Invoker invoker = servers.get(i);
            int weight = getInvokerWeight(invoker);
            int actives = getInvokerActive(invoker, request.getMethodName());
            if (actives < leastActives || leastActives == -1) {
                leastActives = actives;
                leastCount = 1;
                leastArray[0] = i;
                totalWeight = weight;
                firstWeight = weight;
                sameWeight = true;
            } else if (actives == leastActives) {
                leastArray[leastCount++] = i;
                totalWeight += weight;
                if (sameWeight && i > 0 && weight != firstWeight) {
                    sameWeight = false;
                }
            }
        }
        if (leastCount == 1) {
            return servers.get(leastArray[0]);
        }
        if (totalWeight > 0 && !sameWeight) {
            int r = random.nextInt(totalWeight);
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastArray[i];
                r -= getInvokerWeight(servers.get(leastIndex));
                if (r < 0) {
                    return servers.get(leastIndex);
                }
            }
        }
        return servers.get(leastArray[random.nextInt(leastCount)]);
    }

    private int getInvokerActive(Invoker invoker, String methodName) {
        ClientRequest invocation = invoker.getInvocation();
        Map<String, AtomicInteger> methodActiveMap = invocation.getMethodActiveMap();
        int actives = 0;
        if (methodActiveMap != null) {
            AtomicInteger activesAtomic = methodActiveMap.get(methodName);
            if (activesAtomic != null) {
                actives = activesAtomic.get();
            }
        }
        return actives;
    }
}
