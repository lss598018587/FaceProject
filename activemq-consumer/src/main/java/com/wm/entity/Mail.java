package com.wm.entity;

/**
 * Created by wangmiao on 2018/2/22.
 */
public class Mail {
    //来自谁
    private String from;
    //发给谁
    private String to;
    //主题
    private String subject;
    //内容
    private String context;
    public Mail(){}
    public Mail(String from, String to, String subject, String context) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.context = context;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
