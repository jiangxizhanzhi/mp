CREATE TABLE mp_vendor_first_vm_ledger (
    request_name VARCHAR(128) PRIMARY KEY,
    owner_key VARCHAR(129) UNIQUE,
    scene_id VARCHAR(64) NOT NULL,
    element_id VARCHAR(64) NOT NULL,
    image_id VARCHAR(128) NOT NULL,
    vm_uuid VARCHAR(128),
    status VARCHAR(24) NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_v1_vm_scene ON mp_vendor_first_vm_ledger (scene_id, element_id);

CREATE TABLE mp_vendor_scene_delete_retry (
    scene_id VARCHAR(64) PRIMARY KEY,
    claim_id VARCHAR(64) NOT NULL,
    attempts INTEGER DEFAULT 0 NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
