//package cn.nicerpc.consumer.core;
//
//import cn.nicerpc.common.constant.Constants;
//import cn.nicerpc.common.util.FutureUtil;
//import cn.nicerpc.common.util.NetUtil;
//import cn.nicerpc.common.zk.ZookeeperFactory;
//import cn.nicerpc.consumer.loadBalance.LoadBalance;
//import cn.nicerpc.consumer.loadBalance.impl.RandomLoadBalance;
//import cn.nicerpc.common.param.ClientRequest;
//import cn.nicerpc.registry.api.Registry;
//import cn.nicerpc.registry.impl.DefaultRegistry;
//import com.alibaba.fastjson.JSON;
//import io.netty.channel.ChannelFuture;
//import org.apache.curator.framework.CuratorFramework;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class ServerManager {
//
//    private static Registry registry = new DefaultRegistry();
//
//    private static CuratorFramework client = ZookeeperFactory.create();
//
//    private static LoadBalance loadBalance = new RandomLoadBalance();
//
//    private static AtomicInteger pollingIndex = new AtomicInteger(0);
//
//    /**
//     * 保存serviceName与当前主机列表的映射
//     * key:com.nicerpc.nicerpc_demo.api.UserService
//     * value:set{"192.168.1.2#8081","192.168.11.68#8081"}
//     */
//    private static final ConcurrentHashMap<String, Set<String>> realServerPathMap =
//            new ConcurrentHashMap<>();
//
//    /**
//     * 保存serviceName与当前使用的provider所在的主机+端口的映射
//     * key:com.nicerpc.nicerpc_demo.api.UserService
//     * value:host#port
//     */
//    private static final ConcurrentHashMap<String, String> hostAndPortManagerMap =
//            new ConcurrentHashMap<>();
//
//    /**
//     * 保存与某台主机上的一个进程（这个进程可能有多个服务）的一条链接
//     * key:host#port
//     * value:ChannelFuture
//     */
//    private static final ConcurrentHashMap<String, ChannelFuture> connectionManagerMap =
//            new ConcurrentHashMap<>();
//
//    public static void removeServer(String serviceType, String server) {
//        Set<String> serverSet = realServerPathMap.get(serviceType);
//        if (serverSet == null) {
//            return;
//        }
//        serverSet.remove(server);
//    }
//
//    public static void addServer(String serviceType, String server) {
//        Set<String> serverSet = realServerPathMap.get(serviceType);
//        if (serverSet == null) {
//            serverSet = new HashSet<>();
//            realServerPathMap.put(serviceType, serverSet);
//        }
//        serverSet.add(server);
//    }
//
//    public static void clear(String serviceType) {
//        realServerPathMap.remove(serviceType);
//    }
//
//    public static void clearAll() {
//        realServerPathMap.clear();
//    }
//
//    public static ChannelFuture get(ClientRequest request) throws Exception {
//
//        String serviceType = request.getServiceType();
//        String hostAndPort = hostAndPortManagerMap.get(serviceType);
//        if (hostAndPort == null) {
////            代表没有连接过
////            需要进行服务发现
//
////            服务发现
////            需要做给这个服务的消费者做zk节点初始化并注册watcher
//            String zkPath = Constants.SERVER_PATH + "/" + serviceType + "/providers";
//            List<String> allProviders = client.getChildren().forPath(zkPath);
//            if (allProviders == null || allProviders.size() == 0) {
//                throw new RuntimeException("该服务没有provider，请检查。" + JSON.toJSONString(request));
//            }
//            for (String seq :
//                    allProviders) {
//                String[] serverInfoArray = seq.split("#");
//                addServer(serviceType, serverInfoArray[0] + "#" + serverInfoArray[1]);
//            }
//
////            注册watcher
////            todo 注意，这里监听的是providers节点，暂时没有监听configuration节点和routers节点
//            ServerWatcher watcher = new ServerWatcher();
//            client.getChildren().usingWatcher(watcher).forPath(zkPath);
////            注册自己到consumer节点
//            request.setCategory("consumer");
//            request.setHost(NetUtil.getHostAddr());
//            registry.register(request);
//            System.out.println("consumer注册自己到consumers节点上 " + JSON.toJSONString(request));
//
////            负载均衡
//            hostAndPort = getSelectByLoadBalance(serviceType, request);
//
////            todo 这里是初始连接，最好负载均衡选举出来之后直接进行连接，要不然进入到getConnection方法里，得到的future一定为空，则又会进行一次负载均衡选举
////            todo 相当于在初始连接时，一定会浪费一次负载均衡，所以当负载均衡的代价变大时，应考虑修改此处。
////            todo 一定要改！以为在某些负载均衡算法下，某台主机可能永远无法被调用
//
////            hostAndPortManagerMap.put(key, select);
////
//////            查看有无可以使用的连接
////            ChannelFuture future = connectionManagerMap.get(select);
////
////            if (future != null && future.channel().isActive()) {
//////                如果可用的话直接返回
////                return future;
////            }
////            /**
////             * 证明future == null 或者 future不活跃
////             * future == null 代表着：没连接过服务器
////             * future 不活跃 代表着：连接过服务器，服务器连接过期
////             */
////            String[] serverInfo = select.split("#");
////            ChannelFuture connection = TCPClient.b.connect(serverInfo[0], Integer.parseInt(serverInfo[1]));
////            /**
////             * 如果之前是future为空，则需要新建一个连接并放进mangerMap里保存
////             * 如果之前是future不Active的话，同样需要放进managerMap里覆盖之前的
////             */
////            connectionManagerMap.put(select, connection);
////            return getConnection(hostAndPort, serviceType, request);
//        }
//        return getConnection(hostAndPort, serviceType, request);
//    }
//
//    private static ChannelFuture getConnection(String hostAndPort, String serviceType, ClientRequest request) throws Exception {
////        查看有无可以使用的连接
//        ChannelFuture future = connectionManagerMap.get(hostAndPort);
//        if (future != null && future.channel().isActive()) {
////                如果可用的话直接返回
//            System.out.println("对于serviceType = " + serviceType + " hostAndPort = " + hostAndPort + "存在连接，直接返回使用");
//            return future;
//        }
//
//        /**
//         * 如果没有的话，不管是future == null的情况，还是isNotActive的情况
//         * 都需要重新连接，原因如下所示：
//         * 经过思考，我觉得应该是重新使用负载均衡策略进行选举
//         * 因为负载均衡选举是根据最新的realServerPathMap进行的，所以他可以保证
//         * 选举出来的server都是可用的
//         * 假如重连当前的hostAndPort的话，如果该hostAndPort代表的主机进程关闭，
//         * 虽然realServerPathMap会更新，但我们在这里重连还是会失败，
//         * 所以应该以realServerPathMap为准。
//         *
//         * 然后又经过思考，我觉得应该在负载均衡前尝试重连一把，
//         * 因为realServerPathMap并不是跟真实的服务器情况同步更新的，
//         * 所以在真实的服务器情况同步的这段时间内，负载均衡可能每次负载的都是
//         * 同一台机器，而这台机器如果是已经下线的（但它的下线状态还没有被更新到realServerPathMap中），
//         * 可能会一直递归下去！
//         * 所以我们需要在负载均衡前，尝试重连接一次，如果重连接成功的话就返回，
//         * 如果失败的话，从realServerPathMap中remove掉，然后进行负载均衡，这样的话，
//         * 如果所有机器都宕掉，我们也会一台一台的将所有机器删除掉，从而最后在负载均衡的时候
//         * 发现realServerPathMap中为空，会抛出异常，终止，就不会无限的递归下去了。
//         *
//         * 然后又经过思考，因为查看connect是否成功的操作是异步的，所以没办法同步的执行“如果重连失败的话，就从realServerPathMap中
//         * 移除该server”这个操作。当然，future支持sync，await等操作，但是这些都需要阻塞，都会付出不小的代价，
//         * 会影响框架的性能。所以，我决定采用如下方案：
//         * 在通过connectionManagerMap.get(hostAndPort);获得的future不满足future != null && future.channel().isActive()的
//         * 条件之后，首先尝试关闭当前future，然后从realServerSet的副本中剔除当前Server，使用这个处理之后的副本进行fastGetConnection操作。
//         * fastGetConnection：
//         * 每次都使用形参serverSet进行负载均衡选举，并使用
//         * 选举结果进行连接，并使用返回的future.await(long,TimeUnit)设置等待超时时间，当await阻塞完毕后（不管future是否连接完毕），
//         * 检查是否连接成功，如果连接成功则返回，否则在传进来的serverSet中remove掉刚刚那个server，并递归调用fastGetConnection，
//         * 这样我们就可以通过对超时时间进行调优，快速的获取一个客户端了连接了，当遇到网络波动的时候，我们选取到的是一个连通最快的provider，而且
//         * serverSet.remove(server)这个操作是在副本上进行的，不会影响真实的server列表的更新。
//         *
//         * 然后又经过思考，决定在加一个措施，就是normalGetConnection，如果通过fastGetConnection获取不到（这种情况只会发生在
//         * 把serverSet副本掏空后，负载均衡选举发生异常的时候），会接着通过normalGetConnection获取。
//         * normalGetConnection就仅仅是根据当前的realServerSet列表做一个负载均衡选举，然后根据选举结果直接进行异步连接，不管连接结果
//         * 是不是成功
//         *
//         * ===存在的问题===：
//         * await超时时间的设置多少合适，而且也不清楚使用await带来的性能损耗
//         *
//         */
//        System.out.println("getConnection 发现future没法用，因为 ：");
//        if (future == null) {
//            System.out.println("***" + "future 为空");
//        } else {
//            System.out.println("***" + "future 不为空");
//            if (future.channel() == null) {
//                System.out.println("***" + "future.channel() 为空");
//            } else {
//                System.out.println("***" + "future.channel() 不为空");
//                System.out.println("***" + "future.channel().isActive() == " + future.channel().isActive());
//            }
//        }
//
//
//        FutureUtil.closeQuietly(future);
//
//        Set<String> copy = new HashSet<>(realServerPathMap.get(serviceType));
//        copy.remove(hostAndPort);
//
//        GetConnResult getConnResult = fastGetConnection(copy, request);
//        if (!getConnResult.isSuccess) {
//            System.out.println("通过fastGetConnection获得future-失败！转而使用normalGetConnection");
//            getConnResult = normalGetConnection(serviceType, request);
//        }
//        future = getConnResult.future;
//
//        System.out.println("新的连接已建立，serviceType = " + serviceType + " select = " + getConnResult.select);
//        hostAndPortManagerMap.put(serviceType, getConnResult.select);
//        connectionManagerMap.put(getConnResult.select, future);
//
//        return future;
//    }
//
//    private static GetConnResult fastGetConnection(Set<String> copy, ClientRequest request) throws Exception {
//
//        String select = null;
////        负载均衡
//        try {
//            select = getSelectByLoadBalance(copy, request);
//        } catch (Exception e) {
//            System.out.println("ServerManager.fastGetConnection() 进行负载均衡时异常，应该是serverSet为空了，将返回一个空值");
//            return new GetConnResult(null, null, false);
//        }
//
//        ChannelFuture future = connectionManagerMap.get(select);
//        if (future != null && future.channel().isActive()) {
//            System.out.println("对于 hostAndPort = " + select + "存在连接，直接返回使用");
//            return new GetConnResult(select,future,true);
//        }
//
////        连接新select的服务器
//        String[] serverInfo = select.split("#");
//        long startTime = System.currentTimeMillis();
//        ChannelFuture connection = TCPClient.b.connect(serverInfo[0], Integer.parseInt(serverInfo[1]));
//
//        /**
//         * 超时时间设置为0.75秒
//         */
//        connection.await(750, TimeUnit.MILLISECONDS);
//
//        long spendTime = System.currentTimeMillis() - startTime;
//        System.out.println("fastGetConnection connect 花费时间为 " + spendTime);
//
//        /**
//         * 成功的话就返回
//         */
//        if (connection.isSuccess()) {
//            System.out.println("fastGetConnection 成功，返回select为 " + select);
//            return new GetConnResult(select, connection, true);
//        } else {
//            /**
//             * 失败的话就关闭
//             */
//            System.out.println("fastGetConnection 失败，递归调用下一次fastGetConnection ， 失败的select为" + select);
//            FutureUtil.closeQuietly(connection);
//            copy.remove(select);
//            return fastGetConnection(copy, request);
//        }
//    }
//
//    /**
//     * 正常的异步获取connection
//     * 只是根据当前server列表做一个负载均衡选举
//     * 然后做一个连接，不管连接结果，接着返回
//     *
//     * @return
//     */
//    private static GetConnResult normalGetConnection(String serviceType, ClientRequest request) throws Exception {
//        String select = null;
////        负载均衡
//
//        select = getSelectByLoadBalance(realServerPathMap.get(serviceType), request);
//
//        String[] serverInfo = select.split("#");
//        ChannelFuture connection = TCPClient.b.connect(serverInfo[0], Integer.parseInt(serverInfo[1]));
//        return new GetConnResult(select, connection, true);
//    }
//
//    private static String getSelectByLoadBalance(String serviceType, ClientRequest request) throws Exception {
//        return getSelectByLoadBalance(realServerPathMap.get(serviceType), request);
//    }
//
//    private static String getSelectByLoadBalance(Set<String> serverSet, ClientRequest request) throws Exception {
////        负载均衡算法
//        String select = loadBalance.select(new ArrayList<>(serverSet), request);
//        if (select == null) {
//            throw new Exception("负载均衡算法返回为空！！");
//        }
//        System.out.println("负载均衡选择的服务器编号： " + select);
//        return select;
//    }
//
//    private static class GetConnResult {
//        private String select;
//        private ChannelFuture future;
//        private Boolean isSuccess;
//
//        public GetConnResult(String select, ChannelFuture future, Boolean isSuccess) {
//            this.select = select;
//            this.future = future;
//            this.isSuccess = isSuccess;
//        }
//    }
//}
