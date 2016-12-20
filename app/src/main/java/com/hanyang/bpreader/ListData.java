/**
 * ListData
 * MainActivity에서 저장된 바코드 텍스트를 RecyclerView를 통해 보여줄 때 사용할 데이터 집합
 * @author Kyujin Cho
 * @version 1.0
 */

package com.hanyang.bpreader;

public class ListData {
    String id;
    String rawdata;
    String depart;
    String arrival;
    String airline;
    String code;

    public ListData(String id, String rawdata, String depart, String arrival, String airline, String code) {
        this.id = id;
        this.rawdata = rawdata;
        this.depart = depart;
        this.arrival = arrival;
        this.airline = airline;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRawdata() {
        return rawdata;
    }

    public void setRawdata(String rawdata) {
        this.rawdata = rawdata;
    }
}