package com.example.mark_xix.models;

import lombok.Builder;

@Builder
public class MedicineSelecter {
    private String id;
    private Medicine medicine;
    private boolean isSelected;

    public MedicineSelecter() {
    }

    public MedicineSelecter(String id, Medicine medicine, boolean isSelected) {
        this.id = id;
        this.medicine = medicine;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "MedicineSelecter{" +
                "id='" + id + '\'' +
                ", medicine=" + medicine +
                ", isSelected=" + isSelected +
                '}';
    }
}
