package com.memoryDiary.Entity;


/**
 * This class represents a single Memory in a Diary.
 * A Memory is an imagePath and its description. It has other parameters.
 */
public class Memory {
    private String uid;
    private String memoryTitle;
    private String description;
    private long creationTime;
    private String imagePath;

    public Memory() {
    }

    public Memory(String uid, String memoryTitle, String description, long creationTime, String image) {
        this.uid = uid;
        this.memoryTitle = memoryTitle;
        this.description = description;
        this.creationTime = creationTime;
        this.imagePath = image;
    }

    public Memory(Memory memory){
        this.uid = memory.uid;
        this.memoryTitle = memory.memoryTitle;
        this.description = memory.description;
        this.creationTime = memory.creationTime;
        this.imagePath = memory.imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAll(Memory m){
        this.uid = m.uid;
        this.memoryTitle = m.memoryTitle;
        this.description = m.description;
        this.creationTime = m.creationTime;
        this.imagePath = m.imagePath;
    }
}
