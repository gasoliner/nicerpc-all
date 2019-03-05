package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public Invoker select(List<Invoker> invokers, ClientRequest request) {
        if (invokers == null || invokers.size() == 0) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers, request);
    }

    protected abstract Invoker doSelect(List<Invoker> servers, ClientRequest request);


}
