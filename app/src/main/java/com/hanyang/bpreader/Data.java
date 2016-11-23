package com.hanyang.bpreader;
/**
 * Created by kyujin on 2016-08-18.
 */
public class Data {
    String name, content;

    public Data(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
