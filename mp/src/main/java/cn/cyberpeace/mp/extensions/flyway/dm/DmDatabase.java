package cn.cyberpeace.mp.extensions.flyway.dm;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.JdbcUtils;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Slf4j
public class DmDatabase extends Database<DmConnection> {

    public DmDatabase(Configuration configuration, JdbcConnectionFactory jdbcConnectionFactory, StatementInterceptor statementInterceptor) {
        super(configuration, jdbcConnectionFactory, statementInterceptor);
    }

    @Override
    public String getRawCreateScript(Table table, boolean baseline) {
        String tablespace =
                        configuration.getTablespace() == null
                        ? ""
                        : " TABLESPACE \"" + configuration.getTablespace() + "\"";

        String baselineMarker = "";
        if (baseline) {
            baselineMarker = " AS SELECT" +
                    "     1 as \"installed_rank\"," +
                    "     '" + configuration.getBaselineVersion() + "' as \"version\"," +
                    "     '" + configuration.getBaselineDescription() + "' as \"description\"," +
                    "     '" + MigrationType.BASELINE + "' as \"type\"," +
                    "     '" + configuration.getBaselineDescription() + "' as \"script\"," +
                    "     NULL as \"checksum\"," +
                    "     '" + getInstalledBy() + "' as \"installed_by\"," +
                    "     CURRENT_TIMESTAMP as \"installed_on\"," +
                    "     0 as \"execution_time\"," +
                    "     TRUE as \"success\"\n";
        }

        return "CREATE TABLE " + table + " (\n" +
                "    `installed_rank` INT NOT NULL,\n" +
                "    `version` VARCHAR(50),\n" +
                "    `description` VARCHAR(200) NOT NULL,\n" +
                "    `type` VARCHAR(20) NOT NULL,\n" +
                "    `script` VARCHAR(1000) NOT NULL,\n" +
                "    `checksum` INT,\n" +
                "    `installed_by` VARCHAR(100) NOT NULL,\n" +
                "    `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    `execution_time` INT NOT NULL,\n" +
                "    `success` TINYINT NOT NULL,\n" +
                "    CONSTRAINT " + getConstraintName(table.getName()) + " PRIMARY KEY (`installed_rank`)\n" +
                ")" + tablespace +
                baselineMarker +
                ";\n" +
                "CREATE INDEX `" + table.getName() + "_s_idx` ON " + table + " (`success`);";
    }

    protected String getConstraintName(String tableName) {
        return "`" + tableName + "_pk`";
    }

    @Override
    protected DmConnection doGetConnection(Connection connection) {
        return new DmConnection(this, connection);
    }

    @Override
    protected MigrationVersion determineVersion() {
        DatabaseMetaData databaseMetaData = JdbcUtils.getDatabaseMetaData(rawMainJdbcConnection);
        String databaseProductVersion = JdbcUtils.getDatabaseProductVersion(databaseMetaData);
        return MigrationVersion.fromVersion(databaseProductVersion);
    }

    @Override
    public final void ensureSupported() {
        ensureDatabaseIsRecentEnough("8.0");
        ensureDatabaseNotOlderThanOtherwiseRecommendUpgradeToFlywayEdition("8.0", org.flywaydb.core.internal.license.Edition.ENTERPRISE);
        recommendFlywayUpgradeIfNecessary("8.0");
    }

    @Override
    protected String doGetCurrentUser() throws SQLException {
        return getMainConnection().getJdbcTemplate().queryForString("SELECT SUBSTRING_INDEX(USER(),'@',1)");
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

    @Override
    public boolean supportsChangingCurrentSchema() {
        return true;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    public String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    public boolean catalogIsSchema() {
        return true;
    }

    @Override
    public boolean useSingleConnection() {
        return true;
    }
}