- 客户端超时，超时的链接自动关闭
- 分离业务模块
- 增加zk模块，从zk获取服务器列表
- 客户端动态管理连接
- Netty实现RPC服务器
- 定义自己的简单通信协议
- Client与RPC服务器使用长连接进行异步通信
- 客户端动态代理使用SpringCGLib，BeanPostProcessor接口
- 服务器注册到Zookeeper，客户端通过Zookeeper监听服务器状态
## dubbo
- 自动发现: 基于注册中心目录服务，使服务消费方能动态的查找服务提供方，使地址透明，使服务提供方可以平滑增加或减少机器。
- 集群容错: 提供基于接口方法的透明远程过程调用，包括多协议支持，以及软负载均衡，失败容错，地址路由，动态配置等集群支持。
- 远程通讯: 提供对多种基于长连接的NIO框架抽象封装，包括多种线程模型，序列化，以及“请求-响应”模式的信息交换方式。
  
## Netty
### 关于addListener方法
- 经过测试，channelFuture.channel().xxx().addListener(listener)
方法添加的监听器只对单种IO事件的单次操作有效。
- 比如，channelFuture.channel().connect().addListener()添加的监听器只会在connect完成后调用，不会在...channel().writeAndFlush()完成后调用
- 再比如，channelFuture.channel().writeAndFlush().addListener()添加的监听器只会在本次writeAndFlush()完成后执行，假如接着执行了另一次writeAndFlush()操作，该监听器不会再次触发
- ps：监听器会在isDone()返回true后被立即调用
### 关于sync方法
- sync会阻塞到当前IO操作直到其isDone()返回true。
- isDone()代表着完成了，结果可能是成功，失败，被中断等。
- 该IO操作是否成功还是得看isSuccess()方法。
### 关于await()方法
- await(),await(long,TimeUnit),awaitUnInterrupt等方法，底层调用了Object的wait方法，他会阻塞到isDone返回true，上面说到了，isDone返回true有多种结果，不一定成功。
- 当设置了超时时间时，只要到达了超时时间，就不管isDone是否返回true了，会直接返回。

### 关于bootstrap的connect方法
- 经测试，使用同一个bootstrap去连接同一个主机上的同一个进程（ip、port）都相同，返回的是不同的两个future（也就是两个连接），二者可以并发的读写数据。