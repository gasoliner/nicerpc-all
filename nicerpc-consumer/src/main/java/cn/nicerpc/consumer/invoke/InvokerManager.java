package cn.nicerpc.consumer.invoke;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.util.NetUtil;
import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.consumer.core.ServerWatcher;
import cn.nicerpc.registry.api.Registry;
import cn.nicerpc.registry.impl.DefaultRegistry;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerManager {

    private static final ConcurrentHashMap<String, Set<Invoker>> invokerManagerMap =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Class> invokerStrategyMap =
            new ConcurrentHashMap<>();

    private static Registry registry = new DefaultRegistry();

    private static final CuratorFramework client = ZookeeperFactory.create();


    public static List<Invoker> get(String serviceType) throws Exception {
        Set<Invoker> invokers = invokerManagerMap.get(serviceType);
        if (CollectionUtils.isEmpty(invokers)) {
//            一个invoker也没有，需要进行服务发现

//            服务发现开始
            invokers = discoverService(serviceType);
//            服务发现完毕

            invokerManagerMap.put(serviceType, invokers);

//            注册watcher
//            todo 注意，这里监听的是providers节点，暂时没有监听configuration节点和routers节点
            String zkPath = getZkPath(serviceType);
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
        return new ArrayList<>(invokers);
    }

    private static Set<Invoker> discoverService(String serviceType) throws Exception {
        String zkPath = getZkPath(serviceType);
        List<String> allProviders = client.getChildren().forPath(zkPath);
        if (allProviders == null || allProviders.size() == 0) {
            throw new RuntimeException("该服务没有provider，请检查。" + serviceType);
        }
        return doDiscoverService(allProviders,serviceType);

    }

    private static Set<Invoker> doDiscoverService(List<String> allProviders, String serviceType) throws Exception {
        String zkPath = getZkPath(serviceType);
        Set<Invoker> invokers = new HashSet<>();
        Class invokerStrategy = invokerStrategyMap.get(serviceType);
        if (invokerStrategy == null) {
            throw new Exception("invokerManager invokerStrategy must not be null");
        }
        for (String seq :
                allProviders) {
            byte[] serverInfoBytes = client.getData().forPath(zkPath + "/" + seq);
            ClientRequest serverInfo = JSON.parseObject(new String(serverInfoBytes), ClientRequest.class);

            Invoker invoker = (Invoker) invokerStrategy.newInstance();
            ClientRequest invocation = new ClientRequest();
            invocation.setServiceType(serviceType);
            invocation.setHost(serverInfo.getHost());
            invocation.setPort(serverInfo.getPort());
            invocation.setParameters(serverInfo.getParameters());
            invocation.setMethodActiveMap(new ConcurrentHashMap<>());
            invoker.setInvocation(invocation);
            invoker.setId(serverInfo.getHost() + "#" + serverInfo.getPort() + "#" + serviceType);

            invokers.add(invoker);
        }
        return invokers;
    }

    private static Set<Invoker> reDiscoverService(List<String> allProviders, String serviceType) throws Exception {
        return doDiscoverService(allProviders,serviceType);
    }

    private static String getZkPath(String serviceType) {
        return Constants.SERVER_PATH + "/" + serviceType + "/providers";
    }

    public static void registerInvokerIfNeed(String serviceName, Class invokeStrategy) {
        if (invokerStrategyMap.containsKey(serviceName)) {
            return;
        }
        invokerStrategyMap.put(serviceName, invokeStrategy);
    }

    public static void updateInvoker(final String serviceType, final List<String> serverPaths) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " " + " start updateInvoker");
                try {
                    reDiscoverService(serverPaths,serviceType);
                } catch (Exception e) {
                    System.out.println("InvokerManager updateInvoker failed. ");
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " " + " end updateInvoker");
            }
        };
        thread.start();
    }
}
