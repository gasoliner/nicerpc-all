package cn.nicerpc.registry.impl;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.zk.ZookeeperFactory;
import cn.nicerpc.registry.support.AbstractRegistry;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;


public class DefaultRegistry extends AbstractRegistry {

    CuratorFramework client = ZookeeperFactory.create();

    @Override
    public void doRegister(ClientRequest request) {

        String parentPath = Constants.SERVER_PATH + "/" + request.getServiceType();

        try {
            checkServicePathIsExist(parentPath);
        } catch (Exception e) {
        }

        if (request.getCategory().equals("provider")) {


            try {
//                注册自己代表的服务
                client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(parentPath + "/providers/" + request.getHost() + "#" + request.getPort() + "#"
                        , JSON.toJSONString(request).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (request.getCategory().equals("consumer")) {

            /**
             * 注册当前consumer到consumers节点
             */
            try {
                client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(parentPath + "/consumers/" + request.getHost() + "#");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkServicePathIsExist(String parentPath) throws Exception {
        if (client.checkExists().forPath(parentPath) == null) {
            prepareZkDirectory(parentPath);
        }
    }

    private void prepareZkDirectory(String parentPath) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .forPath(parentPath + "/consumers");
        client.create().withMode(CreateMode.PERSISTENT)
                .forPath(parentPath + "/providers");
        client.create().withMode(CreateMode.PERSISTENT)
                .forPath(parentPath + "/routers");
        client.create().withMode(CreateMode.PERSISTENT)
                .forPath(parentPath + "/configurators");
    }

    @Override
    protected void doUnRegister(ClientRequest request) {

    }
}
