package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;

/**
 * 几种调用方式：
 *  oneway调用，只发送请求，不接收返回结果 //RpcContext中如此描述。
 *  isAsync: 异步调用
 *  sync: 本地调用（同步调用），线程等待返回结果
 * @param <T>
 */
public interface Invoker<T> {

//    Class<T> getInterface();

    Response invoke(ClientRequest invocation) throws Exception;

    ClientRequest getInvocation();

    void setInvocation(ClientRequest invocation);
}
