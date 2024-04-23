package com.midsummra.subway.common.exceptionHandler;

import com.midsummra.subway.common.response.Result;
import com.midsummra.subway.common.response.StatusCode;
import jakarta.servlet.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public Result bindExceptionHandler(ServletRequest request, BindException bindException){
        String s = bindException.toString();
        List<FieldError> fieldErrors = bindException.getFieldErrors();
        String defaultMessage = fieldErrors.get(0).getDefaultMessage();

        log.warn("=> " + s);
        log.warn("=> " + defaultMessage);

        return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(), StatusCode.INVALID_PARAMETERS.getResultMessage());
    }
}
