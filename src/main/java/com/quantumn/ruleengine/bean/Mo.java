package com.quantumn.ruleengine.bean;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class Mo {
    String moId;
    String moName;
    List<String> moDrls;

    public Mo(String moId, String moName, List<String> moDrls) {
        this.moId = moId;
        this.moName = moName;
        this.moDrls = moDrls;
    }

    @Override
    public String toString() {
        return "Mo{" +
                "moId='" + moId + '\'' +
                ", moName='" + moName + '\'' +
                '}';
    }
}
