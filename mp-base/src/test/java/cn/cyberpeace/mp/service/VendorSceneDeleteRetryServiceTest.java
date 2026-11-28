package cn.cyberpeace.mp.service;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class VendorSceneDeleteRetryServiceTest {
    private JdbcTemplate jdbc;
    private DataSourceTransactionManager tx;
    private VendorSceneDeleteRetryService service;
    private final Timestamp old = new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2));

    @BeforeEach void setup() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        tx = new DataSourceTransactionManager(source);
        String ddl = Files.readString(Paths.get("../mp/src/main/resources/db/migration/V202611280900__vendor_reclaim_recovery.sql"));
        for (String stmt : ddl.split(";")) if (!stmt.trim().isEmpty()) jdbc.execute(stmt);
        jdbc.execute("CREATE TABLE mp_scene_scene (id VARCHAR(64) PRIMARY KEY,deleted INT,event INT,event_status INT,update_time TIMESTAMP)");
        jdbc.execute("CREATE TABLE mp_cscene_cscene_instance (scene VARCHAR(64),deleted INT)");
        jdbc.execute("CREATE TABLE mp_scene_scene_element (scene VARCHAR(64),vendor INT)");
        service = new VendorSceneDeleteRetryService(jdbc, tx);
    }

    private void seed(String scene, int vendor) {
        jdbc.update("INSERT INTO mp_scene_scene VALUES (?,0,9,5,?)", scene, old);
        jdbc.update("INSERT INTO mp_cscene_cscene_instance VALUES (?,1)", scene);
        jdbc.update("INSERT INTO mp_scene_scene_element VALUES (?,?)", scene, vendor);
    }

    @Test void retriesOnlyExplicitlyClosedFailedVendorScenes() {
        seed("failed", 2);
        seed("still-live", 3);
        jdbc.update("INSERT INTO mp_cscene_cscene_instance VALUES ('still-live',0)");
        seed("not-deleting", 1);
        jdbc.update("UPDATE mp_scene_scene SET event=2 WHERE id='not-deleting'");
        seed("already-deleted", 2);
        jdbc.update("UPDATE mp_scene_scene SET deleted=1 WHERE id='already-deleted'");
        seed("local", 0);
        seed("no-close-evidence", 2);
        jdbc.update("DELETE FROM mp_cscene_cscene_instance WHERE scene='no-close-evidence'");
        List<String> queued = new ArrayList<>();
        service.retry(queued::add);
        assertEquals(List.of("failed"), queued);
        new VendorSceneDeleteRetryService(jdbc, tx).retry(queued::add);
        assertEquals(1, queued.size());
        jdbc.update("UPDATE mp_vendor_scene_delete_retry SET updated_at=? WHERE scene_id='failed'", old);
        service.retry(queued::add);
        assertEquals(List.of("failed", "failed"), queued);
    }

    @Test void oneFailureDoesNotBlockOtherScenesAndBatchIsBounded() {
        for (int i = 0; i < 12; i++) seed(String.format("scene-%02d", i), 2);
        List<String> attempted = new ArrayList<>();
        service.retry(scene -> {
            attempted.add(scene);
            if (scene.equals("scene-00")) throw new IllegalStateException("fixture failure");
        });
        assertEquals(8, attempted.size());
        service.retry(attempted::add);
        assertEquals(12, attempted.size());
        assertEquals(12, attempted.stream().distinct().count());
    }
}
