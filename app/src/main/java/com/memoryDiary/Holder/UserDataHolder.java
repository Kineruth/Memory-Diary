package com.memoryDiary.Holder;

import com.memoryDiary.Entity.User;

public class UserDataHolder {
    private User user = null;
    private static final UserDataHolder data = new UserDataHolder();

    private UserDataHolder(){
        user = new User();
    }

    public static UserDataHolder getUserDataHolder(){
        return data;
    }

    public User getUser(){
        return user;
    }

    public void clearUser(){
        user.setAll(new User());
    }
}
