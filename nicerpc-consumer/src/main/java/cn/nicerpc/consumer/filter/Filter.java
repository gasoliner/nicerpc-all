package cn.nicerpc.consumer.filter;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.invoke.Invoker;

public interface Filter {
    /**
     * 过滤器
     * @param invoker 执行用的invoker
     * @param invocation 本次执行rpc封装参数的ClientRequest
     * @return
     */
    Response invoke(Invoker<?> invoker, ClientRequest invocation);
}
