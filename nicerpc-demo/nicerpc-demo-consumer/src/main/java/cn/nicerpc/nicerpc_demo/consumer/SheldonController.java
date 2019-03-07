package cn.nicerpc.nicerpc_demo.consumer;

import cn.nicerpc.common.annotation.RemoteInvoke;
import cn.nicerpc.consumer.invoke.impl.FailoverInvoker;
import cn.nicerpc.consumer.loadBalance.impl.WeightRandomLoadBalance;
import cn.nicerpc.nicerpc_demo.api.CatService;
import cn.nicerpc.nicerpc_demo.api.DogService;
import cn.nicerpc.nicerpc_demo.api.UserService;
import cn.nicerpc.nicerpc_demo.model.SomeThing;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;

@Controller
public class SheldonController {

    @RemoteInvoke(filter = {},loadBalance = WeightRandomLoadBalance.class,invokeStrategy = FailoverInvoker.class)
    private UserService userService;

    @RemoteInvoke(filter = {},loadBalance = WeightRandomLoadBalance.class,invokeStrategy = FailoverInvoker.class)
    private CatService catService;

    @RemoteInvoke(filter = {},loadBalance = WeightRandomLoadBalance.class,invokeStrategy = FailoverInvoker.class)
    private DogService dogService;

    public void testService() {
        SomeThing someThing = userService.userGivenSomeThing();
        System.out.println("userService.userGivenSomeThing() " + JSON.toJSONString(someThing));
        String cat = catService.catWantSomeThing(someThing);
        System.out.println("catService.catWantSomeThing(someThing) " + JSON.toJSONString(cat));
        String dog = dogService.dogWantSomeThing(someThing);
        System.out.println("dogService.dogWantSomeThing(someThing) " + JSON.toJSONString(dog));
    }
}
