package com.memoryDiary.Holder;

import com.memoryDiary.Entity.Diary;

public class DiaryDataHolder {
    private Diary diary = null;
    private static final DiaryDataHolder data = new DiaryDataHolder();

    private DiaryDataHolder(){
        diary = new Diary();
    }

    public static DiaryDataHolder getPersonalDiaryDataHolder(){
        return data;
    }

    public Diary getPersonalDiary(){
        return diary;
    }

    public void clearPersonalDiary(){
        diary.setAll(new Diary());
    }
}
