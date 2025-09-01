package cn.cyberpeace.mp.extensions.flyway.dm;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.util.StringUtils;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
public class DmConnection extends Connection<DmDatabase> {

    public DmConnection(DmDatabase database, java.sql.Connection connection) {
        super(database, connection);
    }

    @Override
    protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
        return jdbcTemplate.queryForString("SELECT SF_GET_SCHEMA_NAME_BY_ID(CURRENT_SCHID());");
    }

    @Override
    public void doChangeCurrentSchemaOrSearchPathTo(String schema) throws SQLException {
        if (StringUtils.hasLength(schema)) {
            jdbcTemplate.getConnection().setCatalog(schema);
        } else {
            try {
                // Weird hack to switch back to no database selected...
                String newDb = database.quote(UUID.randomUUID().toString());
                jdbcTemplate.execute("CREATE SCHEMA " + newDb);
                jdbcTemplate.execute("USE " + newDb);
                jdbcTemplate.execute("DROP SCHEMA " + newDb);
            } catch (Exception e) {
                log.warn("Unable to restore connection to having no default schema: " + e.getMessage());
            }
        }
    }

    @Override
    protected Schema doGetCurrentSchema() throws SQLException {
        String schemaName = getCurrentSchemaNameOrSearchPath();

        // #2206: MySQL and MariaDB can have URLs where no current schema is set, so we must handle this case explicitly.
        return schemaName == null ? null : getSchema(schemaName);
    }

    @Override
    public Schema getSchema(String name) {
        return new DmSchema(jdbcTemplate, database, name);
    }

    @Override
    public <T> T lock(Table table, Callable<T> callable) {
        return super.lock(table, callable);
    }
}