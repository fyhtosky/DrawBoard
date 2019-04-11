package com.example.liusheng.painboard.bean;

/**
 * Created by chenzhen on 2019/2/20.
 */

public class TemplateBean {

    private String name;
    private String path;


    public TemplateBean() {
    }

    public TemplateBean(String name, String path) {
        this.name = name;
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
