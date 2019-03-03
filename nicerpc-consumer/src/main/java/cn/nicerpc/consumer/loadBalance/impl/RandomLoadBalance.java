package cn.nicerpc.consumer.loadBalance.impl;

import cn.nicerpc.consumer.loadBalance.AbstractLoadBalance;
import cn.nicerpc.common.param.ClientRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected String doSelect(List<String> servers, ClientRequest request) {
        int size = servers.size();
        return servers.get(random.nextInt(size));
    }
}
