package com.memoryDiary.Entity;


/**
 * This class represents the user using the app.
 */
public class User {

    private String name, uid, phoneNumber;

    /**
     * Default Constructor
     */
    public User() { }

    /**
     * Parameterized Constructor
     * @param fullName this user's full name.
     * @param uid this user ID.
     */
    public User(String fullName ,String uid, String phone) {
        this.name = fullName;
        this.uid = uid;
        this.phoneNumber = phone;
    }

    /**
     * Copy constructor by a given other User.
     * @param user a given User to be set from.
     */
    public void User(User user){
        this.name = user.name;
        this.uid = user.uid;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUid() {
        return this.uid;
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
        this.phoneNumber = user.phoneNumber;
    }
    @Override
    public String toString() {
        return "User{" +
                ", name='" + this.name + '\'' +
                ", uid='" + this.uid + '\'' +
                ", phone='" + this.phoneNumber + '\'' +
                '}';
    }
}
