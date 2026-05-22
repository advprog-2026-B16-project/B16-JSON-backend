package id.ac.ui.cs.advprog.jsonbackend.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(Monitored)")
    public Object monitorExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            meterRegistry.counter("method.execution.error",
                    "method", methodName,
                    "exception", e.getClass().getSimpleName()
            ).increment();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            meterRegistry.timer("method.execution.time", "method", methodName)
                    .record(duration, TimeUnit.MILLISECONDS);
        }
    }
}
