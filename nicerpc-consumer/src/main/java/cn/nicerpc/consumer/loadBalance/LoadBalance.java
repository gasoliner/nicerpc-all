package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.param.ClientRequest;

import java.util.List;

public interface LoadBalance {

    String select(List<String> servers, ClientRequest request);

}
