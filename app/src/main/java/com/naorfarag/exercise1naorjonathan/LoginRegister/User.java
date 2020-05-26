package com.naorfarag.exercise1naorjonathan.LoginRegister;

import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class User implements Serializable {
    private String email;
    private String password;
    private String phone;
    private int age;
    public User() {

    }

    public User(String email, String password, String phone, int age) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
