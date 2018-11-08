package com.njbdqn.vo;

import java.io.Serializable;

public class UserDetails implements Serializable{
    private int userid;
    private int udetid;
    private String emertcontact;
    private String emerttelephone;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUdetid() {
        return udetid;
    }

    public void setUdetid(int udetid) {
        this.udetid = udetid;
    }

    public String getEmertcotact() {
        return emertcontact;
    }

    public void setEmertcotact(String emertcotact) {
        this.emertcontact = emertcotact;
    }

    public String getEmerttelephone() {
        return emerttelephone;
    }

    public void setEmerttelephone(String emerttelephone) {
        this.emerttelephone = emerttelephone;
    }
}
