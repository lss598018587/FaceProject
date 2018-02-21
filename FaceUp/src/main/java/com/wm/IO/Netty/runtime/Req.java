package com.wm.IO.Netty.runtime;

import java.io.Serializable;

/**
 * Created by wangmiao on 2018/2/18.
 */
public class Req implements Serializable {
    private static final long serialVersionUID = 7868449756124621319L;

    private String id;
    private String name;
    private String requestMessage;
    private byte[] attachment;

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

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
