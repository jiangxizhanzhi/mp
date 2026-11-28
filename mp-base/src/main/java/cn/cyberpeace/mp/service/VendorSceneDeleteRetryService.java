package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/** Retry the normal scene lifecycle for explicitly closed instances whose deletion failed. */
@Service
@Slf4j
public class VendorSceneDeleteRetryService {
    private static final String TABLE = "mp_vendor_scene_delete_retry";
    private static final String ELIGIBLE = "s.deleted=0 AND s.event=9 AND s.event_status=5 AND s.update_time<? "
            + "AND EXISTS (SELECT 1 FROM mp_cscene_cscene_instance i WHERE i.scene=s.id AND i.deleted=1) "
            + "AND NOT EXISTS (SELECT 1 FROM mp_cscene_cscene_instance i WHERE i.scene=s.id AND i.deleted=0) "
            + "AND EXISTS (SELECT 1 FROM mp_scene_scene_element e WHERE e.scene=s.id AND e.vendor IN (1,2,3))";
    private final JdbcTemplate jdbc;
    private final TransactionTemplate store;
    private final ReentrantLock running = new ReentrantLock();

    public VendorSceneDeleteRetryService(JdbcTemplate jdbc, PlatformTransactionManager tx) {
        this.jdbc = jdbc;
        store = new TransactionTemplate(tx);
        store.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public void retry(Consumer<String> deleteScene) {
        if (!running.tryLock()) return;
        try {
            Timestamp old = new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1));
            Timestamp cooldown = new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));
            List<String> candidates = jdbc.queryForList("SELECT s.id FROM mp_scene_scene s LEFT JOIN " + TABLE
                    + " r ON r.scene_id=s.id WHERE " + ELIGIBLE + " AND (r.scene_id IS NULL OR r.updated_at<?) "
                    + "ORDER BY COALESCE(r.updated_at,s.update_time),s.id", String.class, old, cooldown);
            int submitted = 0;
            for (String scene : candidates) {
                if (submitted >= 8 || Thread.currentThread().isInterrupted()) break;
                String claim = TextUtils.uuid();
                if (!claim(scene, claim, cooldown)) continue;
                Integer current = jdbc.queryForObject("SELECT COUNT(*) FROM mp_scene_scene s WHERE s.id=? AND " + ELIGIBLE,
                        Integer.class, scene, old);
                if (current == null || current != 1) continue;
                submitted++;
                try {
                    deleteScene.accept(scene);
                    log.info("重试已关闭实验的厂商场景回收，scene={}, claim={}", scene, claim);
                } catch (RuntimeException e) {
                    log.warn("厂商场景回收重试未完成，scene={}", scene, e);
                }
            }
        } finally {
            running.unlock();
        }
    }

    private boolean claim(String scene, String token, Timestamp cooldown) {
        try {
            return Objects.requireNonNull(store.execute(tx -> jdbc.update("INSERT INTO " + TABLE
                    + " (scene_id,claim_id,attempts,updated_at) VALUES (?,?,1,CURRENT_TIMESTAMP)", scene, token))) == 1;
        } catch (DuplicateKeyException e) {
            return Objects.requireNonNull(store.execute(tx -> jdbc.update("UPDATE " + TABLE
                    + " SET claim_id=?,attempts=attempts+1,updated_at=CURRENT_TIMESTAMP WHERE scene_id=? AND updated_at<?",
                    token, scene, cooldown))) == 1;
        }
    }
}
