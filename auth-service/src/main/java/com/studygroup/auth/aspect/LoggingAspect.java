package com.studygroup.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Cross-cutting concerns for the auth-service using Aspect-Oriented Programming.
 *
 * <p>Three responsibilities (each in a separate advice type):
 * <ol>
 *   <li><b>Performance Tracking</b> — logs execution time for every service method.</li>
 *   <li><b>Input Logging</b> — DEBUG-level log on every controller entry.</li>
 *   <li><b>Exception Auditing</b> — WARN log for any service-layer exception.</li>
 * </ol>
 *
 * <p>AOP keeps this logic out of business classes, satisfying SRP and OCP.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // ─── Pointcut Definitions ─────────────────────────────────────────────

    /** All methods in any @Service annotated class. */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    /** All methods in any @RestController annotated class. */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    // ─── Advice: Performance Tracking ────────────────────────────────────

    /**
     * Wraps every service method to measure and log execution time.
     * A threshold of 500 ms triggers a WARN to surface slow queries early.
     */
    @Around("serviceLayer()")
    public Object trackPerformance(ProceedingJoinPoint pjp) throws Throwable {
        long start  = System.currentTimeMillis();
        String sig  = pjp.getSignature().toShortString();

        try {
            Object result = pjp.proceed();
            long elapsed  = System.currentTimeMillis() - start;

            if (elapsed > 500) {
                log.warn("SLOW SERVICE METHOD [{}] took {}ms", sig, elapsed);
            } else {
                log.debug("Service [{}] completed in {}ms", sig, elapsed);
            }
            return result;

        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Service [{}] FAILED after {}ms — {}", sig, elapsed, t.getMessage());
            throw t;
        }
    }

    // ─── Advice: Controller Input Logging ────────────────────────────────

    /**
     * Logs the method name and arguments before every controller invocation.
     * Only active at DEBUG level — no PII leakage in production with INFO logging.
     */
    @Before("controllerLayer()")
    public void logControllerEntry(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            log.debug("→ Controller [{}] args={}",
                    jp.getSignature().toShortString(),
                    Arrays.toString(jp.getArgs()));
        }
    }

    /**
     * Logs the return value after every controller invocation.
     */
    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void logControllerReturn(JoinPoint jp, Object result) {
        log.debug("← Controller [{}] returned={}",
                jp.getSignature().toShortString(),
                result != null ? result.getClass().getSimpleName() : "null");
    }

    // ─── Advice: Exception Auditing ───────────────────────────────────────

    /**
     * Captures any exception thrown from the service layer for audit logging.
     * Does NOT suppress the exception — it re-propagates normally.
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void auditServiceException(JoinPoint jp, Throwable ex) {
        log.warn("Service exception in [{}]: {} — {}",
                jp.getSignature().toShortString(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }
}
