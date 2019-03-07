package cn.nicerpc.consumer.proxy;

import cn.nicerpc.consumer.core.RpcRun;
import cn.nicerpc.consumer.invoke.InvokerManager;
import cn.nicerpc.consumer.core.TCPClient;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import cn.nicerpc.consumer.invoke.Invoker;
import cn.nicerpc.consumer.loadBalance.LoadBalance;
import cn.nicerpc.consumer.loadBalance.LoadBalanceManager;
import cn.nicerpc.consumer.filter.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;
import cn.nicerpc.common.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {

        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f :
                fields) {
            if (f.isAnnotationPresent(RemoteInvoke.class)) {
                f.setAccessible(true);

                String serviceName = f.getType().getName();
                RemoteInvoke remoteInvoke = f.getAnnotation(RemoteInvoke.class);
                Class[] filters = remoteInvoke.filter();
                Class loadBalance = remoteInvoke.loadBalance();
                Class invokeStrategy = remoteInvoke.invokeStrategy();
//                注册filter
                FilterManager.registerFilterIfNeed(serviceName,filters);
//                注册loadBalance
                LoadBalanceManager.registerLoadBalanceIfNeed(serviceName,loadBalance);
//                注册invoker
                InvokerManager.registerInvokerIfNeed(serviceName,invokeStrategy);




//                todo 改掉这种方式需要
                final Map<Method,Class> methodClassMap = new HashMap<>();
                putMethodClassMap(methodClassMap,f);

//                给每个标注了RemoteInvoke的成员变量设置一个代理类，在代理类的拦截方法中执行rpc
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{f.getType()});

//                在执行代理类方法时进行拦截
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                       /**一次rpc请求开始**/
//                        String serviceType = methodClassMap.get(method).getName();
                        String serviceType = o.getClass().getInterfaces()[0].getName();
//                        组装一次请求
                        ClientRequest request = new ClientRequest();
                        request.setCommand(serviceType + "." + method.getName());
                        if (objects.length != 0) {
                            request.setContent(objects[0]);
                        }
                        request.setMethodName(method.getName());
                        request.setServiceType(serviceType);
                        request.setCategory("consumers");

                        Response response = RpcRun.rpc(serviceType,request);
                        /**一次rpc请求结束**/
                        return response.getResult(method.getReturnType());
                    }
                });
                try {
                    f.set(o,enhancer.create());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return o;
    }

    private void putMethodClassMap(Map<Method, Class> methodClassMap, Field f) {
        Method[] methods = f.getType().getDeclaredMethods();
        for (Method m :
             methods) {
            methodClassMap.put(m,f.getType());
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
