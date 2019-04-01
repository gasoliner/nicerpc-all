package cn.nicerpc.nicerpc_demo.consumer.filter;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.filter.Filter;
import cn.nicerpc.consumer.invoke.Invoker;
import com.alibaba.fastjson.JSON;

public class CheckInvocationFilter implements Filter {
    @Override
    public Response invoke(Invoker<?> invoker, ClientRequest invocation) throws Exception {
        System.out.println(JSON.toJSONString(invocation));
        System.out.println("check invocation over");
        return invoker.invoke(invocation);
    }
}
