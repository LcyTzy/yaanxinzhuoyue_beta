package com.zhantu.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhantu.entity.OperationLog;
import com.zhantu.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.zhantu.annotation.OperationLog)")
    public void logPointcut() {}

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, com.zhantu.annotation.OperationLog opLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLog log = new OperationLog();
        log.setModule(opLog.module());
        log.setOperation(opLog.operation());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setRequestUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            log.setIp(request.getRemoteAddr());
            try {
                log.setRequestParams(objectMapper.writeValueAsString(point.getArgs()));
            } catch (Exception e) {
                log.setRequestParams("参数序列化失败");
            }
        }

        try {
            Object result = point.proceed();
            log.setStatus(1);
            return result;
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            try {
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    Long userId = (Long) request.getAttribute("userId");
                    log.setUserId(userId);
                    String role = (String) request.getAttribute("role");
                    log.setUsername(role != null ? role : "anonymous");
                }
            } catch (Exception e) {
                log.setUsername("unknown");
            }
            operationLogMapper.insert(log);
        }
    }
}
