package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;

public abstract class AbstractInvoker<T> implements Invoker<T> {


//    @Override
//    public Class<T> getInterface() {
//        return null;
//    }

    private ClientRequest invocation;

    @Override
    public ClientRequest getInvocation() {
        return invocation;
    }

    @Override
    public void setInvocation(ClientRequest invocation) {
        this.invocation = invocation;
    }

    @Override
    public Response invoke(ClientRequest invocation) throws Exception {
        return doInvoke(invocation);
    }

    protected abstract Response doInvoke(ClientRequest invocation) throws Exception;
}
