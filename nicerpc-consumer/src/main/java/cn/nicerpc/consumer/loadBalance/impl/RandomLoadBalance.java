package cn.nicerpc.consumer.loadBalance.impl;

import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.loadBalance.AbstractLoadBalance;
import cn.nicerpc.common.param.ClientRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected Invoker doSelect(List<Invoker> servers, ClientRequest request) {
        int size = servers.size();
        return servers.get(random.nextInt(size));
    }
}
