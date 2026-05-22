package id.ac.ui.cs.advprog.jsonbackend.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ApplicationMetrics {

    private final MeterRegistry meterRegistry;

    public void recordLoginAttempt(boolean success) {
        Counter.builder("auth.login.attempts")
                .tag("result", success ? "success" : "failure")
                .register(meterRegistry)
                .increment();
    }

    public void recordRegistration() {
        Counter.builder("auth.registrations")
                .register(meterRegistry)
                .increment();
    }

    public void recordProfileUpdate() {
        Counter.builder("auth.profile.updates")
                .register(meterRegistry)
                .increment();
    }

    public void recordTransaction(String type, long amount) {
        Counter.builder("wallet.transactions")
                .tag("type", type)
                .register(meterRegistry)
                .increment(amount);
    }

    public void recordTransactionDuration(long durationMs) {
        Timer.builder("wallet.transaction.duration")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordOrderCreated(BigDecimal amount) {
        Counter.builder("order.created")
                .register(meterRegistry)
                .increment();
        meterRegistry.gauge("order.last.amount", amount, BigDecimal::doubleValue);
    }

    public void recordOrderStatusChange(String status) {
        Counter.builder("order.status.change")
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }
}
