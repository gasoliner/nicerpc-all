package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;

public abstract class AbstractInvoker<T> implements Invoker<T> {


//    @Override
//    public Class<T> getInterface() {
//        return null;
//    }

    private ClientRequest invocation;

    /**
     * 一个invoker的唯一标识
     *  格式：
     *      host#port#serviceName
     */
    protected String id;

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

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 重新hashcode
     * @return
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * 重写equals
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        AbstractInvoker invoker = (AbstractInvoker) obj;
        return this.id.equals(invoker.id);
    }

    protected abstract Response doInvoke(ClientRequest invocation) throws Exception;

}
