package com.njbdqn.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shopinfos implements Serializable{
    private int shopid;
    private int typeid;
    private String title;
    private String shops;
    private int instore;
    private String defimg;
    private float price;
    private String typename;
    private String typeenname;
    private int buynum;
    private List<Map> features = new ArrayList<Map>();
    private List imgs = new ArrayList();

    public int getBuynum() {
        return buynum;
    }

    public void setBuynum(int buynum) {
        this.buynum = buynum;
    }

    public List getImgs() {
        return imgs;
    }

    public void setImgs(List imgs) {
        this.imgs = imgs;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShops() {
        return shops;
    }

    public void setShops(String shops) {
        this.shops = shops;
    }

    public int getInstore() {
        return instore;
    }

    public void setInstore(int instore) {
        this.instore = instore;
    }

    public String getDefimg() {
        return defimg;
    }

    public void setDefimg(String defimg) {
        this.defimg = defimg;
    }

    public float getPrive() {
        return price;
    }

    public void setPrive(float prive) {
        this.price = prive;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTypeenname() {
        return typeenname;
    }

    public void setTypeenname(String typeenname) {
        this.typeenname = typeenname;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<Map> getFeatures() {
        return features;
    }

    public void setFeatures(List<Map> features) {
        this.features = features;
    }
}
