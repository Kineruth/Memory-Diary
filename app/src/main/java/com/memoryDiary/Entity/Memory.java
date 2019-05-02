package com.memoryDiary.Entity;


/**
 * This class represents a single Memory in a Diary.
 * A Memory is an image and its description. It has other parameters.
 */
public class Memory {
    private String uid;
    private String memoryTitle;
    private String description;
    private long creationTime;
    private String image;

    public Memory() {
    }

    public Memory(String uid, String memoryTitle, String description, long creationTime, String image) {
        this.uid = uid;
        this.memoryTitle = memoryTitle;
        this.description = description;
        this.creationTime = creationTime;
        this.image = image;
    }

    public Memory(Memory memory){
        this.uid = memory.uid;
        this.memoryName = memory.memoryName;
        this.description = memory.description;
        this.creationTime = memory.creationTime;
        this.image = memory.image;
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
    public String getMemoryTitle() {
        return memoryTitle;
    }

    /**
     * Sets this Memory's name.
     * @param memoryTitle the name to be set from.
     */
    public void setMemoryTitle(String memoryTitle) {
        this.memoryTitle = memoryTitle;
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
        this.memoryTitle = m.memoryTitle;
        this.description = m.description;
        this.creationTime = m.creationTime;
        this.image = m.image;
    }
}
