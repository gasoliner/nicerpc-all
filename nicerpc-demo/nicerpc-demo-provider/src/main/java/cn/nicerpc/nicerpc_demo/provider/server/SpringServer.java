package cn.nicerpc.nicerpc_demo.provider.server;

import cn.nicerpc.provider.ProviderConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.nicerpc.nicerpc_demo.provider")
public class SpringServer {

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(ProviderConfig.class,SpringServer.class);
    }
}
