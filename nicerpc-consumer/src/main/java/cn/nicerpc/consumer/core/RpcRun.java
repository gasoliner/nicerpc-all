package cn.nicerpc.consumer.core;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.filter.FilterManager;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.invoke.InvokerManager;
import cn.nicerpc.consumer.loadBalance.LoadBalanceManager;

import java.util.List;

public class RpcRun {

    public static Response rpc(String serviceType, ClientRequest request) throws Exception {
//        获取invoker
        List<Invoker> invokers = InvokerManager.get(serviceType);
//        负载均衡
        Invoker invoker = LoadBalanceManager.select(serviceType,invokers,request);
//        构建filter链
        invoker = FilterManager.buildFilterChain(serviceType,invoker);
        Response response = invoker.invoke(request);
        return response;
    }

}
