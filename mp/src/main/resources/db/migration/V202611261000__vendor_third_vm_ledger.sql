CREATE TABLE mp_vendor_third_vm_ledger (
    vmid INTEGER NOT NULL PRIMARY KEY,
    owner_key VARCHAR(160),
    scene_id VARCHAR(64),
    element_id VARCHAR(64),
    port_id VARCHAR(64),
    template_id VARCHAR(64) NOT NULL,
    request_name VARCHAR(100) NOT NULL,
    node_name VARCHAR(128) NOT NULL,
    status VARCHAR(24) NOT NULL,
    stage VARCHAR(64) NOT NULL,
    task_id VARCHAR(512),
    observed INTEGER DEFAULT 0 NOT NULL,
    ip VARCHAR(32),
    mac_addr VARCHAR(32),
    netmask VARCHAR(32),
    gateway VARCHAR(32),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_v3_vm_owner UNIQUE (owner_key),
    CONSTRAINT uk_v3_vm_name UNIQUE (request_name)
);
CREATE INDEX idx_v3_vm_scene_element ON mp_vendor_third_vm_ledger (scene_id, element_id);
