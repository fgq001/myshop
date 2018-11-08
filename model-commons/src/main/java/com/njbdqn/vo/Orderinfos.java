package com.njbdqn.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orderinfos implements Serializable{
    private int ordid;
    private int userid;
    private String username;
    private Date orddate;
    private List<Shopinfos> shops = new ArrayList<Shopinfos>();
    private int buynum;

    public int getBuynum() {
        return buynum;
    }

    public void setBuynum(int buynum) {
        this.buynum = buynum;
    }

    public int getOrdid() {
        return ordid;
    }

    public void setOrdid(int ordid) {
        this.ordid = ordid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getOrddate() {
        return orddate;
    }

    public void setOrddate(Date orddate) {
        this.orddate = orddate;
    }

    public List<Shopinfos> getShops() {
        return shops;
    }

    public void setShops(List<Shopinfos> shops) {
        this.shops = shops;
    }
}
