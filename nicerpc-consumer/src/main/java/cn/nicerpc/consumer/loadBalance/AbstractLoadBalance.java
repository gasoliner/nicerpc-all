package cn.nicerpc.consumer.loadBalance;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.consumer.invoke.Invoker;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class AbstractLoadBalance implements LoadBalance {

    protected static final int DEFAULT_WEIGHT = 0;

    @Override
    public Invoker select(List<Invoker> invokers, ClientRequest request) {
        if (invokers == null || invokers.size() == 0) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        Collections.sort(invokers, new Comparator<Invoker>() {
            @Override
            public int compare(Invoker o1, Invoker o2) {
                Integer o1Weight = getInvokerWeight(o1);
                Integer o2Weight = getInvokerWeight(o2);
                return o1Weight.compareTo(o2Weight);
            }
        });
        return doSelect(invokers, request);
    }

    protected abstract Invoker doSelect(List<Invoker> servers, ClientRequest request);

    protected int getInvokerWeight(Invoker invoker) {
        ClientRequest invocation = invoker.getInvocation();
        Map<String, String> parameters = invocation.getParameters();
        int weight = DEFAULT_WEIGHT;
        if (parameters != null) {
            String weightStr = parameters.get(Constants.PARAM_WEIGHT);
            if (StringUtils.isNotEmpty(weightStr)) {
                weight = Integer.valueOf(weight);
            }
        }
        return weight;
    }
}
