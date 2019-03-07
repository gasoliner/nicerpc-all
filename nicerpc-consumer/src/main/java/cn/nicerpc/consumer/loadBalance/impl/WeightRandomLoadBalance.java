package cn.nicerpc.consumer.loadBalance.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.loadBalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightRandomLoadBalance extends AbstractLoadBalance {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * @param servers sorted by weight inc
     * @param request
     * @return
     */
    @Override
    protected Invoker doSelect(List<Invoker> servers, ClientRequest request) {
        int length = servers.size();
        int totalWeight = -1;
        int[] weightArr = new int[length];
        boolean sameWeight = true;
        for (int i = 0; i < length; i++) {
            Invoker invoker = servers.get(i);
            int weight = getInvokerWeight(invoker);
            weightArr[i] = weight;
            totalWeight += weight;
            if (sameWeight && i > 0 && weight != getInvokerWeight(servers.get(i - 1))) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            int r = random.nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                r -= weightArr[i];
                if (r < 0) {
                    return servers.get(i);
                }
            }
        }
        return servers.get(random.nextInt(length));
    }
}
