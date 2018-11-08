package com.njbdqn.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Features implements Serializable{
    private int featureid;
    private String featurename;
    private String featureenname;

    public String getFeatureenname() {
        return featureenname;
    }

    public void setFeatureenname(String featureenname) {
        this.featureenname = featureenname;
    }

    private List feas = new ArrayList();

    public int getFeatureid() {
        return featureid;
    }

    public void setFeatureid(int featureid) {
        this.featureid = featureid;
    }

    public String getFeaturename() {
        return featurename;
    }

    public void setFeaturename(String featurename) {
        this.featurename = featurename;
    }

    public List getFeas() {
        return feas;
    }

    public void setFeas(List feas) {
        this.feas = feas;
    }
}
