package cn.nicerpc.consumer.filter;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.invoke.Invoker;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FilterManager {
    private static final ConcurrentHashMap<String, List<Filter>> filterManagerMap =
            new ConcurrentHashMap<>();

    public static void registerFilter(String serviceName, Class[] filters) {
        if (filterManagerMap.containsKey(serviceName)) {
            return;
        }
        List<Filter> filterList = new ArrayList<>();
        try {
            for (Class temp :
                    filters) {
                Filter filter = (Filter) temp.newInstance();
                filterList.add(filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterManagerMap.put(serviceName,filterList);
    }

    public static Invoker buildFilterChain(String serviceType, Invoker invoker) throws Exception {
        List<Filter> filters = filterManagerMap.get(serviceType);
        if (CollectionUtils.isEmpty(filters)) {
            throw new Exception("FilterManager buildFilterChain filters must not be null !");
        }
        Invoker last = invoker;
        for (int i = filters.size() - 1; i >= 0; i--) {
            final Filter filter = filters.get(i);
            /**
             * 从后往前遍历过滤器
             * 设置next/last指针，
             * 单向链表指针传递，
             * 使用装饰器模式，将一个个过滤器包装成Invoker，形成过滤链，
             * 最后last指向第一个过滤器包装成的Invoker
             */
            Invoker next = last;
            last = new Invoker() {
                @Override
                public Response invoke(ClientRequest invocation) {
                    return filter.invoke(next,invocation);
                }

                @Override
                public ClientRequest getInvocation() {
                    return null;
                }

                @Override
                public void setInvocation(ClientRequest invocation) {
                }
            };
        }
        return last;
    }
}
