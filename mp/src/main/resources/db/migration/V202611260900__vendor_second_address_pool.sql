CREATE TABLE mp_vendor_second_address_pool (
    ip VARCHAR(15) NOT NULL PRIMARY KEY,
    status VARCHAR(24) NOT NULL,
    owner_key VARCHAR(160),
    scene_id VARCHAR(64),
    element_id VARCHAR(64),
    port_id VARCHAR(64),
    request_name VARCHAR(100),
    vm_uuid VARCHAR(128),
    mac_addr VARCHAR(64),
    seen_in_inventory INTEGER DEFAULT 0 NOT NULL,
    reason VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_v2_address_owner UNIQUE (owner_key)
);
CREATE INDEX idx_v2_address_vm ON mp_vendor_second_address_pool (vm_uuid);
