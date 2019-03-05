package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;

import java.util.List;

public interface LoadBalance {

    Invoker select(List<Invoker> servers, ClientRequest request);

}
