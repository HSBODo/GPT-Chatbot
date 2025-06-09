package com.example.chatbot.domain.member.constant;

public enum MemberRole {
    ADMIN("관리자"),
    MANAGER("매니저"),
    MEMBER("일반"),
    ;

    private String name;

    MemberRole(String name) {
        this.name = name;
    }

    public static MemberRole fromString(String name) {
        for (MemberRole role : MemberRole.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + name);
    }
    public String getName() {
        return name;
    }
}
