package org.devzen.domain;

import java.util.Date;

/**
 * POJO
 */
public class FooBean {
    private int id;
    private String name;
    private Date time;
    private String[] address;

    public FooBean() {
        id = 1;
        name = "terry";
        time = new Date();
        address = new String[]{"address1", "address2", "address3"};
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }
}
