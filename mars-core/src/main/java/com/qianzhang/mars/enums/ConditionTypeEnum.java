package com.qianzhang.mars.enums;

public enum ConditionTypeEnum {
    TYPE_THEN("then","then"),
    TYPE_WHEN("when","when")
    ;
    private String type;
    private String name;

    ConditionTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
