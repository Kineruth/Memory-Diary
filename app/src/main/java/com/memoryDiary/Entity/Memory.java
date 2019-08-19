package com.memoryDiary.Entity;


import java.util.ArrayList;

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
    private ArrayList<String> imageLabels = null;

    public Memory() {
    }

    public Memory(String uid, String memoryTitle, String description, long creationTime, String image, ArrayList<String> imageLabels) {
        this.uid = uid;
        this.memoryTitle = memoryTitle;
        this.description = description;
        this.creationTime = creationTime;
        this.imagePath = image;
        this.imageLabels= imageLabels;
    }

    public Memory(Memory memory){
        this.uid = memory.uid;
        this.memoryTitle = memory.memoryTitle;
        this.description = memory.description;
        this.creationTime = memory.creationTime;
        this.imagePath = memory.imagePath;
        this.imageLabels= memory.imageLabels;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return this Memory's name.
     */
    public String getMemoryTitle() {
        return this.memoryTitle;
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
        return this.description;
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
        return this.creationTime;
    }

    /**
     * Sets this Memory's creation time.
     * @param creationTime the creation time to be set from.
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<String> getImageLabels(){ return this.imageLabels; }

    public void setImageLabels(ArrayList<String> imageLabels){ this.imageLabels = imageLabels; }

    public void setAll(Memory m){
        this.uid = m.uid;
        this.memoryTitle = m.memoryTitle;
        this.description = m.description;
        this.creationTime = m.creationTime;
        this.imagePath = m.imagePath;
        this.imageLabels = m.imageLabels;
    }
}
