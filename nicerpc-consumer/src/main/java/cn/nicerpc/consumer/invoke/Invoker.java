package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;

public interface Invoker<T> {

    Class<T> getInterface();

    Response invoke(ClientRequest invocation);
}
