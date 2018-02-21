package com.wm.IO.Netty.runtime;

import java.io.Serializable;

/**
 * Created by wangmiao on 2018/2/18.
 */
public class Resp implements Serializable {
    private static final long serialVersionUID = -8292345691165670328L;

    private String id;
    private String name;
    private String responseMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
