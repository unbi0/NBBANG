package com.elice.nbbang.domain.ott.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class OttNotFoundException extends CustomException {
    public OttNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
