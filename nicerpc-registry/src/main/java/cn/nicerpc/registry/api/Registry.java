package cn.nicerpc.registry.api;

import cn.nicerpc.common.param.ClientRequest;

public interface Registry {

    void register(ClientRequest request);

    void unRegister(ClientRequest request);

}
