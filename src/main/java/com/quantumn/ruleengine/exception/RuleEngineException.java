package com.quantumn.ruleengine.exception;

public class RuleEngineException extends RuntimeException {

    public RuleEngineException(EngineExceptionEnum error) {
        super(error.getMessage());
    }

    public RuleEngineException(EngineExceptionEnum error,String detailMsg) {
        super(error.getMessage()+"\r\n"+detailMsg);
    }

    public RuleEngineException(EngineExceptionEnum error, Throwable throwable) {
        super(error.getMessage(),throwable);
    }
}
