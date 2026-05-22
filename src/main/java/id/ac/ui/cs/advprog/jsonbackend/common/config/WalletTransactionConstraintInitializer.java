package id.ac.ui.cs.advprog.jsonbackend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class WalletTransactionConstraintInitializer implements ApplicationRunner {

    private static final String POSTGRESQL = "postgresql";
    private static final String DROP_TRANSACTION_TYPE_CHECK = """
            ALTER TABLE "wallet_transactions"
            DROP CONSTRAINT IF EXISTS "wallet_transactions_type_check"
            """;
    private static final String ADD_TRANSACTION_TYPE_CHECK = """
            ALTER TABLE "wallet_transactions"
            ADD CONSTRAINT "wallet_transactions_type_check"
            CHECK ("type" IN ('TOP_UP', 'WITHDRAW', 'PAYMENT', 'REFUND', 'PAYOUT'))
            """;
    private static final String DROP_ORDER_STATUS_CHECK = """
            ALTER TABLE "orders"
            DROP CONSTRAINT IF EXISTS "orders_order_status_check"
            """;
    private static final String ADD_ORDER_STATUS_CHECK = """
            ALTER TABLE "orders"
            ADD CONSTRAINT "orders_order_status_check"
            CHECK ("order_status" IN ('PENDING', 'PAID', 'PURCHASED', 'SHIPPED', 'COMPLETED', 'DONE', 'CANCELLED'))
            """;

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        if (!isPostgreSql()) {
            return;
        }

        jdbcTemplate.execute(DROP_TRANSACTION_TYPE_CHECK);
        jdbcTemplate.execute(ADD_TRANSACTION_TYPE_CHECK);
        jdbcTemplate.execute(DROP_ORDER_STATUS_CHECK);
        jdbcTemplate.execute(ADD_ORDER_STATUS_CHECK);
    }

    private boolean isPostgreSql() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData()
                    .getDatabaseProductName()
                    .toLowerCase(Locale.ROOT)
                    .contains(POSTGRESQL);
        }
    }
}
