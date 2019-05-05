package com.memoryDiary.Entity;


/**
 * This class represents the user using the app.
 */
public class User {

    private String name, uid;

    /**
     * Default Constructor
     */
    public User() { }

    /**
     * Parameterized Constructor
     * @param fullName this user's full name.
     * @param uid this user ID.
     */
    public User(String fullName ,String uid) {
        this.name = fullName;
        this.uid = uid;
    }

    /**
     * Copy constructor by a given other User.
     * @param user a given User to be set from.
     */
    public void User(User user){
        this.name = user.name;
        this.uid = user.uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Sets all this User's parameters by a given other User.
     * @param user a given User to be set from.
     */
    public void setAll(User user){
        this.name = user.name;
        this.uid = user.uid;
    }
    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
