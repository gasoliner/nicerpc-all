package cn.nicerpc.nicerpc_demo.provider.controller;

import cn.nicerpc.common.annotation.Remote;
import cn.nicerpc.nicerpc_demo.api.DogService;
import cn.nicerpc.nicerpc_demo.model.SomeThing;
import com.alibaba.fastjson.JSON;

@Remote
public class DogServiceImpl implements DogService {
    @Override
    public String dogWantSomeThing(SomeThing someThing) {
        System.out.println("dogServiceImpl called " + JSON.toJSONString(someThing));
        String result = "dog is very rude! he refuse this gift in a rude way , he return a shit ! 哈哈 " +
                "someThing content" + JSON.toJSONString(someThing);
        return result;
    }
}
