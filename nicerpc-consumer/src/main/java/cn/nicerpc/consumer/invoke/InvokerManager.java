package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.util.NetUtil;
import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.consumer.core.ServerWatcher;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.registry.api.Registry;
import cn.nicerpc.registry.impl.DefaultRegistry;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerManager {

    private static final ConcurrentHashMap<String, List<Invoker>> invokerManagerMap =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Class> invokerStrategyMap =
            new ConcurrentHashMap<>();

    private static Registry registry = new DefaultRegistry();


    public static List<Invoker> get(String serviceType) throws Exception {
        List<Invoker> invokers = invokerManagerMap.get(serviceType);
        if (CollectionUtils.isEmpty(invokers)) {
//            一个invoker也没有，需要进行服务发现

//            服务发现,需要做给这个服务的消费者做zk节点初始化并注册watcher
            CuratorFramework client = ZookeeperFactory.create();
            String zkPath = Constants.SERVER_PATH + "/" + serviceType + "/providers";
            List<String> allProviders = client.getChildren().forPath(zkPath);
            if (allProviders == null || allProviders.size() == 0) {
                throw new RuntimeException("该服务没有provider，请检查。" + serviceType);
            }
            invokers = new ArrayList<>();
            Class invokerStrategy = invokerStrategyMap.get(serviceType);
            if (invokerStrategy == null) {
                throw new Exception("invokerManager invokerStrategy must not be null");
            }
            for (String seq :
                    allProviders) {
                String[] serverInfoArray = seq.split("#");
                Invoker invoker = (Invoker) invokerStrategy.newInstance();
                ClientRequest invocation = new ClientRequest();
                invocation.setServiceType(serviceType);
                invocation.setHost(serverInfoArray[0]);
                invocation.setPort(Integer.valueOf(serverInfoArray[1]));
                invoker.setInvocation(invocation);

                invokers.add(invoker);
            }
            invokerManagerMap.put(serviceType,invokers);
//            服务发现完毕

//            注册watcher
//            todo 注意，这里监听的是providers节点，暂时没有监听configuration节点和routers节点
            ServerWatcher watcher = new ServerWatcher();
            client.getChildren().usingWatcher(watcher).forPath(zkPath);
//            注册自己到consumer节点
            ClientRequest request = new ClientRequest();
            request.setServiceType(serviceType);
            request.setCategory("consumer");
            request.setHost(NetUtil.getHostAddr());
            registry.register(request);
            System.out.println("consumer注册自己到consumers节点上 " + JSON.toJSONString(request));
        }
        return invokers;
    }

    public static void registerInvoker(String serviceName, Class invokeStrategy) {
        if (!invokerStrategyMap.containsKey(serviceName)) {
            invokerStrategyMap.put(serviceName, invokeStrategy);
        }
    }
}
