package com.minsu.controller;

import com.minsu.dao.UserDao;
import com.minsu.model.entity.User;
import com.minsu.model.enums.UserRole;

public class UserController {
    private final UserDao userDao;

    public UserController() {
        this.userDao = new UserDao();
    }

    public boolean register(String username, String password, UserRole role) {
        if (username == null || username.trim().isEmpty() 
            || password == null || password.trim().isEmpty()) {
            return false;
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setRole(role);

        return userDao.addUser(user);
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    public boolean deleteUser(Long id) {
        return userDao.deleteUser(id);
    }
} 