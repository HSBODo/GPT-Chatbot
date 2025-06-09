package com.example.chatbot.dto.kakao.constatnt.block;

public enum BlockId {
    MAIN("메인", "65262b36ddb57b43495c18f8"),
    SUBJECTS_FIRST("과목1","66c6a682f5ed3400bfabbbc5"),

    ;
    private final String name;
    private final String blockId;

    BlockId(String name, String blockId) {
        this.name = name;
        this.blockId = blockId;
    }

    public String getName() {
        return name;
    }

    public String getBlockId() {
        return blockId;
    }
}
