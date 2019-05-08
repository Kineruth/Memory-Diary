package com.memoryDiary.Holder;
import com.memoryDiary.Entity.Memory;

public class MemoryDataHolder {

    private Memory memory = null;
    private static final MemoryDataHolder data = new MemoryDataHolder();

    private MemoryDataHolder(){
        memory = new Memory();
    }

    public static MemoryDataHolder getMemoryDataHolder(){
        return data;
    }

    public Memory getMemory(){
        return memory;
    }

    public void clearMemory(){
        memory.setAll(new Memory());
    }
}