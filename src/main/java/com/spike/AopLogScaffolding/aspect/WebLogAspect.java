package com.spike.AopLogScaffolding.aspect;

import com.alibaba.fastjson.JSON;
import com.spike.AopLogScaffolding.annotation.LogTrace;
import com.spike.AopLogScaffolding.entity.WebLog;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(100)
@Component
public class WebLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    private static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<>();
    private static final String START_TIME = "start_time";
    private static final String REQUEST_PARAMS = "request_params";

    //todo 注入一个写入db的服务实例


    @Pointcut("execution(* com.spike.AopLogScaffolding.controller..*.*(..))")
    public void webLog(){}


    @Before("webLog() && @annotation(logTrace)")
    public void doBefore(JoinPoint joinPoint, LogTrace logTrace){
        long startTime = System.currentTimeMillis();
        HashMap<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, startTime);

        StringBuilder requestStr = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        if (null != args && args.length>0){
            for (Object arg : args) {
                requestStr.append(arg.toString());
            }
        }
        threadInfo.put(REQUEST_PARAMS,requestStr);
        threadLocal.set(threadInfo);
        LOGGER.info("{} 接口调用开始, requestData: {}",
            logTrace.name(),
            threadInfo.get(REQUEST_PARAMS));
    }

    @AfterReturning(value = "webLog() && @annotation(logTrace))", returning = "response")
    public void doAfterReturning(LogTrace logTrace, Object response){
        Map<String, Object> threadInfo = threadLocal.get();
        long timeConsumed = System.currentTimeMillis() -
                (long)threadInfo.getOrDefault(START_TIME, System.currentTimeMillis());
        if (logTrace.persistence()){
            writeDb(logTrace.name(),
                (String) threadInfo.getOrDefault(REQUEST_PARAMS,""),
                JSON.toJSONString(response),timeConsumed);
        }
        threadLocal.remove();
        // 日志记录
        LOGGER.info("{} 接口调用结束, 总耗时: {} ms, responseData: {}",
            logTrace.name(),
            timeConsumed,
            JSON.toJSONString(response));
    }



    public void writeDb(String operationName, String reqStrs, String respStr, long timeConsumed){
        WebLog webLog = new WebLog();
        webLog.setCreateTime(new Date());
        webLog.setRequest(reqStrs);
        webLog.setResponse(respStr);
        webLog.setError(false);
        webLog.setOperationName(operationName);
        webLog.setTaketime(timeConsumed);
        //todo 写入db
    }

}
