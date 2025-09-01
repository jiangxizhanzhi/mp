package cn.cyberpeace.mp.extensions.flyway.dm;

import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DmSchema extends Schema<DmDatabase, DmTable> {

    DmSchema(JdbcTemplate jdbcTemplate, DmDatabase database, String name) {
        super(jdbcTemplate, database, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return name.equalsIgnoreCase(jdbcTemplate.queryForString("SELECT SF_GET_SCHEMA_NAME_BY_ID(CURRENT_SCHID());"));
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        List<String> params = new ArrayList<>(Arrays.asList(name, name, name, name, name));

        return jdbcTemplate.queryForInt("SELECT SUM(found) FROM ("
                        + "(SELECT 1 as found FROM SYS.ALL_TABLES WHERE OWNER=?) UNION ALL "
                        + "(SELECT 1 as found FROM SYS.ALL_VIEWS WHERE OWNER=? LIMIT 1) UNION ALL "
                        + "(SELECT 1 as found FROM SYS.ALL_CONSTRAINTS WHERE OWNER=? LIMIT 1) UNION ALL "
                        + "(SELECT 1 as found FROM SYS.ALL_TRIGGERS WHERE OWNER=?  LIMIT 1) UNION ALL "
                        + "(SELECT 1 as found FROM SYS.ALL_PROCEDURES WHERE OWNER=? LIMIT 1)"
                        + ") as all_found",
                params.toArray(new String[0])
        ) == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        jdbcTemplate.execute("CREATE SCHEMA " + database.quote(name));
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.execute("DROP SCHEMA " + database.quote(name));
    }

    @Override
    protected void doClean() throws SQLException {
        for (String statement : cleanRoutines()) {
            jdbcTemplate.execute(statement);
        }

        for (String statement : cleanViews()) {
            jdbcTemplate.execute(statement);
        }

//        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (Table table : allTables()) {
            table.drop();
        }
//        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        // MariaDB 10.3 and newer only
        for (String statement : cleanSequences()) {
            jdbcTemplate.execute(statement);
        }
    }

    private List<String> cleanRoutines() throws SQLException {
        List<Map<String, String>> routineNames =
                jdbcTemplate.queryForList(
                        "SELECT OBJECT_NAME as 'N', OBJECT_TYPE as 'T' FROM SYS.ALL_PROCEDURES WHERE OWNER=?",
                        name);

        List<String> statements = new ArrayList<>();
        for (Map<String, String> row : routineNames) {
            String routineName = row.get("N");
            String routineType = row.get("T");
            statements.add("DROP " + routineType + " " + database.quote(name, routineName));
        }
        return statements;
    }

    private List<String> cleanViews() throws SQLException {
        List<String> viewNames =
                jdbcTemplate.queryForStringList(
                        "SELECT VIEW_NAME FROM SYS.ALL_VIEWS WHERE OWNER=?", name);

        List<String> statements = new ArrayList<>();
        for (String viewName : viewNames) {
            statements.add("DROP VIEW " + database.quote(name, viewName));
        }
        return statements;
    }

    private List<String> cleanSequences() throws SQLException {
        List<String> names =
                jdbcTemplate.queryForStringList(
                        "SELECT SEQUENCE_NAME FROM SYS.ALL_SEQUENCES WHERE OWNER=?", name);

        List<String> statements = new ArrayList<>();
        for (String name : names) {
            statements.add("DROP SEQUENCE " + database.quote(this.name, name));
        }
        return statements;
    }

    @Override
    protected DmTable[] doAllTables() throws SQLException {
        List<String> tableNames = jdbcTemplate.queryForStringList(
                "SELECT TABLE_NAME FROM SYS.ALL_TABLES WHERE OWNER=?", name);

        DmTable[] tables = new DmTable[tableNames.size()];
        for (int i = 0; i < tableNames.size(); i++) {
            tables[i] = new DmTable(jdbcTemplate, database, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new DmTable(jdbcTemplate, database, this, tableName);
    }
}