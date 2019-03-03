package cn.nicerpc.nicerpc_demo.provider.controller;

import cn.nicerpc.common.annotation.Remote;
import cn.nicerpc.nicerpc_demo.api.CatService;
import cn.nicerpc.nicerpc_demo.model.SomeThing;
import com.alibaba.fastjson.JSON;

@Remote
public class CatServiceImpl implements CatService {
    @Override
    public String catWantSomeThing(SomeThing someThing) {
        System.out.println("catServiceImpl called" + JSON.toJSONString(someThing));
        String result = "cat like you ! he accept this gift ! this gift content 如下 " + JSON.toJSONString(someThing);
        return result;
    }
}
