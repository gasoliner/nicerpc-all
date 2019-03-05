package cn.nicerpc.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemoteInvoke {
    Class[] filter();
    Class loadBalance();
    Class invokeStrategy();
}
