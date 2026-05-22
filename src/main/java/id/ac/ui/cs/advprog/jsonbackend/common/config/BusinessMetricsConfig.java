package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.repository.TransactionRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.RefundRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BusinessMetricsConfig {

    private static final int LOW_STOCK_THRESHOLD = 5;

    @Bean
    @ConditionalOnBean({
            UserRepository.class,
            ProductRepository.class,
            OrderRepository.class,
            PaymentRepository.class,
            WalletRepository.class,
            TransactionRepository.class,
            RefundRepository.class
    })
    public MeterBinder businessMetrics(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            RefundRepository refundRepository
    ) {
        return registry -> {
            gauge(registry, "json_backend_users_total", "Total registered users", userRepository::count);
            gauge(registry, "json_backend_products_total", "Total products", productRepository::count);
            gauge(registry, "json_backend_products_stock_units", "Total product stock units", productRepository::sumStock);
            gauge(registry, "json_backend_products_low_stock_total", "Products with low stock", () -> productRepository.countByStockLessThanEqual(LOW_STOCK_THRESHOLD));
            gauge(registry, "json_backend_wallets_total", "Total wallets", walletRepository::count);
            gauge(registry, "json_backend_wallet_balance_total", "Total wallet balance", walletRepository::sumBalance);

            for (OrderStatus status : OrderStatus.values()) {
                gauge(registry, "json_backend_orders_total", "Orders by status",
                        () -> orderRepository.countByOrderStatus(status), "status", tagValue(status.name()));
            }

            for (PaymentStatus status : PaymentStatus.values()) {
                gauge(registry, "json_backend_payments_total", "Payments by status",
                        () -> paymentRepository.countByStatus(status), "status", tagValue(status.name()));
            }

            for (TransactionStatus status : TransactionStatus.values()) {
                gauge(registry, "json_backend_refunds_total", "Refund requests by status",
                        () -> refundRepository.countByStatus(status), "status", tagValue(status.name()));
            }

            for (TransactionType type : TransactionType.values()) {
                for (TransactionStatus status : TransactionStatus.values()) {
                    gauge(registry, "json_backend_wallet_transactions_total", "Wallet transactions by type and status",
                            () -> transactionRepository.countByTypeAndStatus(type, status),
                            "type", tagValue(type.name()), "status", tagValue(status.name()));
                    gauge(registry, "json_backend_wallet_transaction_amount_total", "Wallet transaction amount by type and status",
                            () -> transactionRepository.sumAmountByTypeAndStatus(type, status),
                            "type", tagValue(type.name()), "status", tagValue(status.name()));
                }
            }
        };
    }

    private void gauge(MeterRegistry registry, String name, String description, Supplier<? extends Number> supplier, String... tags) {
        Gauge.builder(name, () -> number(supplier))
                .description(description)
                .tags(tags)
                .register(registry);
    }

    private double number(Supplier<? extends Number> supplier) {
        Number value = supplier.get();
        if (value == null) {
            return 0;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.doubleValue();
        }
        return value.doubleValue();
    }

    private String tagValue(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
