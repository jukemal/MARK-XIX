package com.example.mark_xix.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class _ResponseBody implements Serializable {
    @SerializedName("status")
    private EnumStatus status;

    @SerializedName("date")
    private Date date;

    public _ResponseBody() {
    }

    public EnumStatus getStatus() {
        return status;
    }

    public void setStatus(EnumStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "_ResponseBody{" +
                "status=" + status +
                ", date=" + date +
                '}';
    }
}
