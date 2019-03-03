package cn.nicerpc.registry.support;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.registry.api.Registry;

public abstract class AbstractRegistry implements Registry {

    @Override
    public void register(ClientRequest request) {
        if (request == null) {
            return;
        }
        doRegister(request);
    }

    public abstract void doRegister(ClientRequest request);

    @Override
    public void unRegister(ClientRequest request) {
        if (request == null) {
            return;
        }
        doUnRegister(request);
    }

    protected abstract void doUnRegister(ClientRequest request);
}
