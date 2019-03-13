package com.memoryDiary.Object;

import java.util.Date;
import java.util.ArrayList;


/**
 * This class represents a single Memory in a Diary.
 * A Memory is an image and its description. It has other parameters.
 */
public class Memory {
    private String uid;
    private String memoryName;
    private String description;
    private long creationTime;
    private String image;

    public Memory() {
    }

    public Memory(String uid, String memoryName, String description, long creationTime, String image) {
        this.uid = uid;
        this.memoryName = memoryName;
        this.description = description;
        this.creationTime = creationTime;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return this Memory's name.
     */
    public String getMemoryName() {
        return memoryName;
    }

    /**
     * Sets this Memory's name.
     * @param memoryName the name to be set from.
     */
    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
    }

    /**
     * @return this Memory's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets this Memory's description.
     * @param description the description to be set from.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return this Memory's creation time.
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Sets this Memory's creation time.
     * @param creationTime the creation time to be set from.
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAll(Memory m){
        this.uid = m.uid;
        this.memoryName = m.memoryName;
        this.description = m.description;
        this.creationTime = m.creationTime;
        this.image = m.image;
    }
}
