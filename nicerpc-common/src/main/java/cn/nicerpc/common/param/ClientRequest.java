package cn.nicerpc.common.param;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {

    private final long id;

    private Object content;

    private String command;

    private String host;

    private Integer port;

    private String category;

    private String serviceType;

    private String methodName;

    private Map<String,String> parameters;

    private Map<String, AtomicInteger> methodActiveMap;

    private final AtomicLong aid = new AtomicLong(1);

    public ClientRequest() {
        this.id = aid.incrementAndGet();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getId() {
        return id;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Map<String, AtomicInteger> getMethodActiveMap() {
        return methodActiveMap;
    }

    public void setMethodActiveMap(Map<String, AtomicInteger> methodActiveMap) {
        this.methodActiveMap = methodActiveMap;
    }
}
