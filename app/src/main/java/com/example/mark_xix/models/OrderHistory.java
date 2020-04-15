package com.example.mark_xix.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Builder;

@Builder
public class OrderHistory implements Serializable {
    private @DocumentId String id;
    private List<Medicine> medicineList;
    private int total;
    private @ServerTimestamp Date timestamp;

    public OrderHistory() {
    }

    public OrderHistory(String id, List<Medicine> medicineList, int total, Date timestamp) {
        this.id = id;
        this.medicineList = medicineList;
        this.total = total;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Medicine> getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "id='" + id + '\'' +
                ", medicineList=" + medicineList +
                ", total=" + total +
                ", timestamp=" + timestamp +
                '}';
    }
}
