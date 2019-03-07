package cn.nicerpc.provider.medium;

import cn.nicerpc.common.constant.Constants;
import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.provider.init.NettyInital;
import cn.nicerpc.registry.api.Registry;
import cn.nicerpc.registry.impl.DefaultRegistry;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import cn.nicerpc.common.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 中介者模式
 *  netty收到请求后，将请求交给中介进行处理
 */
@Component
public class InitalMedium implements BeanPostProcessor {

    Registry registry = new DefaultRegistry();

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

//        我们这个类上是否有controller注解
        if (o.getClass().isAnnotationPresent(Remote.class)) {
            String serviceType = o.getClass().getInterfaces()[0].getName();
            Method[] methods = o.getClass().getDeclaredMethods();

            Map<String, BeanMethod> beanMethodMap = Media.beanMethodMap;
//            保存其所有的Method，以便于后面使用
            for (Method method :
                    methods) {

//                本地保存bean和method
                String beanMethodName = o.getClass().getInterfaces()[0].getName() + "." + method.getName();

                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(o);
                beanMethod.setMethod(method);
                beanMethodMap.put(beanMethodName,beanMethod);

            }
//            Zookeeper注册相应节点
            ClientRequest request = new ClientRequest();
            request.setCategory("provider");
            request.setServiceType(serviceType);
            request.setHost(NettyInital.host);
            request.setPort(NettyInital.port);
            Map<String,String> params = new HashMap<>();
            params.put(Constants.PARAM_WEIGHT, String.valueOf(readNum("服务 " + JSON.toJSONString(request) + " 请为其设置权重(0-100)：")));
            request.setParameters(params);

            registry.register(request);
            System.out.println("InitalMedium 发现服务并暴露成功 " + JSON.toJSONString(request));
        }
        return o;
    }

    private int readNum(String msg) {
        System.out.println(msg);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
