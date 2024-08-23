package com.knu.fromnow.api.global.error.errorcode;

public interface DataErrorCode<T> extends ErrorCode{
    T getData();
}
