package cn.nicerpc.consumer.proxy;

import cn.nicerpc.consumer.core.TCPClient;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;
import cn.nicerpc.common.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {

        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f :
                fields) {
            if (f.isAnnotationPresent(RemoteInvoke.class)) {
                f.setAccessible(true);

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
//                        采用netty客户端调用服务器
                        ClientRequest request = new ClientRequest();
                        request.setCommand(methodClassMap.get(method).getName() + "." + method.getName());
//                        todo 多个参数应该用map，应该把invoke抽象出来
                        if (objects.length != 0) {
                            request.setContent(objects[0]);
                        }
                        request.setMethodName(method.getName());
                        request.setServiceType(methodClassMap.get(method).getName());
                        request.setCategory("consumers");
                        Response response = TCPClient.send(request);

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
