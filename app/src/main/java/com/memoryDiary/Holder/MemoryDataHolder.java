package com.memoryDiary.Holder;
import com.memoryDiary.Entity.Memory;

public class MemoryDataHolder {

    private Memory memory;
    private static final MemoryDataHolder data = new MemoryDataHolder();

    private MemoryDataHolder(){
        this.memory = new Memory();
    }

    public static MemoryDataHolder getMemoryDataHolder(){
        return data;
    }

    public Memory getMemory(){
        return this.memory;
    }

    public void clearMemory(){
        this.memory.setAll(new Memory());
    }
}