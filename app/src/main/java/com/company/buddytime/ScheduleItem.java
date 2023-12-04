package com.company.buddytime;

import java.util.Date;

public class ScheduleItem {
    private String title;
    private Date time1;

    public ScheduleItem(String title, Date time1) {
        this.title = title;
        this.time1 = time1;
    }

    public String getTitle() {
        return title;
    }

    public Date getTime1() {
        return time1;
    }
}