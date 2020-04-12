package com.example.mark_xix.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;

@Builder
public class Medicine implements Serializable {

    private @DocumentId String id;
    private String name;
    private int price;
    private String description;
    private EnumSlot slot;
    private String image_link;
    private @ServerTimestamp Date timestamp;

    public Medicine() {
    }

    public Medicine(String id, String name, int price, String description, EnumSlot slot, String image_link, Date timestamp) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.slot = slot;
        this.image_link = image_link;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumSlot getSlot() {
        return slot;
    }

    public void setSlot(EnumSlot slot) {
        this.slot = slot;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", slot=" + slot +
                ", image_link='" + image_link + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
