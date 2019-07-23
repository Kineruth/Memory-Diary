package com.memoryDiary.Holder;

import com.memoryDiary.Entity.User;

public class UserDataHolder {
    private User user;
    private static final UserDataHolder data = new UserDataHolder();

    private UserDataHolder(){
        this.user = new User();
    }

    public static UserDataHolder getUserDataHolder(){
        return data;
    }

    public User getUser(){
        return this.user;
    }

    public void clearUser(){
        this.user.setAll(new User());
    }
}
