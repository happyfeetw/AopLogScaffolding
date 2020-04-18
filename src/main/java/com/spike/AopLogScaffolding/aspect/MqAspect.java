package com.spike.AopLogScaffolding.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MqAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqAspect.class);

}
