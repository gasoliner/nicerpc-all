package cn.nicerpc.common.param;

import com.alibaba.fastjson.JSON;

public class Response {

    private long id;

    private Object result;

    private String code = "00000";//00000表示成功，其他表示失败

    private String msg;//失败的原因

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getResult(Class<?> returnType) {
        return JSON.parseObject(JSON.toJSONString(result),returnType);
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
