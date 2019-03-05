package cn.nicerpc.consumer.core;

import cn.nicerpc.consumer.invoke.Invoker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerManager {

    static final ConcurrentHashMap<String, List<Invoker>> invokerManagerMap =
            new ConcurrentHashMap<>();
}
