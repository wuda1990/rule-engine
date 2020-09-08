package com.quantumn.ruleengine.exception;

public enum EngineExceptionEnum {

    RULE_COMPILE_ERROR("2000", "规则编译错误"),
    CONTAINER_ERROR("2001","容器生成错误");

    String code;
    String message;

    EngineExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return code+" msg:"+ message;
    }
}
