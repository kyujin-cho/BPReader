/**
 * Data
 * 이름-내용 쌍 형태의 Data 관리를 위한 클래스
 * @author Kyujin Cho
 * @version 1.0
 */

package com.hanyang.bpreader;

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
