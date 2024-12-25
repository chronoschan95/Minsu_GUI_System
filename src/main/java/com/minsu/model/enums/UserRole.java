package com.minsu.model.enums;

public enum UserRole {
    ADMIN("管理员"),
    HOST("房东"),
    GUEST("游客");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(text) || 
                role.displayName.equals(text)) {
                return role;
            }
        }
        return GUEST; // 默认返回游客角色
    }
}
