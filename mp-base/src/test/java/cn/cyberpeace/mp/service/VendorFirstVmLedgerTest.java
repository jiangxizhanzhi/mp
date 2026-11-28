package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendorFirstVmLedgerTest {
    JdbcTemplate jdbc;
    DataSourceTransactionManager tx;
    VendorFirstVmLedger ledger;

    @BeforeEach void setup() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        tx = new DataSourceTransactionManager(source);
        String ddl = Files.readString(Paths.get("../mp/src/main/resources/db/migration/V202611280900__vendor_reclaim_recovery.sql"));
        for (String stmt : ddl.split(";")) if (!stmt.trim().isEmpty()) jdbc.execute(stmt);
        ledger = new VendorFirstVmLedger(jdbc, tx);
    }

    @Test void requestSurvivesRollbackAndRestartAndPreventsDuplicateCreation() {
        new TransactionTemplate(tx).execute(status -> {
            ledger.reserve("scene", "node", "image", "MP_original");
            status.setRollbackOnly();
            return null;
        });
        VendorFirstVmLedger restarted = new VendorFirstVmLedger(jdbc, tx);
        assertEquals("MP_original", restarted.byOwner("scene", "node").getName());
        assertThrows(MpRuntimeException.class, () -> restarted.reserve("scene", "node", "image", "MP_duplicate"));
        VendorFirstVmLedger.Lease lease = restarted.byOwner("scene", "node");
        restarted.created(lease, "vm-original");
        assertThrows(MpRuntimeException.class, () -> restarted.created(lease, "vm-other"));
        restarted.deleted(lease);
        assertNull(restarted.byOwner("scene", "node"));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM mp_vendor_first_vm_ledger WHERE status='DELETED'", Integer.class));
        assertNotNull(restarted.reserve("scene", "node", "image", "MP_new_attempt"));
    }
}
