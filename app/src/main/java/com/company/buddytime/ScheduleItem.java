package com.company.buddytime;

import java.util.Date;

public class ScheduleItem {
    private String title;
    private String DocId;
    private Date time1;


    public ScheduleItem(String title, Date time1, String DocId) {
        this.title = title;
        this.time1 = time1;
        this.DocId = DocId;
    }

    public String getTitle() {
        return title;
    }

    public Date getTime1() {
        return time1;
    }

    public String getDocumentId() {
        return DocId;
    }
}