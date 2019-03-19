package cn.nicerpc.nicerpc_demo.consumer.filter;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.filter.Filter;
import cn.nicerpc.consumer.invoke.Invoker;
import com.alibaba.fastjson.JSON;

public class TestFilter implements Filter {
    @Override
    public Response invoke(Invoker<?> invoker, ClientRequest invocation) throws Exception {
        System.out.println("testFilter invoked... print invocation " + JSON.toJSONString(invocation));
        return invoker.invoke(invocation);
    }
}
