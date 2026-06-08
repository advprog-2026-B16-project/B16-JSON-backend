package id.ac.ui.cs.advprog.jsonbackend.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletTransactionConstraintInitializerTest {

    @Test
    void runShouldApplyConstraintsForPostgresql() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        ApplicationArguments args = mock(ApplicationArguments.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL 16");

        WalletTransactionConstraintInitializer initializer =
                new WalletTransactionConstraintInitializer(jdbcTemplate, dataSource);

        initializer.run(args);

        verify(jdbcTemplate).execute(contains("DROP CONSTRAINT IF EXISTS \"wallet_transactions_type_check\""));
        verify(jdbcTemplate).execute(contains("ADD CONSTRAINT \"wallet_transactions_type_check\""));
        verify(jdbcTemplate).execute(contains("DROP CONSTRAINT IF EXISTS \"orders_order_status_check\""));
        verify(jdbcTemplate).execute(contains("ADD CONSTRAINT \"orders_order_status_check\""));
        verify(connection).close();
    }

    @Test
    void runShouldSkipConstraintsForNonPostgresql() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        ApplicationArguments args = mock(ApplicationArguments.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("H2");

        WalletTransactionConstraintInitializer initializer =
                new WalletTransactionConstraintInitializer(jdbcTemplate, dataSource);

        initializer.run(args);

        verify(jdbcTemplate, never()).execute(contains("wallet_transactions"));
        verify(jdbcTemplate, never()).execute(contains("orders"));
        verify(connection, times(1)).close();
    }
}
