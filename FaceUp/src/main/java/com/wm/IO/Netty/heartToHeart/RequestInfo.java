package com.wm.IO.Netty.heartToHeart;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangmiao on 2018/2/18.
 */
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = -5712642177037911053L;
    private String ip;
    private Map<String,String> cpuPerc = new HashMap<>();
    private Map<String,String> memory = new HashMap<>();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, String> getCpuPerc() {
        return cpuPerc;
    }

    public void setCpuPerc(Map<String, String> cpuPerc) {
        this.cpuPerc = cpuPerc;
    }

    public Map<String, String> getMemory() {
        return memory;
    }

    public void setMemory(Map<String, String> memory) {
        this.memory = memory;
    }
}
