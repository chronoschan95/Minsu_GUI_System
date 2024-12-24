package com.minsu.controller;

import com.minsu.dao.UserDao;
import com.minsu.model.entity.User;

public class LoginController {
    private final UserDao userDao;

    public LoginController() {
        this.userDao = new UserDao();
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDao.login(username.trim(), password.trim());
    }
}
