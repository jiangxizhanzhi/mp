-- Based on the exported data: 48 visible MP entity devices,
-- one existing access configuration (SSH is its first protocol), 47 missing.
-- Run in a single database session. This file has not been applied to the server.

SET @ssh_ip = '192.168.142.10';

-- Expected: 48. Scope comes from MP, not all 75 cpcloud entity devices.
SELECT COUNT(*) AS target_count
FROM mp.mp_devops_target AS t
JOIN cpcloud.device AS d ON d.uuid = t.uuid
WHERE t.image_type = 'entity'
  AND t.is_hide = 0
  AND d.deleted = 0;

START TRANSACTION;

-- Update the existing SSH host; preserve its credentials and other settings.
UPDATE cpcloud.device_entity_access AS a
JOIN mp.mp_devops_target AS t ON t.uuid = a.device_uuid
JOIN cpcloud.device AS d ON d.uuid = t.uuid
SET a.access_protocols = JSON_SET(a.access_protocols, '$[0].hostname', @ssh_ip)
WHERE t.image_type = 'entity'
  AND t.is_hide = 0
  AND d.deleted = 0
  AND JSON_UNQUOTE(JSON_EXTRACT(a.access_protocols, '$[0].protocol')) = 'ssh';

-- Expected on first execution: 1 changed row.
SELECT ROW_COUNT() AS updated_access_rows;

-- Add an access configuration only for devices that do not have one.
INSERT INTO cpcloud.device_entity_access
    (created_at, device_uuid, access_type, switch_name, switch_port,
     port_name, ip, mac, access_protocols)
SELECT UTC_TIMESTAMP(), d.uuid, 'remote_address', '', NULL,
       'mgmt', @ssh_ip, '',
       JSON_ARRAY(JSON_OBJECT(
           'protocol', 'ssh',
           'hostname', @ssh_ip,
           'port', 22,
           'username', '',
           'password', ''
       ))
FROM mp.mp_devops_target AS t
JOIN cpcloud.device AS d ON d.uuid = t.uuid
WHERE t.image_type = 'entity'
  AND t.is_hide = 0
  AND d.deleted = 0
  AND NOT EXISTS (
      SELECT 1
      FROM cpcloud.device_entity_access AS a
      WHERE a.device_uuid = d.uuid
  );

-- Expected on first execution: 47 inserted rows; repeating inserts none.
SELECT ROW_COUNT() AS inserted_access_rows;

-- Expected: 48 devices configured with the requested SSH host.
SELECT COUNT(DISTINCT t.uuid) AS configured_device_count
FROM mp.mp_devops_target AS t
JOIN cpcloud.device AS d ON d.uuid = t.uuid
JOIN cpcloud.device_entity_access AS a ON a.device_uuid = t.uuid
WHERE t.image_type = 'entity'
  AND t.is_hide = 0
  AND d.deleted = 0
  AND JSON_UNQUOTE(JSON_EXTRACT(a.access_protocols, '$[0].protocol')) = 'ssh'
  AND JSON_UNQUOTE(JSON_EXTRACT(a.access_protocols, '$[0].hostname')) = @ssh_ip;

COMMIT;
