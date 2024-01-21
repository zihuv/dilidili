package com.zihuv.dilidili.exception;

import com.zihuv.dilidili.model.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result<?> abstractException(HttpServletRequest request, AbstractException ex) {
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), ex.getErrorMessage());
        return Result.fail(ex.getErrorMessage());
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<?> defaultErrorHandler(HttpServletRequest request, Exception e) {
        // 未预料到的异常
        log.error("[{}] {} ", request.getMethod(), getUrl(request), e);
        return Result.fail(e.getMessage());
    }

    private String getUrl(HttpServletRequest request) {
        if (request.getQueryString() == null || "".equals(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}