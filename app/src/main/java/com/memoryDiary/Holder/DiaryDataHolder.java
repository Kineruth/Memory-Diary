package com.memoryDiary.Holder;

import com.memoryDiary.Entity.Diary;

public class DiaryDataHolder {
    private Diary diary;
    private static final DiaryDataHolder data = new DiaryDataHolder();

    private DiaryDataHolder(){
        this.diary = new Diary();
    }

    public static DiaryDataHolder getDiaryDataHolder(){
        return data;
    }

    public Diary getDiary(){
        return this.diary;
    }

    public void clearDiary(){
        this.diary.setAll(new Diary());
    }
}
