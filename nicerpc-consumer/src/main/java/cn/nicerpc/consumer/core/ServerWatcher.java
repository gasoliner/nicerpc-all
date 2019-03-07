package cn.nicerpc.consumer.core;

import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.consumer.invoke.InvokerManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;

public class ServerWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        CuratorFramework client = ZookeeperFactory.create();
        String path = watchedEvent.getPath();
        client.getChildren().usingWatcher(this);

        String serviceType = path.split("/")[1];

        System.out.println("ServerWatcher process serviceType == " + serviceType);
        List<String> serverPaths = client.getChildren().forPath(path);
//        ServerManager.clear(serviceType);

        InvokerManager.updateInvoker(serviceType,serverPaths);
    }
}
