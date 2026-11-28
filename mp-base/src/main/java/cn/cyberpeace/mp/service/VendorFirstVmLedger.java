package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

/** Preserve a scene's exact creation name before sending the first remote request. */
@Service
public class VendorFirstVmLedger {
    private static final String TABLE = "mp_vendor_first_vm_ledger";
    private final JdbcTemplate jdbc;
    private final TransactionTemplate store;

    public VendorFirstVmLedger(JdbcTemplate jdbc, PlatformTransactionManager tx) {
        this.jdbc = jdbc;
        store = new TransactionTemplate(tx);
        store.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Getter
    @AllArgsConstructor
    static final class Lease {
        private final String name, owner, scene, element, image, uuid, status;
    }

    Lease byOwner(String scene, String element) {
        return query(" WHERE owner_key=?", owner(scene, element)).stream().findFirst().orElse(null);
    }

    private String owner(String scene, String element) {
        if (scene == null || scene.isEmpty() || element == null || element.isEmpty())
            throw new MpRuntimeException("厂商一场景和节点标识不能为空");
        return scene + ":" + element;
    }

    private List<Lease> query(String where, Object... args) {
        return Objects.requireNonNull(store.execute(tx -> jdbc.query(
                "SELECT request_name,owner_key,scene_id,element_id,image_id,vm_uuid,status FROM " + TABLE + where,
                (rs, n) -> new Lease(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)), args)));
    }

    Lease reserve(String scene, String element, String image, String name) {
        String owner = owner(scene, element);
        try {
            write("INSERT INTO " + TABLE + " (request_name,owner_key,scene_id,element_id,image_id,status,updated_at) "
                    + "VALUES (?,?,?,?,?,'SUBMITTING',CURRENT_TIMESTAMP)", name, owner, scene, element, image);
        } catch (DuplicateKeyException e) {
            throw new MpRuntimeException("厂商一节点已有创建记录，请先核对原请求");
        }
        return byOwner(scene, element);
    }

    void created(Lease lease, String uuid) {
        int changed = write("UPDATE " + TABLE + " SET vm_uuid=?,status='CREATED',updated_at=CURRENT_TIMESTAMP "
                        + "WHERE request_name=? AND owner_key=? AND (vm_uuid IS NULL OR vm_uuid=?) AND status IN ('SUBMITTING','UNCERTAIN','CREATED')",
                uuid, lease.name, lease.owner, uuid);
        if (changed != 1) throw new MpRuntimeException("厂商一创建资源与场景台账关联未确认，uuid=" + uuid);
    }

    void uncertain(Lease lease) {
        write("UPDATE " + TABLE + " SET status='UNCERTAIN',updated_at=CURRENT_TIMESTAMP "
                + "WHERE request_name=? AND owner_key=? AND status='SUBMITTING'", lease.name, lease.owner);
    }

    void deleted(Lease lease) {
        write("UPDATE " + TABLE + " SET status='DELETED',owner_key=NULL,updated_at=CURRENT_TIMESTAMP "
                + "WHERE request_name=? AND owner_key=?", lease.name, lease.owner);
    }

    private int write(String sql, Object... args) {
        return Objects.requireNonNull(store.execute(tx -> jdbc.update(sql, args)));
    }
}
