package com.memoryDiary.Holder;

import com.memoryDiary.Entity.Diary;

public class DiaryDataHolder {
    private Diary diary = null;
    private static final DiaryDataHolder data = new DiaryDataHolder();

    private DiaryDataHolder(){
        diary = new Diary();
    }

    public static DiaryDataHolder getDiaryDataHolder(){
        return data;
    }

    public Diary getDiary(){
        return diary;
    }

    public void clearDiary(){
        diary.setAll(new Diary());
    }
}
