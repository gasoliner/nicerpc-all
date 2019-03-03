package cn.nicerpc.provider.medium;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.ServerRequest;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {

    public static Map<String,BeanMethod> beanMethodMap;

    static {
        beanMethodMap = new HashMap<String,BeanMethod>();
    }

    private static Media m = new Media();

    private Media() {}

    public static Media newInstance() {
        return m;
    }

    /**
     * 反射处理业务代码
     * @param request
     * @return
     */
    public Object process(ClientRequest request) {
        String key = request.getServiceType() + "." + request.getMethodName();
        BeanMethod beanMethod = beanMethodMap.get(key);
        if (beanMethod == null) {
            return null;
        }
        Object bean = beanMethod.getBean();
        Method method = beanMethod.getMethod();

        Object result = null;
//        todo 只支持一个参数
        if (method.getParameterTypes().length != 0) {
            Class<?> parameterType = method.getParameterTypes()[0];
            //content的类型为com.alibaba.fastjson.JSONObject
            Object content = request.getContent();

            //args类型为cn.wan.user.bean.User
            Object args = JSON.parseObject(JSON.toJSONString(content),parameterType);

            try {
                result = method.invoke(bean,args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                result = method.invoke(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
