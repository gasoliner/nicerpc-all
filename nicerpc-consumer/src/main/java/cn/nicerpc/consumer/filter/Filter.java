package cn.nicerpc.consumer.filter;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.invoke.Invoker;

public interface Filter {
    Response invoke(Invoker<?> invoker, ClientRequest invocation);
}
