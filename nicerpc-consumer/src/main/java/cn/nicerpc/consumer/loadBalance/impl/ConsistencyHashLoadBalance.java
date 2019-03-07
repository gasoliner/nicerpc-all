package cn.nicerpc.consumer.loadBalance.impl;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.loadBalance.AbstractLoadBalance;

import java.util.List;

public class ConsistencyHashLoadBalance extends AbstractLoadBalance {
    @Override
    protected Invoker doSelect(List<Invoker> servers, ClientRequest request) {
        return null;
    }
}
