package com.complete.todayspace.global.exception;

public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public String getExceptionMethod(){
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

        return className + "." + methodName;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
