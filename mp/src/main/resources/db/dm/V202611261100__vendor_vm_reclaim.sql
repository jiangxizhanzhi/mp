CREATE TABLE mp_vendor_vm_reclaim (
    vendor INTEGER NOT NULL,
    vm_id VARCHAR(64) NOT NULL,
    request_name VARCHAR(128) NOT NULL,
    scene_id VARCHAR(64) NOT NULL,
    element_id VARCHAR(64) NOT NULL,
    status VARCHAR(24) NOT NULL,
    claim_id VARCHAR(64) NOT NULL,
    attempts INTEGER DEFAULT 0 NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (vendor, vm_id)
);
CREATE INDEX idx_vendor_vm_reclaim_status ON mp_vendor_vm_reclaim (vendor, status, updated_at);
