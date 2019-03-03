package cn.nicerpc.nicerpc_demo.provider.controller;

import cn.nicerpc.common.annotation.Remote;
import cn.nicerpc.nicerpc_demo.api.UserService;
import cn.nicerpc.nicerpc_demo.model.SomeThing;


@Remote
public class UserServiceImpl implements UserService {
    @Override
    public SomeThing userGivenSomeThing() {
        System.out.println("userServiceImpl called");
        SomeThing someThing = new SomeThing();
        someThing.setId(123);
        someThing.setName("wan洪基");
        someThing.setDes("he is always very man and cool!");
        return someThing;
    }
}
