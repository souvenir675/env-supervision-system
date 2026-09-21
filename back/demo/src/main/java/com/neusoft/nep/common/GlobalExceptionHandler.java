package com.neusoft.nep.exception;

import com.neusoft.nep.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 打印完整堆栈信息到控制台
        logger.error("系统异常: ", e);
        e.printStackTrace();
        return Result.error(500, e.getMessage());
    }
}