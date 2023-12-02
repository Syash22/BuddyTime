package com.company.buddytime;

public class SingerItem {

    String name;
    String moblie;

    public SingerItem(String name, String moblie) {
        this.name = name;
        this.moblie = moblie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    @Override
    public String toString() {
        return "SingerItem{" +
                "name='" + name + '\'' +
                ", moblie='" + moblie + '\'' +
                '}';
    }
}