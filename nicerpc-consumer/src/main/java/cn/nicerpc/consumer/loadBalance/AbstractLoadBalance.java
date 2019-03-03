package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.param.ClientRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String select(List<String> servers, ClientRequest request) {
        if (servers == null || servers.size() == 0) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        return doSelect(servers, request);
    }

    protected abstract String doSelect(List<String> servers, ClientRequest request);


}
