package com.codecanvas.model;

public class UserSession {
    private static final UserSession instance = new UserSession();
    private int userId = -1;
    private String username;

    private UserSession() {}
    public static UserSession getInstance() { return instance; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}