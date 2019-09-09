package com.memoryDiary.Entity;

import java.util.ArrayList;

public class Tags {

    private User owner;
    private ArrayList<Memory> memories = new ArrayList<>();;

    public Tags(){}

    /**
     * Parameterized Constructor
     * @param owner the owner on the diary
     */
    public Tags(User owner){
        this.owner = owner;
    }

    /**
     * Parameterized Constructor
     * @param owner the owner on the diary
     * @param memories the contents of the diary
     */
    public Tags(User owner, ArrayList<Memory> memories){
        this.owner = owner;
        this.memories = memories;
    }

    /**
     * Insert a memory to the diary
     * @param memory memory to be append to the diary
     * @return true if the memory appended successfully
     */
    public boolean addMemory(Memory memory){
        return this.memories.add(memory);
    }

    public Memory getMemory(int i){ return this.memories.get(i); }

    public int getAmount(){ return this.memories.size(); }
    /**
     * Gets all the contents of the diary
     * @return memories collection
     */
    public ArrayList<Memory> getAllMemories(){
        return this.memories;
    }

    public void setAll(Tags t){
        this.owner = t.owner;
        if(this.memories != null){
            this.memories.clear();
            this.memories.addAll(t.memories);
        }
    }

    public void clearMemories (){ this.memories.clear(); }
}
