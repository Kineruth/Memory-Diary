package com.memoryDiary.Entity;

import java.util.ArrayList;

public class Diary {
    private User owner;
    private ArrayList<Memory> memories;

    /**
     * Parameterized Constructor
     * @param owner the owner on the diary
     */
    public Diary(User owner){
        this.owner = owner;
        this.memories = new ArrayList<>();
    }

    /**
     * Parameterized Constructor
     * @param owner the owner on the diary
     * @param memories the contents of the diary
     */
    public Diary(User owner, ArrayList<Memory> memories){
        this.owner = owner;
        this.memories = memories;
    }

    /**
     * Insert a memory to the diary
     * @param memory memory to be append to the diary
     * @return true if the memory appended successfully
     */
    public boolean insertMemory(Memory memory){
        return this.memories.add(memory);
    }

    /**
     * Gets all the contents of the diary
     * @return memories collection
     */
    public ArrayList<Memory> getAllMemories(){
        return this.memories;
    }
}
