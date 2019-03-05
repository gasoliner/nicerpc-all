package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalanceManager {
    private static final ConcurrentHashMap<String, LoadBalance> loadBalanceManagerMap =
            new ConcurrentHashMap<>();

    public static void registerLoadBalance (String serviceName, Class loadBalance) {
        if (loadBalanceManagerMap.containsKey(serviceName)) {
            return;
        }
        try {
            loadBalanceManagerMap.put(serviceName, (LoadBalance) loadBalance.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Invoker select(String serviceType, List<Invoker> invokers, ClientRequest request) {
        LoadBalance loadBalance = loadBalanceManagerMap.get(serviceType);
        Invoker select = loadBalance.select(invokers, request);
        return select;
    }
}
