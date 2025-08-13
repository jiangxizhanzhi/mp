CREATE TABLE "MP"."mp_auth_client_details"
(
    "pk"                      INT IDENTITY(2, 1) NOT NULL,
    "client_id"               VARCHAR(128 char ) NULL,
        "resource_ids"            VARCHAR(256 char) NULL,
        "client_secret"           VARCHAR(256 char) NULL,
        "type"                    INT DEFAULT 1 NOT NULL,
        "scope"                   VARCHAR(256 char) NULL,
        "authorized_grant_types"  VARCHAR(256 char) NULL,
        "web_server_redirect_uri" VARCHAR(256 char) NULL,
        "authorities"             VARCHAR(256 char) NULL,
        "access_token_validity"   INT NULL,
        "refresh_token_validity"  INT NULL,
        "additional_information"  VARCHAR(4096 char) NULL,
        "autoapprove"             VARCHAR(256 char) NULL
    );

CREATE TABLE "MP"."mp_auth_org"
(
    "pk"          INT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) DEFAULT '' NOT NULL,
        "parent"      VARCHAR(64 char) DEFAULT '' NOT NULL,
        "identifier"  VARCHAR(64 char) NOT NULL,
        "name"        VARCHAR(64 char) NOT NULL,
        "path"        VARCHAR(180 char) NULL,
        "description" VARCHAR(1024 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_org_permission"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NOT NULL,
        "owner"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "can_read"    TINYINT NOT NULL,
        "can_create"  TINYINT NOT NULL,
        "can_update"  TINYINT NOT NULL,
        "can_delete"  TINYINT NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_role"
(
    "pk"          INT IDENTITY(3, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) DEFAULT '' NOT NULL,
        "org"         VARCHAR(64 char) DEFAULT '' NOT NULL,
        "identifier"  VARCHAR(64 char) NOT NULL,
        "name"        VARCHAR(64 char) NOT NULL,
        "description" VARCHAR(1024 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL,
        "visible"     TINYINT DEFAULT 1 NOT NULL
    );

CREATE TABLE "MP"."mp_auth_role_permission"
(
    "pk"          INT IDENTITY(10, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NOT NULL,
        "owner"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "can_read"    TINYINT NOT NULL,
        "can_create"  TINYINT NOT NULL,
        "can_update"  TINYINT NOT NULL,
        "can_delete"  TINYINT NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_target"
(
    "pk"          INT IDENTITY(343, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "parent"      VARCHAR(64 char) NULL,
        "identifier"  VARCHAR(64 char) NOT NULL,
        "tenant"      VARCHAR(64 char) NOT NULL,
        "name"        VARCHAR(64 char) NOT NULL,
        "path"        VARCHAR(255 char) NULL,
        "type"        INT NOT NULL,
        "visible"     INT DEFAULT 1 NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_tenant"
(
    "pk"          INT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "identifier"  VARCHAR(64 char) NOT NULL,
        "name"        VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_tenant_user"
(
    "pk"          INT IDENTITY(3, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) DEFAULT '' NOT NULL,
        "org"         VARCHAR(64 char) NOT NULL,
        "user"        VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_url_ac"
(
    "pk"          INT IDENTITY(708, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "url"         VARCHAR(500 char) NOT NULL,
        "is_pattern"  INT DEFAULT 0 NOT NULL,
        "group"       VARCHAR(64 char) NOT NULL,
        "methods"     VARCHAR(100 char) NOT NULL,
        "desc"        VARCHAR(500 char) NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_url_ac_target"
(
    "pk"          INT IDENTITY(1183, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "group"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_user"
(
    "pk"                        INT IDENTITY(4, 1) NOT NULL,
    "id"                        VARCHAR(64 char ) NOT NULL,
        "tenant"                    VARCHAR(64 char) DEFAULT '' NOT NULL,
        "username"                  VARCHAR(64 char) NOT NULL,
        "identifier"                VARCHAR(64 char) NULL,
        "password"                  VARCHAR(128 char) NOT NULL,
        "token_secret"              VARCHAR(128 char) NOT NULL,
        "name"                      VARCHAR(128 char) NOT NULL,
        "nickname"                  VARCHAR(128 char) NOT NULL,
        "avatar"                    VARCHAR(2048 char) NOT NULL,
        "birthday"                  DATE NOT NULL,
        "email"                     VARCHAR(255 char) NOT NULL,
        "mobile"                    VARCHAR(64 char) NOT NULL,
        "id_card"                   VARCHAR(64 char) NOT NULL,
        "address"                   VARCHAR(255 char) NOT NULL,
        "is_enabled"                INT NOT NULL,
        "is_audited"                INT NOT NULL,
        "init_check"                INT DEFAULT 0 NOT NULL,
        "delay_remind_license_time" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP() NOT NULL,
        "description"               VARCHAR(1024 char) NULL,
        "date_joined"               TIMESTAMP(0) NULL,
        "expired_time"              TIMESTAMP(0) NOT NULL,
        "last_login"                TIMESTAMP(0) NULL,
        "create_time"               TIMESTAMP(0) NOT NULL,
        "update_time"               TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_user_online_record"
(
    "pk"               INT IDENTITY(10, 1) NOT NULL,
    "id"               VARCHAR(64 char ) NOT NULL,
        "user"             VARCHAR(64 char) NOT NULL,
        "tenant"           VARCHAR(64 char) NOT NULL,
        "start_time"       TIMESTAMP(0) NOT NULL,
        "end_time"         TIMESTAMP(0) NULL,
        "duration_minutes" INT DEFAULT 0 NOT NULL,
        "status"           INT DEFAULT 0 NOT NULL,
        "create_time"      TIMESTAMP(0) NOT NULL,
        "update_time"      TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_user_permission"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NOT NULL,
        "owner"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "can_read"    TINYINT NOT NULL,
        "can_create"  TINYINT NOT NULL,
        "can_update"  TINYINT NOT NULL,
        "can_delete"  TINYINT NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_auth_user_role"
(
    "pk"          INT IDENTITY(4, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NOT NULL,
        "user"        VARCHAR(64 char) NOT NULL,
        "role"        VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_base_local_file_mapping"
(
    "pk"                       INT IDENTITY(335, 1) NOT NULL,
    "id"                       VARCHAR(64 char ) NOT NULL,
        "group"                    VARCHAR(64 char) NOT NULL,
        "seq"                      INT NOT NULL,
        "name"                     VARCHAR(255 char) NOT NULL,
        "local_file"               VARCHAR(2048 char) NOT NULL,
        "local_file_modified_time" BIGINT NOT NULL,
        "remote_file"              VARCHAR(2048 char) NOT NULL,
        "create_time"              TIMESTAMP(6) NOT NULL,
        "update_time"              TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_common_option"
(
    "pk"          INT IDENTITY(111, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "code"        VARCHAR(255 char) NOT NULL,
        "parent_code" VARCHAR(255 char) NOT NULL,
        "zh_name"     VARCHAR(255 char) NOT NULL,
        "en_name"     VARCHAR(255 char) NOT NULL,
        "sort"        INT NOT NULL,
        "type"        VARCHAR(255 char) NOT NULL,
        "meta"        VARCHAR(2000 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_common_config"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "key"         VARCHAR(32 char) NOT NULL,
        "content"     TEXT NOT NULL,
        "create_time" TIMESTAMP(6) NOT NULL,
        "update_time" TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_cscene"
(
    "pk"                    INT IDENTITY(8, 1) NOT NULL,
    "id"                    VARCHAR(64 char ) NOT NULL,
        "name"                  VARCHAR(255 char) DEFAULT '' NOT NULL,
        "type"                  INT NOT NULL,
        "auth_type"             INT NOT NULL,
        "reference_type"        INT NOT NULL,
        "reference_meta_data"   VARCHAR(2048 char) NOT NULL,
        "logo"                  VARCHAR(2048 char) NOT NULL,
        "scene_config"          VARCHAR(64 char) NOT NULL,
        "create_user"           VARCHAR(64 char) NOT NULL,
        "create_username"       VARCHAR(64 char) NOT NULL,
        "deleted"               TINYINT NOT NULL,
        "create_time"           TIMESTAMP(6) NOT NULL,
        "update_time"           TIMESTAMP(6) NOT NULL,
        "support_partial_start" TINYINT NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_cscene_auth"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NULL,
        "owner"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "can_read"    TINYINT NULL,
        "can_create"  TINYINT NULL,
        "can_update"  TINYINT NULL,
        "can_delete"  TINYINT NULL,
        "create_time" TIMESTAMP(6) NOT NULL,
        "update_time" TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_cscene_instance"
(
    "pk"                    INT IDENTITY(4, 1) NOT NULL,
    "id"                    VARCHAR(64 char ) NOT NULL,
        "name"                  VARCHAR(255 char) DEFAULT '' NOT NULL,
        "cscene"                VARCHAR(64 char) NOT NULL,
        "type"                  INT NOT NULL,
        "reference_type"        INT NOT NULL,
        "reference_meta_data"   VARCHAR(2048 char) NOT NULL,
        "scene"                 VARCHAR(64 char) NOT NULL,
        "scene_ttl"             INT NOT NULL,
        "scene_attr"            INT NOT NULL,
        "create_user"           VARCHAR(64 char) NOT NULL,
        "create_username"       VARCHAR(64 char) NOT NULL,
        "deleted"               TINYINT NOT NULL,
        "create_time"           TIMESTAMP(6) NOT NULL,
        "update_time"           TIMESTAMP(6) NOT NULL,
        "pass_through"          TEXT NULL,
        "support_partial_start" TINYINT NOT NULL,
        "logo"                  VARCHAR(2048 char) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_cscene_instance_role"
(
    "pk"            INT IDENTITY(1, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "cscene"        VARCHAR(64 char) NOT NULL,
        "parent"        VARCHAR(64 char) NOT NULL,
        "key"           VARCHAR(64 char) NOT NULL,
        "name"          VARCHAR(64 char) NOT NULL,
        "type"          VARCHAR(64 char) NOT NULL,
        "asset_nodes"   VARCHAR(2048 char) NOT NULL,
        "visible_nodes" VARCHAR(2048 char) NOT NULL,
        "users"         VARCHAR(2048 char) NOT NULL,
        "create_time"   TIMESTAMP(6) NOT NULL,
        "update_time"   TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_cscene_role"
(
    "pk"            INT IDENTITY(10, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "cscene"        VARCHAR(64 char) NOT NULL,
        "parent"        VARCHAR(64 char) NOT NULL,
        "key"           VARCHAR(64 char) NOT NULL,
        "name"          VARCHAR(256 char) NOT NULL,
        "type"          VARCHAR(64 char) NOT NULL,
        "asset_nodes"   VARCHAR(2048 char) NOT NULL,
        "visible_nodes" VARCHAR(2048 char) NOT NULL,
        "users"         VARCHAR(2048 char) NOT NULL,
        "create_time"   TIMESTAMP(6) NOT NULL,
        "update_time"   TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_instance_user_count"
(
    "pk"          BIGINT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "user_id"     VARCHAR(64 char) NOT NULL,
        "count"       BIGINT DEFAULT 0 NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_cscene_snapshot"
(
    "pk"                   INT IDENTITY(1, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
        "cscene_id"            VARCHAR(64 char) NOT NULL,
        "cscene_instance_id"   VARCHAR(64 char) NOT NULL,
        "name"                 VARCHAR(255 char) NOT NULL,
        "source_name"          VARCHAR(255 char) NOT NULL,
        "node_id"              VARCHAR(64 char) NOT NULL,
        "snapshot_id"          VARCHAR(64 char) NOT NULL,
        "snapshot_resource_id" VARCHAR(64 char) NOT NULL,
        "deleted"              TINYINT NOT NULL,
        "create_time"          TIMESTAMP(6) NOT NULL,
        "update_time"          TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_alarm_statistics"
(
    "pk"          BIGINT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "alarm_type"  TINYINT NOT NULL,
        "alarm_count" BIGINT NOT NULL,
        "alarm_date"  TIMESTAMP(0) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_cpnode_monitor_script_config"
(
    "pk"          INT IDENTITY(4, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "agent_name"  VARCHAR(64 char) NOT NULL,
        "hostname"    VARCHAR(64 char) NOT NULL,
        "ip"          VARCHAR(64 char) NOT NULL,
        "config"      VARCHAR(2000 char) NOT NULL,
        "status"      INT NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_log_operation"
(
    "pk"           INT IDENTITY(66, 1) NOT NULL,
    "id"           VARCHAR(64 char ) NOT NULL,
        "site_type"    INT NOT NULL,
        "from"         VARCHAR(255 char) NOT NULL,
        "log_type"     INT NOT NULL,
        "op_module"    VARCHAR(255 char) NOT NULL,
        "op_module_id" INT NOT NULL,
        "op_type"      VARCHAR(255 char) NOT NULL,
        "op_type_id"   INT NOT NULL,
        "op_target"    VARCHAR(255 char) NOT NULL,
        "op_target_id" INT NOT NULL,
        "title"        VARCHAR(255 char) NOT NULL,
        "user_id"      VARCHAR(64 char) NOT NULL,
        "user_name"    VARCHAR(64 char) NOT NULL,
        "user_type"    VARCHAR(64 char) NOT NULL,
        "content"      TEXT NOT NULL,
        "description"  VARCHAR(1024 char) NOT NULL,
        "op_time"      TIMESTAMP(0) NOT NULL,
        "op_ip"        VARCHAR(64 char) NOT NULL,
        "create_time"  TIMESTAMP(0) NOT NULL,
        "update_time"  TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_screenshot"
(
    "pk"            BIGINT IDENTITY(1, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "file"          VARCHAR(256 char) NOT NULL,
        "file_name"     VARCHAR(256 char) NOT NULL,
        "file_size"     BIGINT NOT NULL,
        "scene_name"    VARCHAR(256 char) NOT NULL,
        "user_id"       VARCHAR(64 char) NOT NULL,
        "user_name"     VARCHAR(64 char) NOT NULL,
        "user_org_name" VARCHAR(1024 char) NOT NULL,
        "scene"         VARCHAR(64 char) DEFAULT '' NOT NULL,
        "node_id"       VARCHAR(64 char) DEFAULT '' NOT NULL,
        "create_time"   TIMESTAMP(0) NOT NULL,
        "update_time"   TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_system_alarm"
(
    "pk"           BIGINT IDENTITY(2, 1) NOT NULL,
    "id"           VARCHAR(64 char ) NOT NULL,
        "alarm_type"   TINYINT NOT NULL,
        "alarm_msg"    VARCHAR(256 char) NOT NULL,
        "hostname"     VARCHAR(128 char) NOT NULL,
        "servername"   VARCHAR(128 char) NOT NULL,
        "running_time" INT NOT NULL,
        "alarm_status" TINYINT NOT NULL,
        "create_time"  TIMESTAMP(0) NOT NULL,
        "update_time"  TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_target"
(
    "pk"                   INT IDENTITY(16, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
        "uuid"                 VARCHAR(64 char) NOT NULL,
        "name"                 VARCHAR(164 char) NOT NULL,
        "logo"                 VARCHAR(255 char) NULL,
        "role"                 TINYINT NULL,
        "main_category"        INT NULL,
        "sub_category"         INT NULL,
        "third_category"       INT NULL,
        "image_type"           VARCHAR(32 char) NULL,
        "is_public"            TINYINT NOT NULL,
        "quick_create_support" TINYINT DEFAULT 0 NOT NULL,
        "in_position"          TINYINT NULL,
        "reference_status"     INT DEFAULT 0 NOT NULL,
        "use_status"           TINYINT DEFAULT 0 NOT NULL,
        "parent_system_type"   INT NULL,
        "system_type"          INT NULL,
        "system_architecure"   VARCHAR(16 char) NULL,
        "language"             VARCHAR(16 char) NULL,
        "image_status"         TINYINT NULL,
        "base_image_support"   TINYINT DEFAULT 0 NULL,
        "init_support"         TINYINT DEFAULT 0 NOT NULL,
        "create_time"          TIMESTAMP(6) NOT NULL,
        "update_time"          TIMESTAMP(6) NOT NULL,
        "image_source"         TINYINT NULL,
        "is_import"            TINYINT DEFAULT 0 NOT NULL,
        "is_hide"              TINYINT DEFAULT 0 NOT NULL,
        "instance_state"       TINYINT DEFAULT 0 NOT NULL,
        "is_init"              TINYINT DEFAULT 0 NULL,
        "instance_create_user" VARCHAR(64 char) NULL,
        "support_export"       TINYINT DEFAULT 0 NOT NULL,
        "create_user"          VARCHAR(128 char) NOT NULL,
        "create_user_name"     VARCHAR(128 char) NOT NULL
    );

CREATE TABLE "MP"."mp_devops_target_auth"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) NULL,
        "owner"       VARCHAR(64 char) NOT NULL,
        "target"      VARCHAR(64 char) NOT NULL,
        "can_read"    TINYINT NULL,
        "can_create"  TINYINT NULL,
        "can_update"  TINYINT NULL,
        "can_delete"  TINYINT NULL,
        "create_time" TIMESTAMP(6) NOT NULL,
        "update_time" TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_event_tracking_record"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "module"      VARCHAR(256 char) NOT NULL,
        "category"    VARCHAR(256 char) NOT NULL,
        "action"      VARCHAR(1024 char) NOT NULL,
        "target"      VARCHAR(1024 char) NOT NULL,
        "create_time" TIMESTAMP(6) NOT NULL,
        "update_time" TIMESTAMP(6) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_actuator"
(
    "pk"          BIGINT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "title"       VARCHAR(64 char) NOT NULL,
        "ip"          VARCHAR(32 char) NOT NULL,
        "network_id"  VARCHAR(64 char) NOT NULL,
        "instance_id" VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_collection"
(
    "pk"                    BIGINT IDENTITY(1, 1) NOT NULL,
    "id"                    VARCHAR(64 char ) NOT NULL,
        "title"                 VARCHAR(64 char) NOT NULL,
        "description"           VARCHAR(320 char) NOT NULL,
        "category"              TINYINT NOT NULL,
        "type_flag"             SMALLINT DEFAULT 2 NOT NULL,
        "background_flow_count" INT NOT NULL,
        "attack_flow_count"     INT NOT NULL,
        "create_time"           TIMESTAMP(0) NOT NULL,
        "update_time"           TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_collection_rel"
(
    "pk"            BIGINT IDENTITY(1, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "collection_id" VARCHAR(64 char) NOT NULL,
        "flow_id"       VARCHAR(64 char) NOT NULL,
        "create_time"   TIMESTAMP(0) NOT NULL,
        "update_time"   TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_flow_info"
(
    "pk"               BIGINT IDENTITY(154, 1) NOT NULL,
    "id"               VARCHAR(64 char ) NOT NULL,
        "title"            VARCHAR(64 char) NOT NULL,
        "flow_desc"        VARCHAR(320 char) NOT NULL,
        "interactive_type" TINYINT NOT NULL,
        "resource_type"    TINYINT NOT NULL,
        "run_cmd"          VARCHAR(128 char) NOT NULL,
        "category"         TINYINT NOT NULL,
        "file_name"        VARCHAR(128 char) NOT NULL,
        "pcap_file"        VARCHAR(255 char) NOT NULL,
        "script_file"      VARCHAR(255 char) NOT NULL,
        "create_time"      TIMESTAMP(0) NOT NULL,
        "update_time"      TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_pcap"
(
    "pk"          BIGINT IDENTITY(141, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "flow_id"     VARCHAR(64 char) NOT NULL,
        "duration"    DECIMAL(20, 0) NOT NULL,
        "pcap_size"   DECIMAL(20, 0) NOT NULL,
        "pkt_num"     DECIMAL(20, 0) NOT NULL,
        "protocols"   VARCHAR(128 char) NOT NULL,
        "pcap_file"   VARCHAR(255 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_port"
(
    "pk"             BIGINT IDENTITY(1, 1) NOT NULL,
    "id"             VARCHAR(64 char ) NOT NULL,
        "server_ip"      VARCHAR(32 char) NOT NULL,
        "server_mac"     VARCHAR(32 char) NOT NULL,
        "server_port_id" VARCHAR(40 char) NOT NULL,
        "server_gateway" VARCHAR(32 char) NOT NULL,
        "client_ip"      VARCHAR(32 char) NOT NULL,
        "client_mac"     VARCHAR(32 char) NOT NULL,
        "client_port_id" VARCHAR(40 char) NOT NULL,
        "client_gateway" VARCHAR(32 char) NOT NULL,
        "network_id"     VARCHAR(64 char) DEFAULT '' NOT NULL,
        "actuator_id"    VARCHAR(64 char) DEFAULT '' NOT NULL,
        "port_type"      TINYINT DEFAULT 0 NOT NULL,
        "create_time"    TIMESTAMP(0) NOT NULL,
        "update_time"    TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_protocol"
(
    "pk"            BIGINT IDENTITY(1106, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "flow_id"       VARCHAR(64 char) NOT NULL,
        "protocol_name" VARCHAR(64 char) NOT NULL,
        "rate"          REAL NOT NULL,
        "marked"        SMALLINT NOT NULL,
        "create_time"   TIMESTAMP(0) NOT NULL,
        "update_time"   TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_script_demo"
(
    "pk"          BIGINT IDENTITY(6, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "script_name" VARCHAR(64 char) NOT NULL,
        "url"         VARCHAR(1024 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL,
        "type"        INT DEFAULT 1 NULL
    );

CREATE TABLE "MP"."mp_flow_script_param"
(
    "pk"          BIGINT IDENTITY(29, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "flow_id"     VARCHAR(64 char) NOT NULL,
        "param_key"   VARCHAR(50 char) NOT NULL,
        "param_value" VARCHAR(64 char) NOT NULL,
        "param_type"  TINYINT NOT NULL,
        "param_desc"  VARCHAR(2048 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_task"
(
    "pk"              BIGINT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
        "title"           VARCHAR(64 char) NOT NULL,
        "task_desc"       VARCHAR(320 char) NOT NULL,
        "actuator_id"     VARCHAR(64 char) NOT NULL,
        "flow_id"         VARCHAR(64 char) NOT NULL,
        "flow_port_id"    VARCHAR(64 char) NOT NULL,
        "flow_platform"   TINYINT NOT NULL,
        "third_flow_id"   VARCHAR(64 char) NOT NULL,
        "third_flow_name" VARCHAR(128 char) NOT NULL,
        "third_task_id"   VARCHAR(64 char) NOT NULL,
        "count"           BIGINT NOT NULL,
        "delay"           BIGINT NOT NULL,
        "interval_time"   BIGINT NOT NULL,
        "task_status"     TINYINT DEFAULT 0 NOT NULL,
        "running_count"   INT DEFAULT 0 NOT NULL,
        "origin_ips"      VARCHAR(32 char) NOT NULL,
        "target_ips"      VARCHAR(32 char) NOT NULL,
        "node_id"         VARCHAR(64 char) NOT NULL,
        "custom_flag"     TINYINT DEFAULT 0 NOT NULL,
        "flow_protocol"   VARCHAR(512 char) NOT NULL,
        "content"         VARCHAR(1024 char) NOT NULL,
        "mult"            INT DEFAULT 1 NOT NULL,
        "create_time"     TIMESTAMP(0) NOT NULL,
        "update_time"     TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_task_conf"
(
    "pk"             BIGINT IDENTITY(29, 1) NOT NULL,
    "id"             VARCHAR(64 char ) NOT NULL,
        "task_id"        VARCHAR(64 char) NOT NULL,
        "keep_running"   SMALLINT NOT NULL,
        "rate_unlimited" SMALLINT NOT NULL,
        "replay"         SMALLINT NOT NULL,
        "conf"           TEXT NOT NULL,
        "create_time"    TIMESTAMP(0) NOT NULL,
        "update_time"    TIMESTAMP(0) NOT NULL,
        "config"         INT DEFAULT 0 NOT NULL
    );

CREATE TABLE "MP"."mp_flow_task_port"
(
    "pk"          BIGINT IDENTITY(4, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "network_id"  VARCHAR(64 char) DEFAULT '' NOT NULL,
        "actuator_id" VARCHAR(64 char) DEFAULT '' NOT NULL,
        "mac"         VARCHAR(64 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_task_record"
(
    "pk"            BIGINT IDENTITY(64, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
        "actuator_id"   VARCHAR(64 char) NOT NULL,
        "task_id"       VARCHAR(64 char) NOT NULL,
        "tx_bps"        BIGINT NOT NULL,
        "tx_pps"        BIGINT NOT NULL,
        "latency"       BIGINT NOT NULL,
        "jitter"        BIGINT NOT NULL,
        "obytes"        BIGINT NOT NULL,
        "oerrors"       BIGINT NOT NULL,
        "total_tx_L2"   BIGINT NOT NULL,
        "total_tx_L1"   BIGINT NOT NULL,
        "total_pps"     BIGINT NOT NULL,
        "total_rx"      BIGINT NOT NULL,
        "queue_full"    BIGINT NOT NULL,
        "drop_rate"     BIGINT NOT NULL,
        "record_status" TINYINT NOT NULL,
        "create_time"   TIMESTAMP(0) NOT NULL,
        "update_time"   TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_flow_task_task"
(
    "pk"               BIGINT IDENTITY(29, 1) NOT NULL,
    "id"               VARCHAR(64 char ) NOT NULL,
        "scene_id"         VARCHAR(64 char) NOT NULL,
        "actuator_id"      VARCHAR(64 char) NOT NULL,
        "resource_type"    TINYINT NOT NULL,
        "interactive_type" TINYINT NOT NULL,
        "flow_id"          VARCHAR(64 char) NOT NULL,
        "port_id"          VARCHAR(64 char) NOT NULL,
        "success_count"    INT DEFAULT 0 NOT NULL,
        "error_count"      INT DEFAULT 0 NOT NULL,
        "task_status"      TINYINT DEFAULT 0 NOT NULL,
        "msg"              VARCHAR(1024 char) DEFAULT '' NOT NULL,
        "node_id"          VARCHAR(64 char) NOT NULL,
        "node_ip"          VARCHAR(24 char) NOT NULL,
        "node_name"        VARCHAR(64 char) NOT NULL,
        "create_time"      TIMESTAMP(0) NOT NULL,
        "update_time"      TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_front_admin_activ_ctl"
(
    "pk"                INT IDENTITY(2, 1) NOT NULL,
    "id"                VARCHAR(64 char ) NOT NULL,
        "tenant"            VARCHAR(64 char) DEFAULT '' NOT NULL,
        "salt"              VARCHAR(64 char) NULL,
        "suffix"            VARCHAR(10 char) NULL,
        "license_code"      VARCHAR(256 char) NULL,
        "first_active_date" VARCHAR(256 char) NULL,
        "last_active_date"  VARCHAR(256 char) NULL,
        "expire_date"       VARCHAR(256 char) NULL,
        "active_count"      INT DEFAULT 1 NOT NULL,
        "sys_timestamp" CLOB NULL,
        "sys_run_seconds" VARCHAR(256 char) DEFAULT '0' NULL,
        "sys_lifetime"    VARCHAR(256 char) NULL,
        "status"          INT DEFAULT 1 NOT NULL,
        "create_time"     TIMESTAMP(0) NOT NULL,
        "update_time"     TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_front_admin_menu_extra_info"
(
    "pk"          INT IDENTITY(224, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
        "tenant"      VARCHAR(64 char) DEFAULT '' NOT NULL,
        "menu"        VARCHAR(64 char) NOT NULL,
        "editions"    VARCHAR(200 char) NOT NULL,
        "activ_keys"  VARCHAR(1024 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_log_level"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(256 char ) NULL,
        "log_name"    VARCHAR(256 char) NOT NULL,
        "log_level"   VARCHAR(8 char) NOT NULL,
        "create_time" TIMESTAMP(0) NOT NULL,
        "update_time" TIMESTAMP(0) NOT NULL
    );

CREATE TABLE "MP"."mp_log_report"
(
    "pk"           INT IDENTITY(51065, 1) NOT NULL,
    "id"           VARCHAR(32 char ) NOT NULL,
        "file_name"    VARCHAR(32 char) NOT NULL,
        "type"         VARCHAR(20 char) NOT NULL,
        "file_size"    BIGINT NOT NULL,
        "download_url" VARCHAR(255 char) NOT NULL,
        "host_ip"      VARCHAR(20 char) DEFAULT '' NOT NULL,
        "create_time"  TIMESTAMP(0) NOT NULL,
        "update_time"  TIMESTAMP(0) NOT NULL
    );

ALTER TABLE "MP"."mp_auth_client_details" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_org" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_org_permission" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_role_permission" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_target" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_tenant" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_tenant_user" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_url_ac" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_url_ac_target" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_user" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_user_online_record" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_user_permission" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_auth_user_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_base_local_file_mapping" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_common_option" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_common_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_cscene" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_cscene_auth" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_cscene_instance" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_cscene_instance_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_cscene_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_instance_user_count" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_cscene_snapshot" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_alarm_statistics" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_cpnode_monitor_script_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_log_operation" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_screenshot" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_system_alarm" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_target" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_devops_target_auth" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_event_tracking_record" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_actuator" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_collection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_collection_rel" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_flow_info" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_pcap" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_protocol" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_script_demo" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_script_param" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_task" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_task_conf" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_task_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_task_record" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_flow_task_task" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_front_admin_activ_ctl" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_front_admin_menu_extra_info" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_log_level" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_log_report" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

CREATE INDEX "module_cat"
    ON "MP"."mp_event_tracking_record"
        (
         "module",
         "category"
            );

ALTER INDEX "MP"."module_cat" VISIBLE;

ALTER TABLE "MP"."mp_cscene_instance_user_count" ADD CONSTRAINT "MPCONS134218851" CHECK ("count" >= 0) ENABLE;

ALTER TABLE "MP"."mp_auth_client_details" ADD CONSTRAINT "client_id" UNIQUE ("client_id");

ALTER TABLE "MP"."mp_auth_org" ADD CONSTRAINT "tenant" UNIQUE ("tenant", "identifier");

ALTER TABLE "MP"."mp_auth_org" ADD CONSTRAINT "id" UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_org_permission" ADD UNIQUE ("tenant", "owner", "target");

ALTER TABLE "MP"."mp_auth_org_permission" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_role" ADD CONSTRAINT "tenant_2" UNIQUE ("tenant", "name");

ALTER TABLE "MP"."mp_auth_role" ADD UNIQUE ("tenant", "identifier");

ALTER TABLE "MP"."mp_auth_role" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_role_permission" ADD UNIQUE ("tenant", "owner", "target");

ALTER TABLE "MP"."mp_auth_role_permission" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_target" ADD CONSTRAINT "identifier" UNIQUE ("identifier");

ALTER TABLE "MP"."mp_auth_target" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_tenant" ADD UNIQUE ("identifier");

ALTER TABLE "MP"."mp_auth_tenant" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_tenant_user" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_url_ac" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_url_ac_target" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_user" ADD CONSTRAINT "username" UNIQUE ("username");

ALTER TABLE "MP"."mp_auth_user" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_user_online_record" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_user_permission" ADD UNIQUE ("tenant", "owner", "target");

ALTER TABLE "MP"."mp_auth_user_permission" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_auth_user_role" ADD UNIQUE ("tenant", "user", "role");

ALTER TABLE "MP"."mp_auth_user_role" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_base_local_file_mapping" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_common_option" ADD CONSTRAINT "code" UNIQUE ("code", "type");

ALTER TABLE "MP"."mp_common_option" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_cscene_common_config" ADD CONSTRAINT "key" UNIQUE ("key");

ALTER TABLE "MP"."mp_cscene_common_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_cscene_cscene" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_cscene_cscene_auth" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_cscene_cscene_instance" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_cscene_cscene_instance_role" ADD CONSTRAINT "cscene_id" UNIQUE ("cscene", "id");

ALTER TABLE "MP"."mp_cscene_cscene_role" ADD UNIQUE ("cscene", "id");

ALTER TABLE "MP"."mp_cscene_snapshot" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_alarm_statistics" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_cpnode_monitor_script_config" ADD CONSTRAINT "agent_name" UNIQUE ("agent_name");

ALTER TABLE "MP"."mp_devops_cpnode_monitor_script_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_log_operation" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_screenshot" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_system_alarm" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_target" ADD CONSTRAINT "name" UNIQUE ("name");

ALTER TABLE "MP"."mp_devops_target" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_devops_target_auth" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_actuator" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_collection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_collection_rel" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_collection_rel" ADD CONSTRAINT "collection_id" UNIQUE ("collection_id", "flow_id");

ALTER TABLE "MP"."mp_flow_flow_info" ADD CONSTRAINT "title" UNIQUE ("title");

ALTER TABLE "MP"."mp_flow_flow_info" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_pcap" ADD CONSTRAINT "flow_id" UNIQUE ("flow_id");

ALTER TABLE "MP"."mp_flow_pcap" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_protocol" ADD CONSTRAINT "flow_protocol_id" UNIQUE ("id", "flow_id");

ALTER TABLE "MP"."mp_flow_script_demo" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_script_param" ADD UNIQUE ("id", "flow_id");

ALTER TABLE "MP"."mp_flow_task" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_task_conf" ADD CONSTRAINT "task_id" UNIQUE ("task_id");

ALTER TABLE "MP"."mp_flow_task_conf" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_task_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_task_record" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_flow_task_task" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_front_admin_activ_ctl" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_front_admin_menu_extra_info" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_log_level" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_log_level" ADD CONSTRAINT "log_name" UNIQUE ("log_name");

ALTER TABLE "MP"."mp_log_report" ADD UNIQUE ("id");

COMMENT ON TABLE "MP"."mp_auth_client_details"
IS
    '客户端认证配置表';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."access_token_validity"
IS
    'access_token的有效期（秒）';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."additional_information"
IS
    '预留字段';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."authorities"
IS
    '权限';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."authorized_grant_types"
IS
    '授权类型';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."autoapprove"
IS
    '设置用户是否自动approve操作';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."client_id"
IS
    '客户端ID';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."client_secret"
IS
    '客户端秘钥';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."refresh_token_validity"
IS
    'refresh_token的有效期（秒）';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."resource_ids"
IS
    '资源id';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."scope"
IS
    '权限范围';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."type"
IS
    '类型 1:Oauth2渠道 2:免认证渠道';
    COMMENT ON COLUMN "MP"."mp_auth_client_details"."web_server_redirect_uri"
IS
    '回跳地址';
    COMMENT ON TABLE "MP"."mp_auth_org"
IS
    '组织表';
    COMMENT ON COLUMN "MP"."mp_auth_org"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_org"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_auth_org"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_org"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_auth_org"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_auth_org"."parent"
IS
    '父组织';
    COMMENT ON COLUMN "MP"."mp_auth_org"."path"
IS
    '组织结构路径';
    COMMENT ON COLUMN "MP"."mp_auth_org"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_org"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_org"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_org_permission"
IS
    '组织权限关系表';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."can_create"
IS
    '创建权限';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."can_delete"
IS
    '删除权限';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."can_read"
IS
    '读权限';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."can_update"
IS
    '更新权限';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."owner"
IS
    '权限所有者(组织)';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."target"
IS
    '资源目标';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_org_permission"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_role"
IS
    '角色表';
    COMMENT ON COLUMN "MP"."mp_auth_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_role"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_auth_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_role"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_auth_role"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_auth_role"."org"
IS
    '所属组织';
    COMMENT ON COLUMN "MP"."mp_auth_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_role"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_role"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_role"."visible"
IS
    '是否可见 1:可见，0:不可见';
    COMMENT ON TABLE "MP"."mp_auth_role_permission"
IS
    '角色权限关系表';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."can_create"
IS
    '创建权限';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."can_delete"
IS
    '删除权限';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."can_read"
IS
    '读权限';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."can_update"
IS
    '更新权限';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."owner"
IS
    '权限所有者(角色ID)';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."target"
IS
    '权限ID';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_role_permission"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_target"
IS
    '权限（资源）表';
    COMMENT ON COLUMN "MP"."mp_auth_target"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_target"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_target"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_auth_target"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_auth_target"."parent"
IS
    '父权限ID';
    COMMENT ON COLUMN "MP"."mp_auth_target"."path"
IS
    '路径';
    COMMENT ON COLUMN "MP"."mp_auth_target"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_target"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_target"."type"
IS
    '资源类型 1:菜单, 2:API, 3:数据';
    COMMENT ON COLUMN "MP"."mp_auth_target"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_target"."visible"
IS
    '是否可见 1:可见，0:不可见（受控于激活选项） 默认为可见';
    COMMENT ON TABLE "MP"."mp_auth_tenant"
IS
    '租户表';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_tenant"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_user_permission"
IS
    '用户权限关系表';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."can_create"
IS
    '创建权限';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."can_delete"
IS
    '删除权限';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."can_read"
IS
    '读权限';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."can_update"
IS
    '更新权限';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."owner"
IS
    '权限所有者(用户ID)';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."target"
IS
    '资源目标';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_user_permission"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."group"
IS
    '分组';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."id"
IS
    'id';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."local_file"
IS
    '本地文件路径';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."local_file_modified_time"
IS
    '本地文件修改时间';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."remote_file"
IS
    '远程文件路径';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."seq"
IS
    '序号';
    COMMENT ON COLUMN "MP"."mp_base_local_file_mapping"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_common_option"
IS
    '选项表';
    COMMENT ON COLUMN "MP"."mp_common_option"."code"
IS
    '选项编码';
    COMMENT ON COLUMN "MP"."mp_common_option"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_common_option"."en_name"
IS
    '选项英文名称';
    COMMENT ON COLUMN "MP"."mp_common_option"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_common_option"."meta"
IS
    '元数据';
    COMMENT ON COLUMN "MP"."mp_common_option"."parent_code"
IS
    '父级选项编码';
    COMMENT ON COLUMN "MP"."mp_common_option"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_common_option"."sort"
IS
    '排序字段';
    COMMENT ON COLUMN "MP"."mp_common_option"."type"
IS
    '选项类型';
    COMMENT ON COLUMN "MP"."mp_common_option"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_common_option"."zh_name"
IS
    '选项中文名称';
    COMMENT ON TABLE "MP"."mp_cscene_common_config"
IS
    '场景通用配置表';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."content"
IS
    '配置内容';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."key"
IS
    '配置对应key';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_common_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_cscene_cscene"
IS
    '场景信息表';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."auth_type"
IS
    '授权类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."create_user"
IS
    '创建用户id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."create_username"
IS
    '创建用户名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."deleted"
IS
    '是否删除';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."logo"
IS
    '综合场景模板logo';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."reference_meta_data"
IS
    '综合场景模板引用元数据';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."reference_type"
IS
    '综合场景模板引用类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."scene_config"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."support_partial_start"
IS
    '是否支持部分启动';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."type"
IS
    '综合场景模板类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_cscene_cscene_auth"
IS
    '场景模板授权信息表';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."can_create"
IS
    '可创建';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."can_delete"
IS
    '可删除';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."can_read"
IS
    '可读';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."can_update"
IS
    '可更新';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."owner"
IS
    '用户ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."target"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_auth"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_cscene_cscene_instance"
IS
    '场景实例表';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."create_user"
IS
    '创建用户id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."create_username"
IS
    '创建用户名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."cscene"
IS
    '综合场景id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."deleted"
IS
    '是否删除';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."logo"
IS
    '综合场景模板logo';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."pass_through"
IS
    '透传字段';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."reference_meta_data"
IS
    '综合场景实例引用元数据';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."reference_type"
IS
    '综合场景实例引用类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."scene_attr"
IS
    '场景实例属性';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."scene_ttl"
IS
    '场景实例存活时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."support_partial_start"
IS
    '是否支持部分启动';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."type"
IS
    '综合场景实例类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_cscene_cscene_instance_role"
IS
    '场景实例角色表';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."asset_nodes"
IS
    '资产节点';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."cscene"
IS
    '综合场景实例id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."key"
IS
    '角色key';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."parent"
IS
    '父角色id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."type"
IS
    '类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."users"
IS
    '包含用户';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_instance_role"."visible_nodes"
IS
    '可见节点';
    COMMENT ON TABLE "MP"."mp_cscene_cscene_role"
IS
    '场景角色表';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."asset_nodes"
IS
    '资产节点';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."cscene"
IS
    '综合场景id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."key"
IS
    '角色key';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."parent"
IS
    '父角色id';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."type"
IS
    '类型';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."users"
IS
    '包含用户';
    COMMENT ON COLUMN "MP"."mp_cscene_cscene_role"."visible_nodes"
IS
    '可见节点';
    COMMENT ON TABLE "MP"."mp_devops_alarm_statistics"
IS
    '告警统计信息表';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."alarm_count"
IS
    '告警数量';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."alarm_date"
IS
    '告警日期';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."alarm_type"
IS
    '告警类型：0 云平台服务, 1 业务服务 2 服务器';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."id"
IS
    '告警统计id';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_alarm_statistics"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_devops_cpnode_monitor_script_config"
IS
    '云平台节点探针监控脚本安装信息表';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."agent_name"
IS
    '探针名称';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."config"
IS
    '脚本配置JSON字符串，包括launchid, interval，scriptUrl等信息';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."hostname"
IS
    '主机名称';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."ip"
IS
    'IP地址';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."status"
IS
    '1:已完成安装 2:已发送指令-待确认安装成功 3:安装失败';
    COMMENT ON COLUMN "MP"."mp_devops_cpnode_monitor_script_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_devops_screenshot"
IS
    '截屏文件管理表';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."file"
IS
    '截屏文件地址';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."file_name"
IS
    '截屏文件名称';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."file_size"
IS
    '截屏文件大小，单位:B';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."scene"
IS
    '场景id';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."scene_name"
IS
    '场景名称';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."user_id"
IS
    '用户id';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."user_name"
IS
    '用户名称';
    COMMENT ON COLUMN "MP"."mp_devops_screenshot"."user_org_name"
IS
    '组织名称';
    COMMENT ON TABLE "MP"."mp_devops_system_alarm"
IS
    '告警信息表';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."alarm_msg"
IS
    '告警信息';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."alarm_status"
IS
    '告警状态：0 生效， 1失效';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."alarm_type"
IS
    '告警类型：0 云平台服务, 1 业务服务 2 服务器';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."hostname"
IS
    '节点名称';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."running_time"
IS
    '持续时间';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."servername"
IS
    '设备名称';
    COMMENT ON COLUMN "MP"."mp_devops_system_alarm"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_devops_target_auth"
IS
    '靶标授权表';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."can_create"
IS
    '可创建';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."can_delete"
IS
    '可删除';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."can_read"
IS
    '可读';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."can_update"
IS
    '可更新';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."owner"
IS
    '用户ID';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."target"
IS
    '镜像UUID';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_devops_target_auth"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_event_tracking_record"
IS
    '埋点表';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."action"
IS
    '动作';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."category"
IS
    '分类';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."module"
IS
    '模块';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."target"
IS
    '操作目标';
    COMMENT ON COLUMN "MP"."mp_event_tracking_record"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_actuator"
IS
    '流量执行器表';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."id"
IS
    '执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."instance_id"
IS
    '实例id';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."ip"
IS
    '执行器ip地址';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."network_id"
IS
    '网络id';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."title"
IS
    '执行器名称';
    COMMENT ON COLUMN "MP"."mp_flow_actuator"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_collection_rel"
IS
    '流量关联集合表';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."collection_id"
IS
    '流量集合id';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."id"
IS
    '关联id';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_collection_rel"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_flow_info"
IS
    '流量信息表';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."category"
IS
    '分类   0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."file_name"
IS
    '文件名称';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."flow_desc"
IS
    '流量描述';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."interactive_type"
IS
    '交互类型：0 背景流量，1 攻击流量，2 全流量';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."pcap_file"
IS
    'pcap文件地址';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."resource_type"
IS
    '资源类型：0 pcap包，1 脚本';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."run_cmd"
IS
    '攻击脚本运行命令';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."script_file"
IS
    '脚本文件地址';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."title"
IS
    '流量标题';
    COMMENT ON COLUMN "MP"."mp_flow_flow_info"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_port"
IS
    '流量执行网口表';
    COMMENT ON COLUMN "MP"."mp_flow_port"."actuator_id"
IS
    '流量执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_port"."client_gateway"
IS
    '客户端网关';
    COMMENT ON COLUMN "MP"."mp_flow_port"."client_ip"
IS
    '客户端ip';
    COMMENT ON COLUMN "MP"."mp_flow_port"."client_mac"
IS
    '客户端mac';
    COMMENT ON COLUMN "MP"."mp_flow_port"."client_port_id"
IS
    '客户端portId';
    COMMENT ON COLUMN "MP"."mp_flow_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_port"."id"
IS
    '网口id';
    COMMENT ON COLUMN "MP"."mp_flow_port"."network_id"
IS
    '网络id';
    COMMENT ON COLUMN "MP"."mp_flow_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_port"."port_type"
IS
    '网口类型：0 pcap包，1 脚本';
    COMMENT ON COLUMN "MP"."mp_flow_port"."server_gateway"
IS
    '服务端网关';
    COMMENT ON COLUMN "MP"."mp_flow_port"."server_ip"
IS
    '服务端ip';
    COMMENT ON COLUMN "MP"."mp_flow_port"."server_mac"
IS
    '服务端mac';
    COMMENT ON COLUMN "MP"."mp_flow_port"."server_port_id"
IS
    '服务端portId';
    COMMENT ON COLUMN "MP"."mp_flow_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_script_demo"
IS
    '流量脚本模板表';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."script_name"
IS
    '脚本名称';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."type"
IS
    '示例脚本类型 1:流量 2:探针';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_flow_script_demo"."url"
IS
    '脚本url';
    COMMENT ON TABLE "MP"."mp_flow_script_param"
IS
    '流量脚本参数表';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."id"
IS
    '脚本参数id';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."param_desc"
IS
    '参数说明';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."param_key"
IS
    '参数名';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."param_type"
IS
    '参数类型：0 string,1 IP,2 number,3 bool';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."param_value"
IS
    '参数值';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_script_param"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_task_port"
IS
    '流量任务网口表';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."actuator_id"
IS
    '流量执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."id"
IS
    '网口id';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."mac"
IS
    '网口mac';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."network_id"
IS
    '网络id';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_task_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_task_task"
IS
    '流量执行任务表';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."actuator_id"
IS
    '流量执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."error_count"
IS
    '失败执行次数';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."id"
IS
    '任务id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."interactive_type"
IS
    '交互类型：0 背景流量，1 攻击流量';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."msg"
IS
    '错误信息';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."node_id"
IS
    '目标节点id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."node_ip"
IS
    '目标节点ip';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."node_name"
IS
    '目标节点名称';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."port_id"
IS
    '网口id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."resource_type"
IS
    '资源类型：0 pcap包，1 脚本';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."scene_id"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."success_count"
IS
    '成功执行次数';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."task_status"
IS
    '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败';
    COMMENT ON COLUMN "MP"."mp_flow_task_task"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_front_admin_activ_ctl"
IS
    '系统激活信息表';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."active_count"
IS
    '同一个激活码的激活次数';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."expire_date"
IS
    '（密文）过期日期';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."first_active_date"
IS
    '（密文）系统首次激活日期。如果同一个license_code多次激活，最终的过期日期以第一次激活日期为准';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."last_active_date"
IS
    '（密文）系统最近一次激活日期，同一个激活码的情况下';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."license_code"
IS
    '（密文）激活码';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."salt"
IS
    '随机盐';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."status"
IS
    '状态 1:有效 0:无效';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."suffix"
IS
    '6位随机数字+字母，当前表中每次新增一条数据，则随机产生一个suffix';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."sys_lifetime"
IS
    '系统寿命';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."sys_run_seconds"
IS
    '系统运行秒数';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."sys_timestamp"
IS
    '定时任务记录的系统时间戳，发生时间回拨，则该值也回到回拨时间，用于累计系统运行秒数';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_front_admin_activ_ctl"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_log_level"
IS
    '系统日志级别配置表';
    COMMENT ON COLUMN "MP"."mp_log_level"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_log_level"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_log_level"."log_level"
IS
    '日志级别(DEBUGINFOWARNERROR)';
    COMMENT ON COLUMN "MP"."mp_log_level"."log_name"
IS
    '日志名称(一般为包路径)';
    COMMENT ON COLUMN "MP"."mp_log_level"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_log_level"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_log_report"
IS
    '日志上报表';
    COMMENT ON COLUMN "MP"."mp_log_report"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_log_report"."download_url"
IS
    '文件下载地址';
    COMMENT ON COLUMN "MP"."mp_log_report"."file_name"
IS
    '文件名称';
    COMMENT ON COLUMN "MP"."mp_log_report"."file_size"
IS
    '文件大小';
    COMMENT ON COLUMN "MP"."mp_log_report"."host_ip"
IS
    '节点ip';
    COMMENT ON COLUMN "MP"."mp_log_report"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_log_report"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_log_report"."type"
IS
    '日志类型（CRMPADCP)';
    COMMENT ON COLUMN "MP"."mp_log_report"."update_time"
IS
    '更新时间';
CREATE TABLE "MP"."mp_menu_template_info"
(
    "pk"          INT IDENTITY(234, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "template_id" VARCHAR(64 char) NOT NULL,
            "menu_id"     VARCHAR(64 char) NOT NULL,
            "parent"      VARCHAR(64 char) NULL,
            "identifier"  VARCHAR(64 char) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "type"        INT NOT NULL,
            "visible"     INT DEFAULT 1 NOT NULL,
            "name"        VARCHAR(64 char) NOT NULL,
            "en_name"     VARCHAR(64 char) NOT NULL,
            "path"        VARCHAR(255 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_menu_template_role"
(
    "pk"          INT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "template_id" VARCHAR(64 char) NOT NULL,
            "role_id"     VARCHAR(64 char) NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene"
(
    "pk"                     INT IDENTITY(4, 1) NOT NULL,
    "id"                     VARCHAR(64 char ) NOT NULL,
            "source"                 VARCHAR(64 char) NOT NULL,
            "name"                   VARCHAR(255 char) NOT NULL,
            "description"            VARCHAR(2048 char) NOT NULL,
            "file"                   VARCHAR(2048 char) NOT NULL,
            "display_context"        TEXT NOT NULL,
            "event"                  INT NOT NULL,
            "event_status"           INT NOT NULL,
            "process"                VARCHAR(64 char) NOT NULL,
            "process_status"         INT NOT NULL,
            "process_message"        VARCHAR(2048 char) NOT NULL,
            "create_consume_seconds" INT NOT NULL,
            "deleted"                TINYINT NOT NULL,
            "create_time"            TIMESTAMP(6) NOT NULL,
            "update_time"            TIMESTAMP(6) NOT NULL,
            "stage"                  VARCHAR(64 char) NOT NULL,
            "stage_name"             VARCHAR(64 char) NOT NULL,
            "stage_percentage"       INT NOT NULL,
            "stage_list"             VARCHAR(2048 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_area"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "node_id"         VARCHAR(64 char) NOT NULL,
            "area"            VARCHAR(64 char) NOT NULL,
            "icon2"           VARCHAR(2048 char) NOT NULL,
            "position2"       VARCHAR(128 char) NOT NULL,
            "icon3"           VARCHAR(2048 char) NOT NULL,
            "position3"       VARCHAR(128 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "description"     VARCHAR(1024 char) NOT NULL,
            "open"            TINYINT NOT NULL,
            "type"            TINYINT DEFAULT 0 NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config"
(
    "pk"              INT IDENTITY(8, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "type"            INT NOT NULL,
            "source"          VARCHAR(64 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "description"     VARCHAR(2048 char) NOT NULL,
            "file"            VARCHAR(2048 char) NOT NULL,
            "display_context" TEXT NOT NULL,
            "deleted"         TINYINT NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_area"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "node_id"         VARCHAR(64 char) NOT NULL,
            "area"            VARCHAR(64 char) NOT NULL,
            "icon2"           VARCHAR(2048 char) NOT NULL,
            "position2"       VARCHAR(128 char) NOT NULL,
            "icon3"           VARCHAR(2048 char) NOT NULL,
            "position3"       VARCHAR(128 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "description"     VARCHAR(1024 char) NOT NULL,
            "open"            TINYINT NOT NULL,
            "type"            TINYINT DEFAULT 0 NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_element"
(
    "pk"                  INT IDENTITY(34, 1) NOT NULL,
    "id"                  VARCHAR(64 char ) NOT NULL,
            "scene"               VARCHAR(64 char) NOT NULL,
            "node_id"             VARCHAR(64 char) NOT NULL,
            "area"                VARCHAR(64 char) NOT NULL,
            "icon2"               VARCHAR(2048 char) NOT NULL,
            "position2"           VARCHAR(64 char) NOT NULL,
            "icon3"               VARCHAR(2048 char) NOT NULL,
            "position3"           VARCHAR(64 char) NOT NULL,
            "name"                VARCHAR(255 char) NOT NULL,
            "description"         VARCHAR(1024 char) NOT NULL,
            "type"                INT NOT NULL,
            "file"                VARCHAR(2048 char) NOT NULL,
            "system_type"         VARCHAR(64 char) NOT NULL,
            "system_architecture" VARCHAR(16 char) NOT NULL,
            "system_language"     VARCHAR(8 char) NOT NULL,
            "cpu"                 INT NOT NULL,
            "ram"                 INT NOT NULL,
            "disk"                INT NOT NULL,
            "init_commands" CLOB NOT NULL,
            "max_port_number" INT NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "is_auxiliary"    TINYINT NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_element_plugin"
(
    "pk"            INT IDENTITY(51, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "plugin_key"    VARCHAR(32 char) NOT NULL,
            "create_time"   TIMESTAMP(6) NOT NULL,
            "update_time"   TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_element_port"
(
    "pk"                    INT IDENTITY(75, 1) NOT NULL,
    "id"                    VARCHAR(64 char ) NOT NULL,
            "scene"                 VARCHAR(64 char) NOT NULL,
            "scene_element"         VARCHAR(64 char) NOT NULL,
            "port_id"               VARCHAR(64 char) NOT NULL,
            "name"                  VARCHAR(255 char) NOT NULL,
            "type"                  INT NOT NULL,
            "usable"                TINYINT DEFAULT 1 NULL,
            "mac_addr"              VARCHAR(32 char) NOT NULL,
            "ip"                    VARCHAR(32 char) NOT NULL,
            "netmask"               VARCHAR(32 char) NOT NULL,
            "gateway"               VARCHAR(32 char) NOT NULL,
            "dhcp"                  TINYINT NOT NULL,
            "dns"                   VARCHAR(255 char) NOT NULL,
            "egress"                INT NOT NULL,
            "ingress"               INT NOT NULL,
            "delay"                 INT NOT NULL,
            "loss"                  INT NOT NULL,
            "access_modes"          TEXT NOT NULL,
            "create_time"           TIMESTAMP(6) NOT NULL,
            "update_time"           TIMESTAMP(6) NOT NULL,
            "order"                 INT DEFAULT 0 NOT NULL,
            "is_auxiliary"          TINYINT NOT NULL,
            "port_connect_net_cidr" VARCHAR(20 char) NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_element_port_connection"
(
    "pk"              INT IDENTITY(27, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "source_element"  VARCHAR(64 char) NOT NULL,
            "target_element"  VARCHAR(64 char) NOT NULL,
            "source_port"     VARCHAR(64 char) NOT NULL,
            "target_port"     VARCHAR(64 char) NOT NULL,
            "connection_id"   VARCHAR(64 char) NOT NULL,
            "source_port_id"  VARCHAR(64 char) NOT NULL,
            "target_port_id"  VARCHAR(64 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "unidirectional"  TINYINT NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "type"            VARCHAR(64 char) NULL,
            "vertices"        TEXT NULL,
            "info"            TEXT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_node_connection"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "source_node_id"  VARCHAR(64 char) NOT NULL,
            "target_node_id"  VARCHAR(64 char) NOT NULL,
            "source_node"     VARCHAR(64 char) NOT NULL,
            "target_node"     VARCHAR(64 char) NOT NULL,
            "connection_id"   VARCHAR(64 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "type"            VARCHAR(64 char) NULL,
            "vertices"        TEXT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_config_piece"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "node_id"         VARCHAR(64 char) NOT NULL,
            "area"            VARCHAR(64 char) NOT NULL,
            "icon2"           VARCHAR(2048 char) NOT NULL,
            "position2"       VARCHAR(128 char) NOT NULL,
            "icon3"           VARCHAR(2048 char) NOT NULL,
            "position3"       VARCHAR(128 char) NOT NULL,
            "name"            VARCHAR(64 char) NOT NULL,
            "description"     VARCHAR(1024 char) NOT NULL,
            "type"            VARCHAR(64 char) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_element"
(
    "pk"                  INT IDENTITY(17, 1) NOT NULL,
    "id"                  VARCHAR(64 char ) NOT NULL,
            "scene"               VARCHAR(64 char) NOT NULL,
            "node_id"             VARCHAR(64 char) NOT NULL,
            "area"                VARCHAR(64 char) NOT NULL,
            "icon2"               VARCHAR(2048 char) NOT NULL,
            "position2"           VARCHAR(64 char) NOT NULL,
            "icon3"               VARCHAR(2048 char) NOT NULL,
            "position3"           VARCHAR(64 char) NOT NULL,
            "name"                VARCHAR(255 char) NOT NULL,
            "description"         VARCHAR(1024 char) NOT NULL,
            "type"                INT NOT NULL,
            "file"                VARCHAR(2048 char) NOT NULL,
            "system_type"         VARCHAR(64 char) NOT NULL,
            "system_architecture" VARCHAR(16 char) NOT NULL,
            "system_language"     VARCHAR(8 char) NOT NULL,
            "cpu"                 INT NOT NULL,
            "ram"                 INT NOT NULL,
            "disk"                INT NOT NULL,
            "init_commands" CLOB NOT NULL,
            "max_port_number" INT NOT NULL,
            "event"           INT NOT NULL,
            "event_status"    INT NOT NULL,
            "process"         VARCHAR(64 char) NOT NULL,
            "process_status"  INT NOT NULL,
            "process_message" VARCHAR(2048 char) NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "is_auxiliary"    TINYINT NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_element_plugin"
(
    "pk"            INT IDENTITY(25, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "plugin_key"    VARCHAR(32 char) NOT NULL,
            "create_time"   TIMESTAMP(6) NOT NULL,
            "update_time"   TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_element_port"
(
    "pk"                    INT IDENTITY(36, 1) NOT NULL,
    "id"                    VARCHAR(64 char ) NOT NULL,
            "scene"                 VARCHAR(64 char) NOT NULL,
            "scene_element"         VARCHAR(64 char) NOT NULL,
            "port_id"               VARCHAR(64 char) NOT NULL,
            "name"                  VARCHAR(255 char) NOT NULL,
            "type"                  INT NOT NULL,
            "usable"                TINYINT DEFAULT 1 NULL,
            "mac_addr"              VARCHAR(32 char) NOT NULL,
            "ip"                    VARCHAR(32 char) NOT NULL,
            "netmask"               VARCHAR(32 char) NOT NULL,
            "gateway"               VARCHAR(32 char) NOT NULL,
            "dhcp"                  TINYINT NOT NULL,
            "dns"                   VARCHAR(255 char) NOT NULL,
            "egress"                INT NOT NULL,
            "ingress"               INT NOT NULL,
            "delay"                 INT NOT NULL,
            "loss"                  INT NOT NULL,
            "access_modes"          TEXT NOT NULL,
            "create_time"           TIMESTAMP(6) NOT NULL,
            "update_time"           TIMESTAMP(6) NOT NULL,
            "order"                 INT DEFAULT 0 NOT NULL,
            "is_auxiliary"          TINYINT NOT NULL,
            "port_connect_net_cidr" VARCHAR(20 char) NULL
        );

CREATE TABLE "MP"."mp_scene_scene_element_port_connection"
(
    "pk"              INT IDENTITY(14, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "source_element"  VARCHAR(64 char) NOT NULL,
            "target_element"  VARCHAR(64 char) NOT NULL,
            "source_port"     VARCHAR(64 char) NOT NULL,
            "target_port"     VARCHAR(64 char) NOT NULL,
            "connection_id"   VARCHAR(64 char) NOT NULL,
            "source_port_id"  VARCHAR(64 char) NOT NULL,
            "target_port_id"  VARCHAR(64 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "unidirectional"  TINYINT NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "type"            VARCHAR(64 char) NULL,
            "vertices"        TEXT NULL,
            "info"            TEXT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_element_snapshot"
(
    "pk"            INT IDENTITY(1, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "name"          VARCHAR(255 char) NOT NULL,
            "description"   VARCHAR(2048 char) NOT NULL,
            "address"       VARCHAR(512 char) NOT NULL,
            "create_time"   TIMESTAMP(6) NOT NULL,
            "update_time"   TIMESTAMP(6) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "source_name"   VARCHAR(255 char) NOT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_node_connection"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "source_node_id"  VARCHAR(64 char) NOT NULL,
            "target_node_id"  VARCHAR(64 char) NOT NULL,
            "source_node"     VARCHAR(64 char) NOT NULL,
            "target_node"     VARCHAR(64 char) NOT NULL,
            "connection_id"   VARCHAR(64 char) NOT NULL,
            "name"            VARCHAR(255 char) NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "type"            VARCHAR(64 char) NULL,
            "vertices"        TEXT NULL
        );

CREATE TABLE "MP"."mp_scene_scene_piece"
(
    "pk"              INT IDENTITY(1, 1) NOT NULL,
    "id"              VARCHAR(64 char ) NOT NULL,
            "scene"           VARCHAR(64 char) NOT NULL,
            "node_id"         VARCHAR(64 char) NOT NULL,
            "area"            VARCHAR(64 char) NOT NULL,
            "icon2"           VARCHAR(2048 char) NOT NULL,
            "position2"       VARCHAR(128 char) NOT NULL,
            "icon3"           VARCHAR(2048 char) NOT NULL,
            "position3"       VARCHAR(128 char) NOT NULL,
            "name"            VARCHAR(64 char) NOT NULL,
            "description"     VARCHAR(1024 char) NOT NULL,
            "type"            VARCHAR(64 char) NOT NULL,
            "display_context" VARCHAR(2048 char) NOT NULL,
            "create_time"     TIMESTAMP(6) NOT NULL,
            "update_time"     TIMESTAMP(6) NOT NULL,
            "outer_position2" VARCHAR(1024 char) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene"
(
    "pk"               INT IDENTITY(4, 1) NOT NULL,
    "id"               VARCHAR(64 char ) NOT NULL,
            "scene"            VARCHAR(64 char) NOT NULL,
            "create_time"      TIMESTAMP(6) NOT NULL,
            "update_time"      TIMESTAMP(6) NOT NULL,
            "enable_recording" TINYINT NOT NULL,
            "enable_rate"      TINYINT NOT NULL,
            "dns_nameservers"  TEXT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_config"
(
    "pk"               INT IDENTITY(8, 1) NOT NULL,
    "id"               VARCHAR(64 char ) NOT NULL,
            "scene"            VARCHAR(64 char) NOT NULL,
            "create_time"      TIMESTAMP(6) NOT NULL,
            "update_time"      TIMESTAMP(6) NOT NULL,
            "enable_recording" TINYINT NOT NULL,
            "enable_rate"      TINYINT NOT NULL,
            "dns_nameservers"  TEXT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_config_element"
(
    "pk"                   INT IDENTITY(34, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
            "scene"                VARCHAR(64 char) NOT NULL,
            "scene_element"        VARCHAR(64 char) NOT NULL,
            "external"             TINYINT NOT NULL,
            "virtual_type"         INT NOT NULL,
            "transparent"          TINYINT NOT NULL,
            "resource_id"          VARCHAR(64 char) NOT NULL,
            "electric_port_number" INT NOT NULL,
            "electric_port_type"   VARCHAR(8 char) NOT NULL,
            "optical_port_number"  INT NOT NULL,
            "optical_port_type"    VARCHAR(8 char) NOT NULL,
            "cidr"                 VARCHAR(32 char) NOT NULL,
            "gateway"              VARCHAR(32 char) NOT NULL,
            "gateway_off"          TINYINT NOT NULL,
            "dns_nameservers"      VARCHAR(255 char) NOT NULL,
            "dhcp"                 TINYINT NOT NULL,
            "allocation_pools"     VARCHAR(1024 char) NOT NULL,
            "vlan"                 TINYINT NOT NULL,
            "vlan_id"              INT NOT NULL,
            "static_routes"        TEXT NOT NULL,
            "firewall_rules"       TEXT NOT NULL,
            "image"                VARCHAR(255 char) NOT NULL,
            "initial_tools"        VARCHAR(2048 char) NOT NULL,
            "role"                 INT NOT NULL,
            "image_type"           VARCHAR(64 char) NOT NULL,
            "category"             INT NOT NULL,
            "init_support"         TINYINT NOT NULL,
            "quick_create_support" TINYINT NOT NULL,
            "quick_create_params" CLOB NOT NULL,
            "auxiliary_network_on"                 TINYINT NOT NULL,
            "auxiliary_network_id"                 VARCHAR(64 char) NOT NULL,
            "auxiliary_network_cidr"               VARCHAR(32 char) NOT NULL,
            "auxiliary_network_gateway"            VARCHAR(32 char) NOT NULL,
            "auxiliary_network_dns_nameservers"    VARCHAR(255 char) NOT NULL,
            "auxiliary_network_dhcp"               TINYINT NOT NULL,
            "auxiliary_network_allocation_pools"   VARCHAR(1024 char) NOT NULL,
            "auxiliary_network_vlan"               TINYINT NOT NULL,
            "auxiliary_network_vlan_id"            INT NOT NULL,
            "enable_mgmt"                          TINYINT NOT NULL,
            "mgmt_port_resource_id"                VARCHAR(64 char) NOT NULL,
            "mgmt_ip"                              VARCHAR(32 char) NOT NULL,
            "mgmt_mac_addr"                        VARCHAR(32 char) NOT NULL,
            "create_time"                          TIMESTAMP(6) NOT NULL,
            "update_time"                          TIMESTAMP(6) NOT NULL,
            "wan_port_number"                      INT NOT NULL,
            "wan_port_type"                        VARCHAR(8 char) NOT NULL,
            "lan_port_number"                      INT NOT NULL,
            "lan_port_type"                        VARCHAR(8 char) NOT NULL,
            "host"                                 VARCHAR(64 char) DEFAULT '' NOT NULL,
            "category2"                            INT NOT NULL,
            "category3"                            INT NOT NULL,
            "auxiliary_network_router_on"          TINYINT NOT NULL,
            "auxiliary_network_router_resource_id" VARCHAR(64 char) NOT NULL,
            "auxiliary_network_router_ip"          VARCHAR(32 char) NOT NULL,
            "firewall_resource_id"                 VARCHAR(64 char) NOT NULL,
            "host_routes"                          TEXT NOT NULL,
            "auxiliary_network_host_routes"        TEXT NOT NULL,
            "is_build_in"                          TINYINT NOT NULL,
            "auxiliary_router_off"                 TINYINT NOT NULL,
            "ospf_router_id"                       VARCHAR(16 char) NOT NULL,
            "image_uuid"                           VARCHAR(64 char) NOT NULL,
            "rip_loopback"                         VARCHAR(64 char) NOT NULL,
            "net_mode"                             INT NOT NULL,
            "iso_attach_filename"                  VARCHAR(64 char) NOT NULL,
            "iso_tmpdir_uuid"                      VARCHAR(64 char) NOT NULL,
            "virtual_usbs"                         VARCHAR(512 char) NOT NULL,
            "entity_usbs"                          VARCHAR(512 char) NOT NULL,
            "docker_params"                        TEXT NOT NULL,
            "platform"                             VARCHAR(64 char) NOT NULL,
            "snmp_config"                          TEXT NULL,
            "mib_data"                             TEXT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_config_element_port"
(
    "pk"                   INT IDENTITY(75, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
            "scene"                VARCHAR(64 char) NOT NULL,
            "scene_element_port"   VARCHAR(64 char) NOT NULL,
            "resource_id"          VARCHAR(64 char) NOT NULL,
            "nat"                  TINYINT NOT NULL,
            "entity_access_type"   VARCHAR(64 char) NOT NULL,
            "entity_switch_name"   VARCHAR(64 char) NOT NULL,
            "entity_switch_port"   VARCHAR(64 char) NOT NULL,
            "gateway_port_type"    INT NOT NULL,
            "gateway_port_ips"     VARCHAR(64 char) NOT NULL,
            "auxiliary_network_id" VARCHAR(64 char) NOT NULL,
            "qos_id"               VARCHAR(64 char) NOT NULL,
            "access_modes"         TEXT NOT NULL,
            "floating_ip"          VARCHAR(32 char) NOT NULL,
            "floating_ip_id"       VARCHAR(64 char) NOT NULL,
            "create_time"          TIMESTAMP(6) NOT NULL,
            "update_time"          TIMESTAMP(6) NOT NULL,
            "firewall_rules"       TEXT NOT NULL,
            "firewall_resource_id" VARCHAR(64 char) NOT NULL,
            "ospf_ip"              VARCHAR(64 char) NOT NULL,
            "ospf_area"            VARCHAR(16 char) NOT NULL,
            "bgp_as"               VARCHAR(16 char) NOT NULL,
            "bgp_ip"               VARCHAR(64 char) NOT NULL,
            "bgp_peer"             VARCHAR(64 char) NOT NULL,
            "bgp_peer_as"          VARCHAR(16 char) NOT NULL,
            "entity_port"          VARCHAR(128 char) NOT NULL,
            "connect_mode"         VARCHAR(32 char) NOT NULL,
            "rip_ip"               VARCHAR(32 char) NOT NULL,
            "rip_pear"             VARCHAR(32 char) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_connection"
(
    "pk"                          INT IDENTITY(1, 1) NOT NULL,
    "id"                          VARCHAR(64 char ) NOT NULL,
            "scene"                       VARCHAR(64 char) NOT NULL,
            "scene_element"               VARCHAR(64 char) NOT NULL,
            "scene_element_port"          VARCHAR(64 char) NOT NULL,
            "access_mode_id"              VARCHAR(32 char) NOT NULL,
            "protocol"                    VARCHAR(64 char) NOT NULL,
            "port"                        INT NOT NULL,
            "username"                    VARCHAR(255 char) NOT NULL,
            "password"                    VARCHAR(255 char) NOT NULL,
            "token"                       VARCHAR(2048 char) NOT NULL,
            "access_ip"                   VARCHAR(32 char) NOT NULL,
            "access_port"                 INT NOT NULL,
            "access_user"                 VARCHAR(255 char) NOT NULL,
            "access_pass"                 VARCHAR(255 char) NOT NULL,
            "access_token"                VARCHAR(2048 char) NOT NULL,
            "access_token_expire_time"    TIMESTAMP(6) NOT NULL,
            "connection_id"               VARCHAR(64 char) NOT NULL,
            "connection_url"              VARCHAR(2048 char) NOT NULL,
            "create_time"                 TIMESTAMP(6) NOT NULL,
            "update_time"                 TIMESTAMP(6) NOT NULL,
            "connection_enable_recording" TINYINT NOT NULL,
            "connection_recording_name"   VARCHAR(255 char) NOT NULL,
            "disable"                     TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_element"
(
    "pk"                   INT IDENTITY(17, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
            "scene"                VARCHAR(64 char) NOT NULL,
            "scene_element"        VARCHAR(64 char) NOT NULL,
            "external"             TINYINT NOT NULL,
            "virtual_type"         INT NOT NULL,
            "transparent"          TINYINT NOT NULL,
            "resource_id"          VARCHAR(64 char) NOT NULL,
            "electric_port_number" INT NOT NULL,
            "electric_port_type"   VARCHAR(8 char) NOT NULL,
            "optical_port_number"  INT NOT NULL,
            "optical_port_type"    VARCHAR(8 char) NOT NULL,
            "cidr"                 VARCHAR(32 char) NOT NULL,
            "gateway"              VARCHAR(32 char) NOT NULL,
            "gateway_off"          TINYINT NOT NULL,
            "dns_nameservers"      VARCHAR(255 char) NOT NULL,
            "dhcp"                 TINYINT NOT NULL,
            "allocation_pools"     VARCHAR(1024 char) NOT NULL,
            "vlan"                 TINYINT NOT NULL,
            "vlan_id"              INT NOT NULL,
            "static_routes"        TEXT NOT NULL,
            "firewall_rules"       TEXT NOT NULL,
            "image"                VARCHAR(255 char) NOT NULL,
            "initial_tools"        VARCHAR(2048 char) NOT NULL,
            "role"                 INT NOT NULL,
            "image_type"           VARCHAR(64 char) NOT NULL,
            "category"             INT NOT NULL,
            "init_support"         TINYINT NOT NULL,
            "quick_create_support" TINYINT NOT NULL,
            "quick_create_params" CLOB NOT NULL,
            "auxiliary_network_on"                 TINYINT NOT NULL,
            "auxiliary_network_id"                 VARCHAR(64 char) NOT NULL,
            "auxiliary_network_cidr"               VARCHAR(32 char) NOT NULL,
            "auxiliary_network_gateway"            VARCHAR(32 char) NOT NULL,
            "auxiliary_network_dns_nameservers"    VARCHAR(255 char) NOT NULL,
            "auxiliary_network_dhcp"               TINYINT NOT NULL,
            "auxiliary_network_allocation_pools"   VARCHAR(1024 char) NOT NULL,
            "auxiliary_network_vlan"               TINYINT NOT NULL,
            "auxiliary_network_vlan_id"            INT NOT NULL,
            "enable_mgmt"                          TINYINT NOT NULL,
            "mgmt_port_resource_id"                VARCHAR(64 char) NOT NULL,
            "mgmt_ip"                              VARCHAR(32 char) NOT NULL,
            "mgmt_mac_addr"                        VARCHAR(32 char) NOT NULL,
            "create_time"                          TIMESTAMP(6) NOT NULL,
            "update_time"                          TIMESTAMP(6) NOT NULL,
            "wan_port_number"                      INT NOT NULL,
            "wan_port_type"                        VARCHAR(8 char) NOT NULL,
            "lan_port_number"                      INT NOT NULL,
            "lan_port_type"                        VARCHAR(8 char) NOT NULL,
            "host"                                 VARCHAR(64 char) DEFAULT '' NOT NULL,
            "category2"                            INT NOT NULL,
            "category3"                            INT NOT NULL,
            "auxiliary_network_router_on"          TINYINT NOT NULL,
            "auxiliary_network_router_resource_id" VARCHAR(64 char) NOT NULL,
            "auxiliary_network_router_ip"          VARCHAR(32 char) NOT NULL,
            "firewall_resource_id"                 VARCHAR(64 char) NOT NULL,
            "host_routes"                          TEXT NOT NULL,
            "auxiliary_network_host_routes"        TEXT NOT NULL,
            "is_build_in"                          TINYINT NOT NULL,
            "auxiliary_router_off"                 TINYINT NOT NULL,
            "ospf_router_id"                       VARCHAR(64 char) NOT NULL,
            "image_uuid"                           VARCHAR(64 char) NOT NULL,
            "rip_loopback"                         VARCHAR(64 char) NOT NULL,
            "net_mode"                             INT NOT NULL,
            "iso_attach_filename"                  VARCHAR(64 char) NOT NULL,
            "iso_tmpdir_uuid"                      VARCHAR(64 char) NOT NULL,
            "virtual_usbs"                         VARCHAR(512 char) NOT NULL,
            "entity_usbs"                          VARCHAR(512 char) NOT NULL,
            "docker_params"                        TEXT NOT NULL,
            "platform"                             VARCHAR(64 char) NOT NULL,
            "snmp_config"                          TEXT NULL,
            "mib_data"                             TEXT NULL,
            "fwdns_ips"                            TEXT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_element_network"
(
    "pk"                                   INT IDENTITY(19, 1) NOT NULL,
    "id"                                   VARCHAR(64 char ) NOT NULL,
            "scene"                                VARCHAR(64 char) NOT NULL,
            "scene_element"                        VARCHAR(64 char) NOT NULL,
            "cidr"                                 VARCHAR(32 char) NOT NULL,
            "gateway"                              VARCHAR(32 char) NOT NULL,
            "gateway_off"                          TINYINT NOT NULL,
            "dns_nameservers"                      VARCHAR(255 char) NOT NULL,
            "dhcp"                                 TINYINT NOT NULL,
            "dhcp_ip"                              VARCHAR(32 char) NOT NULL,
            "allocation_pools"                     VARCHAR(1024 char) NOT NULL,
            "vlan"                                 TINYINT NOT NULL,
            "vlan_id"                              INT NOT NULL,
            "host_routes"                          TEXT NOT NULL,
            "resource_id"                          VARCHAR(64 char) NOT NULL,
            "external"                             TINYINT NOT NULL,
            "create_time"                          TIMESTAMP(0) NOT NULL,
            "update_time"                          TIMESTAMP(0) NOT NULL,
            "auxiliary_network_router_on"          TINYINT NOT NULL,
            "auxiliary_network_router_resource_id" VARCHAR(64 char) NOT NULL,
            "auxiliary_network_router_ip"          VARCHAR(32 char) NOT NULL,
            "net_mode"                             INT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincloud_scene_element_port"
(
    "pk"                          INT IDENTITY(36, 1) NOT NULL,
    "id"                          VARCHAR(64 char ) NOT NULL,
            "scene"                       VARCHAR(64 char) NOT NULL,
            "scene_element_port"          VARCHAR(64 char) NOT NULL,
            "resource_id"                 VARCHAR(64 char) NOT NULL,
            "nat"                         TINYINT NOT NULL,
            "entity_access_type"          VARCHAR(64 char) NOT NULL,
            "entity_switch_name"          VARCHAR(64 char) NOT NULL,
            "entity_switch_port"          VARCHAR(64 char) NOT NULL,
            "gateway_port_type"           INT NOT NULL,
            "gateway_port_ips"            VARCHAR(64 char) NOT NULL,
            "auxiliary_network_id"        VARCHAR(64 char) NOT NULL,
            "qos_id"                      VARCHAR(64 char) NOT NULL,
            "access_modes"                TEXT NOT NULL,
            "distributed_port_mapping_ip" VARCHAR(32 char) NOT NULL,
            "floating_ip"                 VARCHAR(32 char) NOT NULL,
            "floating_ip_id"              VARCHAR(64 char) NOT NULL,
            "create_time"                 TIMESTAMP(6) NOT NULL,
            "update_time"                 TIMESTAMP(6) NOT NULL,
            "firewall_rules"              TEXT NOT NULL,
            "firewall_resource_id"        VARCHAR(64 char) NOT NULL,
            "logic_network_id"            VARCHAR(64 char) NOT NULL,
            "ospf_ip"                     VARCHAR(64 char) NOT NULL,
            "ospf_area"                   VARCHAR(16 char) NOT NULL,
            "bgp_as"                      VARCHAR(16 char) NOT NULL,
            "bgp_ip"                      VARCHAR(64 char) NOT NULL,
            "bgp_peer"                    VARCHAR(64 char) NOT NULL,
            "bgp_peer_as"                 VARCHAR(16 char) NOT NULL,
            "entity_port"                 VARCHAR(128 char) NOT NULL,
            "connect_mode"                VARCHAR(32 char) NOT NULL,
            "rip_ip"                      VARCHAR(32 char) NOT NULL,
            "rip_pear"                    VARCHAR(32 char) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene"
(
    "pk"                   INT IDENTITY(4, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
            "scene"                VARCHAR(64 char) NOT NULL,
            "enable_flow_collect"  TINYINT NOT NULL,
            "flow_collector"       VARCHAR(64 char) NOT NULL,
            "create_time"          TIMESTAMP(6) NOT NULL,
            "update_time"          TIMESTAMP(6) NOT NULL,
            "probe_collector"      VARCHAR(64 char) NOT NULL,
            "enable_probe_collect" TINYINT NOT NULL,
            "probe_router"         VARCHAR(64 char) NOT NULL,
            "flow_file"            VARCHAR(256 char) DEFAULT '' NOT NULL,
            "enable_flow_mirror"   TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_config"
(
    "pk"                   INT IDENTITY(8, 1) NOT NULL,
    "id"                   VARCHAR(64 char ) NOT NULL,
            "scene"                VARCHAR(64 char) NOT NULL,
            "enable_flow_collect"  TINYINT NOT NULL,
            "create_time"          TIMESTAMP(6) NOT NULL,
            "update_time"          TIMESTAMP(6) NOT NULL,
            "enable_probe_collect" TINYINT NOT NULL,
            "enable_flow_mirror"   TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_config_element"
(
    "pk"            INT IDENTITY(14, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "probes" CLOB NOT NULL,
            "create_time"          TIMESTAMP(6) NOT NULL,
            "update_time"          TIMESTAMP(6) NOT NULL,
            "enable_probe_collect" TINYINT NOT NULL,
            "edited"               TINYINT DEFAULT 0 NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_config_element_port"
(
    "pk"                  INT IDENTITY(1, 1) NOT NULL,
    "id"                  VARCHAR(64 char ) NOT NULL,
            "scene"               VARCHAR(64 char) NOT NULL,
            "scene_element_port"  VARCHAR(64 char) NOT NULL,
            "enable_flow_collect" TINYINT NOT NULL,
            "create_time"         TIMESTAMP(6) NOT NULL,
            "update_time"         TIMESTAMP(6) NOT NULL,
            "enable_flow_mirror"  TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_data"
(
    "pk"            INT IDENTITY(134473, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "type"          INT NOT NULL,
            "data"          TEXT NOT NULL,
            "update_time"   TIMESTAMP(6) NOT NULL,
            "create_time"   TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_element"
(
    "pk"            INT IDENTITY(7, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "probes" CLOB NOT NULL,
            "create_time"          TIMESTAMP(6) NOT NULL,
            "update_time"          TIMESTAMP(6) NOT NULL,
            "enable_probe_collect" TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_element_port"
(
    "pk"                  INT IDENTITY(1, 1) NOT NULL,
    "id"                  VARCHAR(64 char ) NOT NULL,
            "scene"               VARCHAR(64 char) NOT NULL,
            "scene_element_port"  VARCHAR(64 char) NOT NULL,
            "enable_flow_collect" TINYINT NOT NULL,
            "create_time"         TIMESTAMP(6) NOT NULL,
            "update_time"         TIMESTAMP(6) NOT NULL,
            "enable_flow_mirror"  TINYINT NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_scene_probe_router_port"
(
    "pk"            INT IDENTITY(2, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "network_id"    VARCHAR(64 char) NOT NULL,
            "ip"            VARCHAR(32 char) NOT NULL,
            "create_time"   TIMESTAMP(6) NOT NULL,
            "update_time"   TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_wazuh_rule"
(
    "pk"               INT IDENTITY(3167, 1) NOT NULL,
    "file"             VARCHAR(64 char ) NOT NULL,
            "relative_dirname" VARCHAR(64 char) NOT NULL,
            "id"               VARCHAR(64 char) NOT NULL,
            "level"            INT NOT NULL,
            "status"           VARCHAR(64 char) NOT NULL,
            "details"          VARCHAR(1024 char) NULL,
            "pci_dss"          VARCHAR(256 char) NULL,
            "gpg13"            VARCHAR(256 char) NULL,
            "gdpr"             VARCHAR(256 char) NULL,
            "hipaa"            VARCHAR(256 char) NULL,
            "nist"             VARCHAR(256 char) NULL,
            "tsc"              VARCHAR(256 char) NULL,
            "mitre"            VARCHAR(256 char) NULL,
            "groups"           VARCHAR(256 char) NOT NULL,
            "description"      VARCHAR(256 char) NULL,
            "create_time"      TIMESTAMP(0) NOT NULL,
            "update_time"      TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type"
(
    "pk"          INT IDENTITY(34, 1) NOT NULL,
    "type"        VARCHAR(64 char ) NOT NULL,
            "id"          VARCHAR(64 char) NOT NULL,
            "code"        VARCHAR(64 char) NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"
(
    "pk"          INT IDENTITY(2352, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NULL,
            "rule_id"     VARCHAR(64 char) NOT NULL,
            "type_id"     VARCHAR(64 char) NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene"
(
    "pk"          INT IDENTITY(2, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "scene"       VARCHAR(64 char) NOT NULL,
            "executor"    VARCHAR(64 char) NOT NULL,
            "create_time" TIMESTAMP(6) NOT NULL,
            "update_time" TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene_config"
(
    "pk"          INT IDENTITY(3, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "scene"       VARCHAR(64 char) NOT NULL,
            "create_time" TIMESTAMP(6) NOT NULL,
            "update_time" TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene_config_element"
(
    "pk"            INT IDENTITY(5, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "flows" CLOB NOT NULL,
            "create_time" TIMESTAMP(6) NOT NULL,
            "update_time" TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene_config_element_port"
(
    "pk"                 INT IDENTITY(1, 1) NOT NULL,
    "id"                 VARCHAR(64 char ) NOT NULL,
            "scene"              VARCHAR(64 char) NOT NULL,
            "scene_element_port" VARCHAR(64 char) NOT NULL,
            "create_time"        TIMESTAMP(6) NOT NULL,
            "update_time"        TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene_element"
(
    "pk"            INT IDENTITY(3, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "scene"         VARCHAR(64 char) NOT NULL,
            "scene_element" VARCHAR(64 char) NOT NULL,
            "flows" CLOB NOT NULL,
            "create_time" TIMESTAMP(6) NOT NULL,
            "update_time" TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_scenepluginflow_scene_element_port"
(
    "pk"                 INT IDENTITY(1, 1) NOT NULL,
    "id"                 VARCHAR(64 char ) NOT NULL,
            "scene"              VARCHAR(64 char) NOT NULL,
            "scene_element_port" VARCHAR(64 char) NOT NULL,
            "create_time"        TIMESTAMP(6) NOT NULL,
            "update_time"        TIMESTAMP(6) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_collection_template"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "name"        VARCHAR(200 char) NOT NULL,
            "creator"     VARCHAR(100 char) NULL,
            "description" VARCHAR(600 char) NULL,
            "OS_type"     INT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_collection_template_probe"
(
    "pk"             INT IDENTITY(1, 1) NOT NULL,
    "id"             VARCHAR(64 char ) NOT NULL,
            "template_id"    VARCHAR(64 char) NOT NULL,
            "probe_id"       VARCHAR(64 char) NOT NULL,
            "name"           VARCHAR(32 char) NOT NULL,
            "description"    VARCHAR(400 char) NULL,
            "probe_category" INT DEFAULT 0 NOT NULL,
            "type"           INT DEFAULT 0 NOT NULL,
            "output_format"  VARCHAR(10 char) DEFAULT 'none' NOT NULL,
            "engine"         VARCHAR(40 char) NULL,
            "OS_type"        VARCHAR(20 char) NULL,
            "creator"        VARCHAR(100 char) NULL,
            "disabled"       INT DEFAULT 0 NOT NULL,
            "script_type"    INT NOT NULL,
            "script_url"     VARCHAR(400 char) NULL,
            "interval_sec"   INT DEFAULT 180 NOT NULL,
            "config"         VARCHAR(4000 char) NULL,
            "parameter"      VARCHAR(2000 char) NULL,
            "create_time"    TIMESTAMP(0) NOT NULL,
            "update_time"    TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_flow_classification"
(
    "pk"          BIGINT IDENTITY(43, 1) NOT NULL,
    "id"          VARCHAR(32 char ) NOT NULL,
            "name"        VARCHAR(32 char) NOT NULL,
            "category_en" VARCHAR(255 char) NOT NULL,
            "category_zh" VARCHAR(255 char) NOT NULL,
            "severity"    TINYINT NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NULL
        );

CREATE TABLE "MP"."mp_situation_flow_port_protocol"
(
    "pk"          BIGINT IDENTITY(539, 1) NOT NULL,
    "id"          VARCHAR(32 char ) NOT NULL,
            "port"        INT NOT NULL,
            "protocol"    VARCHAR(32 char) NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_probe"
(
    "pk"             INT IDENTITY(18, 1) NOT NULL,
    "id"             VARCHAR(64 char ) NOT NULL,
            "name"           VARCHAR(200 char) NOT NULL,
            "description"    VARCHAR(600 char) NULL,
            "probe_category" INT DEFAULT 0 NOT NULL,
            "type"           INT DEFAULT 0 NOT NULL,
            "creator"        VARCHAR(100 char) NULL,
            "interval_sec"   INT DEFAULT 180 NOT NULL,
            "output_format"  VARCHAR(10 char) DEFAULT 'none' NOT NULL,
            "engine"         VARCHAR(40 char) NULL,
            "is_deleted"     INT DEFAULT 0 NULL,
            "OS_type"        VARCHAR(20 char) NULL,
            "create_time"    TIMESTAMP(0) NOT NULL,
            "update_time"    TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_probe_ability"
(
    "pk"          INT IDENTITY(178, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "probe_id"    VARCHAR(64 char) NOT NULL,
            "name"        VARCHAR(200 char) NOT NULL,
            "engine"      VARCHAR(40 char) NULL,
            "OS_type"     VARCHAR(20 char) NULL,
            "description" VARCHAR(400 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

ALTER TABLE "MP"."mp_menu_template_info" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_menu_template_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_area" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_area" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_element_plugin" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_element_port_connection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_node_connection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_config_piece" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_element_plugin" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_element_port_connection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_element_snapshot" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_node_connection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scene_scene_piece" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_connection" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element_network" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_data" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_probe_router_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type_relation" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element_port" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_collection_template" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_collection_template_probe" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_flow_classification" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_flow_port_protocol" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_probe" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_probe_ability" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

CREATE INDEX "INDEX17508608083300"
    ON "MP"."mp_sceneplugincollection_scene_data"
        (
         "scene",
         "scene_element"
            );

ALTER INDEX "MP"."INDEX17508608083300" VISIBLE;

CREATE INDEX "org"
    ON "MP"."mp_auth_tenant_user"
        (
         "org",
         "user"
            );

ALTER INDEX "MP"."org" VISIBLE;

CREATE INDEX "user"
    ON "MP"."mp_auth_tenant_user"
        (
         "user",
         "org"
            );

ALTER INDEX "MP"."user" VISIBLE;

CREATE INDEX "url" ON "MP"."mp_auth_url_ac"
    ("url"
        );

ALTER INDEX "MP"."url" VISIBLE;

CREATE INDEX "target"
    ON "MP"."mp_auth_url_ac_target"
        (
         "target"
            );

ALTER INDEX "MP"."target" VISIBLE;

CREATE INDEX "urlac_id"
    ON "MP"."mp_auth_url_ac_target"
        (
         "group"
            );

ALTER INDEX "MP"."urlac_id" VISIBLE;

CREATE INDEX "INDEX17508648634900"
    ON "MP"."mp_auth_user"
        (
         "identifier"
            );

ALTER INDEX "MP"."INDEX17508648634900" VISIBLE;

CREATE INDEX "update_time"
    ON "MP"."mp_auth_user"
        (
         "update_time"
            );

ALTER INDEX "MP"."update_time" VISIBLE;

CREATE INDEX "INDEX17508680675000"
    ON "MP"."mp_auth_user_online_record"
        (
         "user"
            );

ALTER INDEX "MP"."INDEX17508680675000" VISIBLE;

CREATE INDEX "end_time"
    ON "MP"."mp_auth_user_online_record"
        (
         "end_time",
         "status"
            );

ALTER INDEX "MP"."end_time" VISIBLE;

CREATE INDEX "start_time"
    ON "MP"."mp_auth_user_online_record"
        (
         "start_time",
         "end_time",
         "status"
            );

ALTER INDEX "MP"."start_time" VISIBLE;

CREATE INDEX "user_2"
    ON "MP"."mp_auth_user_online_record"
        (
         "user",
         "end_time"
            );

ALTER INDEX "MP"."user_2" VISIBLE;

CREATE INDEX "INDEX17508694445100"
    ON "MP"."mp_auth_user_role"
        (
         "user",
         "role"
            );

ALTER INDEX "MP"."INDEX17508694445100" VISIBLE;

CREATE INDEX "role"
    ON "MP"."mp_auth_user_role"
        (
         "role",
         "user"
            );

ALTER INDEX "MP"."role" VISIBLE;

CREATE INDEX "user_id"
    ON "MP"."mp_cscene_instance_user_count"
        (
         "user_id"
            );

ALTER INDEX "MP"."user_id" VISIBLE;

CREATE INDEX "INDEX17508703488500"
    ON "MP"."mp_cscene_snapshot"
        (
         "cscene_id"
            );

ALTER INDEX "MP"."INDEX17508703488500" VISIBLE;

CREATE INDEX "INDEX17508710372200"
    ON "MP"."mp_devops_log_operation"
        (
         "user_id",
         "create_time"
            );

ALTER INDEX "MP"."INDEX17508710372200" VISIBLE;

CREATE INDEX "op_module_id"
    ON "MP"."mp_devops_log_operation"
        (
         "op_module_id",
         "op_type_id",
         "user_name",
         "op_target",
         "create_time"
            );

ALTER INDEX "MP"."op_module_id" VISIBLE;

CREATE INDEX "INDEX17508734538000"
    ON "MP"."mp_devops_target"
        (
         "role"
            );

ALTER INDEX "MP"."INDEX17508734538000" VISIBLE;

CREATE INDEX "main_category"
    ON "MP"."mp_devops_target"
        (
         "main_category"
            );

ALTER INDEX "MP"."main_category" VISIBLE;

CREATE INDEX "sub_category"
    ON "MP"."mp_devops_target"
        (
         "sub_category"
            );

ALTER INDEX "MP"."sub_category" VISIBLE;

CREATE INDEX "INDEX17508749042500"
    ON "MP"."mp_flow_task_record"
        (
         "task_id"
            );

ALTER INDEX "MP"."INDEX17508749042500" VISIBLE;

CREATE INDEX "create_time"
    ON "MP"."mp_flow_task_record"
        (
         "create_time"
            );

ALTER INDEX "MP"."create_time" VISIBLE;

CREATE INDEX "task_id_and_create_time"
    ON "MP"."mp_flow_task_record"
        (
         "task_id",
         "create_time"
            );

ALTER INDEX "MP"."task_id_and_create_time" VISIBLE;

CREATE INDEX "menu"
    ON "MP"."mp_front_admin_menu_extra_info"
        (
         "menu"
            );

ALTER INDEX "MP"."menu" VISIBLE;

ALTER TABLE "MP"."mp_flow_collection" ADD CONSTRAINT "MPCONS134218969" CHECK ("type_flag" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_pcap" ADD CONSTRAINT "MPCONS134218972" CHECK ("pkt_num" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_pcap" ADD CONSTRAINT "MPCONS134218971" CHECK ("pcap_size" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_pcap" ADD CONSTRAINT "MPCONS134218970" CHECK ("duration" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_protocol" ADD CONSTRAINT "MPCONS134218973" CHECK ("marked" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task" ADD CONSTRAINT "MPCONS134218976" CHECK ("interval_time" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task" ADD CONSTRAINT "MPCONS134218975" CHECK ("delay" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task" ADD CONSTRAINT "MPCONS134218974" CHECK ("count" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task_conf" ADD CONSTRAINT "MPCONS134218979" CHECK ("replay" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task_conf" ADD CONSTRAINT "MPCONS134218978" CHECK ("rate_unlimited" >= 0) ENABLE;

ALTER TABLE "MP"."mp_flow_task_conf" ADD CONSTRAINT "MPCONS134218977" CHECK ("keep_running" >= 0) ENABLE;

ALTER TABLE "MP"."mp_menu_template_info" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_area" ADD CONSTRAINT "scene" UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_area" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_area" ADD UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_config_area" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_element" ADD UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_config_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_element_plugin" ADD CONSTRAINT "scene_element" UNIQUE ("scene_element", "plugin_key");

ALTER TABLE "MP"."mp_scene_scene_config_element_plugin" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_element_port" ADD UNIQUE ("scene", "port_id");

ALTER TABLE "MP"."mp_scene_scene_config_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_element_port_connection" ADD UNIQUE ("scene", "source_port_id", "target_port_id");

ALTER TABLE "MP"."mp_scene_scene_config_element_port_connection" ADD CONSTRAINT "source_port" UNIQUE ("source_port", "target_port");

ALTER TABLE "MP"."mp_scene_scene_config_element_port_connection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_node_connection" ADD UNIQUE ("scene", "source_node_id", "target_node_id");

ALTER TABLE "MP"."mp_scene_scene_config_node_connection" ADD CONSTRAINT "source_node" UNIQUE ("source_node", "target_node");

ALTER TABLE "MP"."mp_scene_scene_config_node_connection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_config_piece" ADD UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_config_piece" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_element" ADD UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_element_plugin" ADD UNIQUE ("scene_element", "plugin_key");

ALTER TABLE "MP"."mp_scene_scene_element_plugin" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_element_port" ADD UNIQUE ("scene_element", "port_id");

ALTER TABLE "MP"."mp_scene_scene_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_element_port_connection" ADD UNIQUE ("scene", "source_port_id", "target_port_id");

ALTER TABLE "MP"."mp_scene_scene_element_port_connection" ADD UNIQUE ("source_port", "target_port");

ALTER TABLE "MP"."mp_scene_scene_element_port_connection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_element_snapshot" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_node_connection" ADD UNIQUE ("scene", "source_node_id", "target_node_id");

ALTER TABLE "MP"."mp_scene_scene_node_connection" ADD UNIQUE ("source_node", "target_node");

ALTER TABLE "MP"."mp_scene_scene_node_connection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scene_scene_piece" ADD UNIQUE ("scene", "node_id");

ALTER TABLE "MP"."mp_scene_scene_piece" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_sceneplugincloud_scene" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_config_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_connection" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element_network" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_sceneplugincloud_scene_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_sceneplugincollection_scene" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_config_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_scene_probe_router_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type_relation" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type_relation" ADD CONSTRAINT "rule_id" UNIQUE ("rule_id", "type_id");

ALTER TABLE "MP"."mp_scenepluginflow_scene" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_scenepluginflow_scene" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config" ADD UNIQUE ("scene");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_scenepluginflow_scene_config_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element" ADD UNIQUE ("scene", "scene_element");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element_port" ADD UNIQUE ("scene", "scene_element_port");

ALTER TABLE "MP"."mp_scenepluginflow_scene_element_port" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_collection_template" ADD UNIQUE ("name");

ALTER TABLE "MP"."mp_situation_collection_template" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_collection_template_probe" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_flow_classification" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_flow_port_protocol" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_probe" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_probe_ability" ADD UNIQUE ("id");

COMMENT ON TABLE "MP"."mp_menu_template_info"
IS
    '菜单模板表';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."en_name"
IS
    '英文名称';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."menu_id"
IS
    '菜单ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."name"
IS
    '中文名称';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."parent"
IS
    '父权限ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."path"
IS
    '路径';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."template_id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."type"
IS
    '资源类型 1:菜单, 2:API, 3:数据';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_menu_template_info"."visible"
IS
    '是否可见 1:可见，0:不可见（受控于激活选项） 默认为可见';
    COMMENT ON TABLE "MP"."mp_menu_template_role"
IS
    '角色关联菜单模板表';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."role_id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."template_id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_menu_template_role"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene"
IS
    '场景实例信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."create_consume_seconds"
IS
    '创建消耗时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."deleted"
IS
    '是否删除';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."event"
IS
    '当前事件';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."event_status"
IS
    '当前事件状态';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."file"
IS
    '场景文件';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."process"
IS
    '当前事件进程';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."process_message"
IS
    '当前事件进程描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."process_status"
IS
    '当前事件进程状态';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."source"
IS
    '源场景模板';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."stage"
IS
    '当前阶段';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."stage_list"
IS
    '阶段列表';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."stage_name"
IS
    '当前阶段名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."stage_percentage"
IS
    '当前阶段百分比';
    COMMENT ON COLUMN "MP"."mp_scene_scene"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_area"
IS
    '场景实例节点坐标表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."open"
IS
    '是否展开';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."type"
IS
    '区域类型 0:普通区域 1:层级区域';
    COMMENT ON COLUMN "MP"."mp_scene_scene_area"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_config"
IS
    '场景拓扑信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."deleted"
IS
    '是否删除';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."file"
IS
    '场景文件';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."source"
IS
    '源实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."type"
IS
    '模板类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_area"
IS
    '场景拓扑节点坐标表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."open"
IS
    '是否展开';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."type"
IS
    '区域类型 0:普通区域 1:层级区域';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_area"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_element"
IS
    '场景拓扑节点信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."cpu"
IS
    'cpu数量';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."disk"
IS
    '磁盘大小';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."file"
IS
    '节点文件';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."init_commands"
IS
    '初始化命令';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."is_auxiliary"
IS
    '是否是辅助节点';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."max_port_number"
IS
    '最大端口数量';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."ram"
IS
    '内存大小';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."system_architecture"
IS
    '系统架构';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."system_language"
IS
    '系统语言';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."system_type"
IS
    '系统类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."type"
IS
    '网元类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_element_plugin"
IS
    '场景拓扑节点插件信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."plugin_key"
IS
    '插件代号';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."scene_element"
IS
    '所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_plugin"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_element_port"
IS
    '场景拓扑节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."access_modes"
IS
    '接入信息列表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."delay"
IS
    '延迟';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."dhcp"
IS
    '是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."dns"
IS
    'dns列表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."egress"
IS
    '上行带宽';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."gateway"
IS
    '网关';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."ingress"
IS
    '下行带宽';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."ip"
IS
    'ip地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."is_auxiliary"
IS
    '是否是辅助端口';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."loss"
IS
    '丢包';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."mac_addr"
IS
    'mac地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."netmask"
IS
    '子网掩码';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."order"
IS
    '端口排序';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."port_connect_net_cidr"
IS
    '路由端口网络cidr';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."port_id"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."scene_element"
IS
    '所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."type"
IS
    '端口类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port"."usable"
IS
    '是否可用';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_element_port_connection"
IS
    '场景拓扑节点端口连线信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."connection_id"
IS
    '连接id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."info"
IS
    '其他信息,前端可自由定义';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."source_element"
IS
    '源端口所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."source_port"
IS
    '源端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."source_port_id"
IS
    '源端口id(port_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."target_element"
IS
    '目标端口所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."target_port"
IS
    '目标端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."target_port_id"
IS
    '目标端口id(port_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."type"
IS
    '连线类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."unidirectional"
IS
    '是否单向';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_element_port_connection"."vertices"
IS
    '路径点';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_node_connection"
IS
    '场景实例节点连线信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."connection_id"
IS
    '连接id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."source_node"
IS
    '源节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."source_node_id"
IS
    '源节点id(node_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."target_node"
IS
    '目标节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."target_node_id"
IS
    '目标节点id(node_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."type"
IS
    '连线类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_node_connection"."vertices"
IS
    '路径点';
    COMMENT ON TABLE "MP"."mp_scene_scene_config_piece"
IS
    '场景拓扑碎片信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."type"
IS
    '碎片类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_config_piece"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_element"
IS
    '场景实例节点信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."cpu"
IS
    'cpu数量';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."disk"
IS
    '磁盘大小';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."event"
IS
    '当前事件';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."event_status"
IS
    '当前事件状态';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."file"
IS
    '节点文件';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."init_commands"
IS
    '初始化命令';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."is_auxiliary"
IS
    '是否是辅助节点';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."max_port_number"
IS
    '最大端口数量';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."process"
IS
    '当前事件进程';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."process_message"
IS
    '当前事件进程描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."process_status"
IS
    '当前事件进程状态';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."ram"
IS
    '内存大小';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."system_architecture"
IS
    '系统架构';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."system_language"
IS
    '系统语言';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."system_type"
IS
    '系统类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."type"
IS
    '网元类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_element_plugin"
IS
    '场景实例节点插件信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."plugin_key"
IS
    '插件代号';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."scene_element"
IS
    '所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_plugin"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_element_port"
IS
    '场景实例节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."access_modes"
IS
    '接入信息列表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."delay"
IS
    '延迟';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."dhcp"
IS
    '是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."dns"
IS
    'dns列表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."egress"
IS
    '上行带宽';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."gateway"
IS
    '网关';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."ingress"
IS
    '下行带宽';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."ip"
IS
    'ip地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."is_auxiliary"
IS
    '是否是辅助端口';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."loss"
IS
    '丢包';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."mac_addr"
IS
    'mac地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."netmask"
IS
    '子网掩码';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."order"
IS
    '端口排序';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."port_connect_net_cidr"
IS
    '路由端口网络cidr';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."port_id"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."scene_element"
IS
    '所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."type"
IS
    '端口类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port"."usable"
IS
    '是否可用';
    COMMENT ON TABLE "MP"."mp_scene_scene_element_port_connection"
IS
    '场景实例节点端口连线信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."connection_id"
IS
    '连接id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."info"
IS
    '其他信息,前端可自由定义';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."source_element"
IS
    '源端口所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."source_port"
IS
    '源端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."source_port_id"
IS
    '源端口id(port_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."target_element"
IS
    '目标端口所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."target_port"
IS
    '目标端口id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."target_port_id"
IS
    '目标端口id(port_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."type"
IS
    '连线类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."unidirectional"
IS
    '是否单向';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_port_connection"."vertices"
IS
    '路径点';
    COMMENT ON TABLE "MP"."mp_scene_scene_element_snapshot"
IS
    '场景实例节点快照信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."address"
IS
    '快照地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."scene"
IS
    '所属场景id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."scene_element"
IS
    '所属网元id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."source_name"
IS
    '源快照名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_element_snapshot"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scene_scene_node_connection"
IS
    '场景实例节点连线信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."connection_id"
IS
    '连接id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."source_node"
IS
    '源节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."source_node_id"
IS
    '源节点id(node_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."target_node"
IS
    '目标节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."target_node_id"
IS
    '目标节点id(node_id)';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."type"
IS
    '连线类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_node_connection"."vertices"
IS
    '路径点';
    COMMENT ON TABLE "MP"."mp_scene_scene_piece"
IS
    '场景实例节点碎片信息表';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."area"
IS
    '所属区域节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."display_context"
IS
    '显示上下文';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."icon2"
IS
    '2d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."icon3"
IS
    '3d图标地址';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."outer_position2"
IS
    '外部2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."position2"
IS
    '2d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."position3"
IS
    '3d坐标';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."type"
IS
    '碎片类型';
    COMMENT ON COLUMN "MP"."mp_scene_scene_piece"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene"
IS
    '场景实例云平台插件信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."dns_nameservers"
IS
    '全局dns设置';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."enable_rate"
IS
    '是否开启环境总速率统计';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."enable_recording"
IS
    '是否开启录屏';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_config"
IS
    '场景拓扑云平台插件信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."dns_nameservers"
IS
    '全局dns设置';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."enable_rate"
IS
    '是否开启环境总速率统计';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."enable_recording"
IS
    '是否开启录屏';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_config_element"
IS
    '场景拓扑云平台插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."allocation_pools"
IS
    '虚拟交换机地址池';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_allocation_pools"
IS
    '辅助网络地址池';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_cidr"
IS
    '辅助网络cidr地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_dhcp"
IS
    '辅助网络是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_dns_nameservers"
IS
    '辅助网络dns';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_gateway"
IS
    '辅助网络网关';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_host_routes"
IS
    '辅助网络静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_id"
IS
    '辅助网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_on"
IS
    '是否开启辅助网络';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_router_ip"
IS
    '辅助网络辅助路由ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_router_on"
IS
    '是否开启辅助网络辅助路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_router_resource_id"
IS
    '辅助网络辅助路由id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_vlan"
IS
    '辅助网络vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_network_vlan_id"
IS
    '辅助网络vlanId';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."auxiliary_router_off"
IS
    '是否关闭辅助路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."category"
IS
    '靶标分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."category2"
IS
    '二级分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."category3"
IS
    '三级分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."cidr"
IS
    '虚拟交换机cidr地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."dhcp"
IS
    '虚拟交换机是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."dns_nameservers"
IS
    '虚拟交换机dns';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."docker_params"
IS
    'docker参数';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."electric_port_number"
IS
    '电口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."electric_port_type"
IS
    '电口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."enable_mgmt"
IS
    '是否开启管理网络网卡';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."entity_usbs"
IS
    '实体usb列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."external"
IS
    '是否外部引用资源';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."firewall_resource_id"
IS
    '虚拟防火墙资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."firewall_rules"
IS
    '虚拟防火墙规则';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."gateway"
IS
    '虚拟交换机网关';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."gateway_off"
IS
    '关闭虚拟交换机网关的分配';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."host"
IS
    '宿主机名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."host_routes"
IS
    '虚拟路由静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."image"
IS
    '虚拟机容器镜像名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."image_type"
IS
    '靶标镜像类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."image_uuid"
IS
    '靶标id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."init_support"
IS
    '是否支持初始化';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."initial_tools"
IS
    '终端初始包含工具';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."is_build_in"
IS
    '是否云平台內置靶标';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."iso_attach_filename"
IS
    '挂载iso文件名';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."iso_tmpdir_uuid"
IS
    '挂载iso临时文件地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."lan_port_number"
IS
    'lan口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."lan_port_type"
IS
    'lan口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."mgmt_ip"
IS
    '管理网络ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."mgmt_mac_addr"
IS
    '管理网络mac地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."mgmt_port_resource_id"
IS
    '管理网络端口资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."mib_data"
IS
    'mib文件数据';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."net_mode"
IS
    '云平台网络类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."optical_port_number"
IS
    '光口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."optical_port_type"
IS
    '光口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."ospf_router_id"
IS
    'ospf网络下的routerID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."platform"
IS
    'gns3 platform';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."quick_create_params"
IS
    '快速构建参数';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."quick_create_support"
IS
    '是否支持快速构建';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."resource_id"
IS
    '云平台虚拟化资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."rip_loopback"
IS
    'rip组网的loopback';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."role"
IS
    '靶标角色';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."snmp_config"
IS
    'snmp相关配置';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."static_routes"
IS
    '虚拟路由静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."transparent"
IS
    '是否透明模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."virtual_type"
IS
    '云平台虚拟化类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."virtual_usbs"
IS
    '虚拟usb列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."vlan"
IS
    '虚拟交换机vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."vlan_id"
IS
    '虚拟交换机vlanId';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."wan_port_number"
IS
    'wan口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element"."wan_port_type"
IS
    'wan口类型';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_config_element_port"
IS
    '场景拓扑云平台插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."access_modes"
IS
    '接入方式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."auxiliary_network_id"
IS
    '辅助网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."bgp_as"
IS
    'bgp组网下的as';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."bgp_ip"
IS
    'bgp组网下的ip地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."bgp_peer"
IS
    'bgp组网下的对端端口ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."bgp_peer_as"
IS
    'bgp组网下的对端端口所属AS';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."connect_mode"
IS
    '组网连接模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."entity_access_type"
IS
    '实体设备接入类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."entity_port"
IS
    '实体设备端口';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."entity_switch_name"
IS
    '实体设备端口连接交换机名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."entity_switch_port"
IS
    '实体设备端口连接交换机端口';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."firewall_resource_id"
IS
    '虚拟防火墙资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."firewall_rules"
IS
    '虚拟防火墙规则';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."floating_ip"
IS
    '浮动ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."floating_ip_id"
IS
    '浮动ip的id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."gateway_port_ips"
IS
    '网关端口地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."gateway_port_type"
IS
    '网关端口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."nat"
IS
    '是否nat外网';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."ospf_area"
IS
    'ospf组网下的area';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."ospf_ip"
IS
    'ospf组网下的ip地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."qos_id"
IS
    'qos id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."resource_id"
IS
    '云平台虚拟化资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."rip_ip"
IS
    'rip组网的ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."rip_pear"
IS
    'rip组网的邻居';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_config_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_element"
IS
    '场景实例云平台插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."allocation_pools"
IS
    '虚拟交换机地址池';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_allocation_pools"
IS
    '辅助网络地址池';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_cidr"
IS
    '辅助网络cidr地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_dhcp"
IS
    '辅助网络是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_dns_nameservers"
IS
    '辅助网络dns';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_gateway"
IS
    '辅助网络网关';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_host_routes"
IS
    '辅助网络静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_id"
IS
    '辅助网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_on"
IS
    '是否开启辅助网络';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_router_ip"
IS
    '辅助网络辅助路由ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_router_on"
IS
    '是否开启辅助网络辅助路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_router_resource_id"
IS
    '辅助网络辅助路由id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_vlan"
IS
    '辅助网络vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_network_vlan_id"
IS
    '辅助网络vlanId';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."auxiliary_router_off"
IS
    '是否关闭辅助路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."category"
IS
    '靶标分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."category2"
IS
    '二级分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."category3"
IS
    '三级分类';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."cidr"
IS
    '虚拟交换机cidr地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."dhcp"
IS
    '虚拟交换机是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."dns_nameservers"
IS
    '虚拟交换机dns';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."docker_params"
IS
    'docker参数';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."electric_port_number"
IS
    '电口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."electric_port_type"
IS
    '电口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."enable_mgmt"
IS
    '是否开启管理网络网卡';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."entity_usbs"
IS
    '实体usb列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."external"
IS
    '是否外部引用资源';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."firewall_resource_id"
IS
    '虚拟防火墙资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."firewall_rules"
IS
    '虚拟防火墙规则';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."fwdns_ips"
IS
    '云平台用于分布式部署时需要的ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."gateway"
IS
    '虚拟交换机网关';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."gateway_off"
IS
    '关闭虚拟交换机网关的分配';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."host"
IS
    '宿主机名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."host_routes"
IS
    '虚拟路由静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."image"
IS
    '虚拟机容器镜像名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."image_type"
IS
    '靶标镜像类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."image_uuid"
IS
    '靶标id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."init_support"
IS
    '是否支持初始化';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."initial_tools"
IS
    '终端初始包含工具';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."is_build_in"
IS
    '是否云平台內置靶标';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."iso_attach_filename"
IS
    '挂载iso文件名';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."iso_tmpdir_uuid"
IS
    '挂载iso临时文件地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."lan_port_number"
IS
    'lan口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."lan_port_type"
IS
    'lan口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."mgmt_ip"
IS
    '管理网络ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."mgmt_mac_addr"
IS
    '管理网络mac地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."mgmt_port_resource_id"
IS
    '管理网络端口资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."mib_data"
IS
    'mib文件数据';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."net_mode"
IS
    '云平台网络类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."optical_port_number"
IS
    '光口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."optical_port_type"
IS
    '光口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."ospf_router_id"
IS
    'ospf网络下的routerID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."platform"
IS
    'gns3 platform';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."quick_create_params"
IS
    '快速构建参数';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."quick_create_support"
IS
    '是否支持快速构建';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."resource_id"
IS
    '云平台虚拟化资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."rip_loopback"
IS
    'rip组网的loopback';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."role"
IS
    '靶标角色';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."snmp_config"
IS
    'snmp相关配置';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."static_routes"
IS
    '虚拟路由静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."transparent"
IS
    '是否透明模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."virtual_type"
IS
    '云平台虚拟化类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."virtual_usbs"
IS
    '虚拟usb列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."vlan"
IS
    '虚拟交换机vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."vlan_id"
IS
    '虚拟交换机vlanId';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."wan_port_number"
IS
    'wan口数量';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element"."wan_port_type"
IS
    'wan口类型';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_element_port"
IS
    '场景实例云平台插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."access_modes"
IS
    '接入方式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."auxiliary_network_id"
IS
    '辅助网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."bgp_as"
IS
    'bgp组网下的as';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."bgp_ip"
IS
    'bgp组网下的ip地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."bgp_peer"
IS
    'bgp组网下的对端端口ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."bgp_peer_as"
IS
    'bgp组网下的对端端口所属AS';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."connect_mode"
IS
    '组网连接模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."distributed_port_mapping_ip"
IS
    '分布式端口映射的ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."entity_access_type"
IS
    '实体设备接入类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."entity_port"
IS
    '实体设备端口';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."entity_switch_name"
IS
    '实体设备端口连接交换机名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."entity_switch_port"
IS
    '实体设备端口连接交换机端口';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."firewall_resource_id"
IS
    '虚拟防火墙资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."firewall_rules"
IS
    '虚拟防火墙规则';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."floating_ip"
IS
    '浮动ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."floating_ip_id"
IS
    '浮动ip的id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."gateway_port_ips"
IS
    '网关端口地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."gateway_port_type"
IS
    '网关端口类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."logic_network_id"
IS
    '网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."nat"
IS
    '是否nat外网';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."ospf_area"
IS
    'ospf组网下的area';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."ospf_ip"
IS
    'ospf组网下的ip地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."qos_id"
IS
    'qos id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."resource_id"
IS
    '云平台虚拟化资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."rip_ip"
IS
    'rip组网的ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."rip_pear"
IS
    'rip组网的邻居';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene"
IS
    '场景实例数据采集插件信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."enable_flow_collect"
IS
    '开启流量采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."enable_flow_mirror"
IS
    '开启流量流量镜像';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."enable_probe_collect"
IS
    '开启探针采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."flow_collector"
IS
    '流量采集器id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."flow_file"
IS
    '流量采集pcap文件地址';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."probe_collector"
IS
    '流量采集器id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."probe_router"
IS
    '采集路由id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_config"
IS
    '场景拓扑数据采集插件信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."enable_flow_collect"
IS
    '开启流量采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."enable_flow_mirror"
IS
    '开启流量流量镜像';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."enable_probe_collect"
IS
    '开启探针采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_config_element"
IS
    '场景拓扑数据采集插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."edited"
IS
    '是否有添加过探针';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."enable_probe_collect"
IS
    '开启探针采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."probes"
IS
    '探针列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_config_element_port"
IS
    '场景拓扑数据采集插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."enable_flow_collect"
IS
    '开启流量采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."enable_flow_mirror"
IS
    '开启流量流量镜像';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_config_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_data"
IS
    '场景实例数据采集插件信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."data"
IS
    '数据';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."type"
IS
    '数据类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_data"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_element"
IS
    '场景实例数据采集插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."enable_probe_collect"
IS
    '开启探针采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."id"
IS
    'id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."probes"
IS
    '探针列表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_element_port"
IS
    '场景实例数据采集插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."enable_flow_collect"
IS
    '开启流量采集';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."enable_flow_mirror"
IS
    '开启流量流量镜像';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_scene_probe_router_port"
IS
    '场景实例数据采集插件探针路由端口表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."ip"
IS
    'ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."network_id"
IS
    '网络id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_scene_probe_router_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_wazuh_rule"
IS
    'wazuh规则表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."details"
IS
    '详细描述';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."file"
IS
    '文件';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."gdpr"
IS
    'gdpr';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."gpg13"
IS
    'gpg13';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."groups"
IS
    '分组';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."hipaa"
IS
    'hipaa';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."id"
IS
    '规则id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."level"
IS
    '规则等级';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."mitre"
IS
    'mitre';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."nist"
IS
    'nist_800_53';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."pci_dss"
IS
    'pci_dss';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."relative_dirname"
IS
    '相对目录名';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."status"
IS
    '状态';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."tsc"
IS
    'tsc';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type"
IS
    'wazuh类型表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."code"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."id"
IS
    '类型id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."type"
IS
    '类型名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"
IS
    'wazuh规则类型关系表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."id"
IS
    'id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."rule_id"
IS
    '规则id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."type_id"
IS
    '类型id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincollection_wazuh_rule_type_relation"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene"
IS
    '场景实例流量插件信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."executor"
IS
    '流量发生器id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene_config"
IS
    '场景拓扑流量插件信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config"."id"
IS
    'id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene_config_element"
IS
    '场景拓扑流量插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."flows"
IS
    '流量列表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene_config_element_port"
IS
    '场景拓扑流量插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."scene"
IS
    '场景模板id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_config_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene_element"
IS
    '场景实例流量插件节点信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."flows"
IS
    '流量列表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_scenepluginflow_scene_element_port"
IS
    '场景实例流量插件节点端口信息表';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_scenepluginflow_scene_element_port"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_flow_classification"
IS
    '流量分类表';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."category_en"
IS
    '分类英文名称';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."category_zh"
IS
    '分类中文名称';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."severity"
IS
    '严重性';
    COMMENT ON COLUMN "MP"."mp_situation_flow_classification"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_flow_port_protocol"
IS
    '流量端口协议表';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."port"
IS
    '端口';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."protocol"
IS
    '协议';
    COMMENT ON COLUMN "MP"."mp_situation_flow_port_protocol"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_probe_ability"
IS
    '探针功能项信息';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."OS_type"
IS
    '比探针的os_type种类要多，0:Windows, 1:Unix, 2:Linux, 3:BSD, 5:cisco-ios, 6:HP, 7:junos, 8:openbsd';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."engine"
IS
    '分析引擎';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."name"
IS
    '功能名称';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."probe_id"
IS
    '探针id';
    COMMENT ON COLUMN "MP"."mp_situation_probe_ability"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_tenant_user"
IS
    '组织用户关系表';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."org"
IS
    '组织ID';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_tenant_user"."user"
IS
    '用户ID';
    COMMENT ON TABLE "MP"."mp_auth_url_ac"
IS
    'URL访问控制表';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."desc"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."group"
IS
    '权限组';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."is_pattern"
IS
    '是否包含模式字符 1:是 0:否';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."methods"
IS
    '支持的请求方法，如get,post';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac"."url"
IS
    'URL pattern';
    COMMENT ON TABLE "MP"."mp_auth_url_ac_target"
IS
    'URL访问控制-权限关系表';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."group"
IS
    '权限组';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."target"
IS
    '权限ID';
    COMMENT ON COLUMN "MP"."mp_auth_url_ac_target"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_auth_user"
IS
    '用户表';
    COMMENT ON COLUMN "MP"."mp_auth_user"."address"
IS
    '地址';
    COMMENT ON COLUMN "MP"."mp_auth_user"."avatar"
IS
    '头像';
    COMMENT ON COLUMN "MP"."mp_auth_user"."birthday"
IS
    '生日';
    COMMENT ON COLUMN "MP"."mp_auth_user"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."date_joined"
IS
    '加入时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."delay_remind_license_time"
IS
    '延期提醒时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_auth_user"."email"
IS
    '邮箱';
    COMMENT ON COLUMN "MP"."mp_auth_user"."expired_time"
IS
    '过期时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_user"."id_card"
IS
    '身份证号';
    COMMENT ON COLUMN "MP"."mp_auth_user"."identifier"
IS
    '编号';
    COMMENT ON COLUMN "MP"."mp_auth_user"."init_check"
IS
    '是否首次登陆需要强制修改密码 0:否，1:是';
    COMMENT ON COLUMN "MP"."mp_auth_user"."is_audited"
IS
    '1:已审核 0：待审核';
    COMMENT ON COLUMN "MP"."mp_auth_user"."is_enabled"
IS
    '1:启用 0：禁用';
    COMMENT ON COLUMN "MP"."mp_auth_user"."last_login"
IS
    '最后登录时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."mobile"
IS
    '手机号';
    COMMENT ON COLUMN "MP"."mp_auth_user"."name"
IS
    '姓名';
    COMMENT ON COLUMN "MP"."mp_auth_user"."nickname"
IS
    '昵称';
    COMMENT ON COLUMN "MP"."mp_auth_user"."password"
IS
    '密码';
    COMMENT ON COLUMN "MP"."mp_auth_user"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_user"."tenant"
IS
    '原生租户';
    COMMENT ON COLUMN "MP"."mp_auth_user"."token_secret"
IS
    'token密钥';
    COMMENT ON COLUMN "MP"."mp_auth_user"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_user"."username"
IS
    '用户名';
    COMMENT ON TABLE "MP"."mp_auth_user_online_record"
IS
    '用户在线记录表';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."duration_minutes"
IS
    '本轮累计在线分钟数';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."end_time"
IS
    '结束时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."start_time"
IS
    '开始时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."status"
IS
    '记录完成与否 0:未完成 1:已完成';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_online_record"."user"
IS
    '用户ID';
    COMMENT ON TABLE "MP"."mp_auth_user_role"
IS
    '用户角色关系表';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."role"
IS
    '角色ID';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_auth_user_role"."user"
IS
    '用户ID';
    COMMENT ON TABLE "MP"."mp_cscene_instance_user_count"
IS
    '用户场景统计表';
    COMMENT ON COLUMN "MP"."mp_cscene_instance_user_count"."count"
IS
    '场景启动数量';
    COMMENT ON COLUMN "MP"."mp_cscene_instance_user_count"."create_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_cscene_instance_user_count"."id"
IS
    '统计id';
    COMMENT ON COLUMN "MP"."mp_cscene_instance_user_count"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_cscene_instance_user_count"."user_id"
IS
    '用户id';
    COMMENT ON TABLE "MP"."mp_cscene_snapshot"
IS
    '场景快照表';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."cscene_id"
IS
    '场景模板';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."cscene_instance_id"
IS
    '场景实例';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."deleted"
IS
    '是否删除';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."name"
IS
    '名称';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."node_id"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."snapshot_id"
IS
    '场景数据库快照id';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."snapshot_resource_id"
IS
    '快照资源id';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."source_name"
IS
    '源快照名称';
    COMMENT ON COLUMN "MP"."mp_cscene_snapshot"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_devops_log_operation"
IS
    '审计日志表';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."content"
IS
    '内容';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."from"
IS
    '日志来源';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."log_type"
IS
    '日志类型：设备日志、系统日志、业务日志、用户日志、场景日志';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_ip"
IS
    '客户端IP';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_module"
IS
    '操作模块';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_module_id"
IS
    '操作模块ID';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_target"
IS
    '操作对象';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_target_id"
IS
    '操作对象ID';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_time"
IS
    '操作时间';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_type"
IS
    '操作类型';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."op_type_id"
IS
    '操作类型ID';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."site_type"
IS
    '站点类型：应用、中台、前端、平台';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."title"
IS
    '主题';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."user_id"
IS
    '用户ID';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."user_name"
IS
    '用户名';
    COMMENT ON COLUMN "MP"."mp_devops_log_operation"."user_type"
IS
    '用户类型（用户，客户端）';
    COMMENT ON TABLE "MP"."mp_devops_target"
IS
    '靶标信息表';
    COMMENT ON COLUMN "MP"."mp_devops_target"."base_image_support"
IS
    '是否支持基础镜像,0:不支持，1：支持';
    COMMENT ON COLUMN "MP"."mp_devops_target"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_devops_target"."create_user"
IS
    '靶标创建用户';
    COMMENT ON COLUMN "MP"."mp_devops_target"."create_user_name"
IS
    '靶标创建用户名';
    COMMENT ON COLUMN "MP"."mp_devops_target"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_devops_target"."image_source"
IS
    '系统镜像类型，1：基础镜像，2：openstack已有镜像，3：自定义上传';
    COMMENT ON COLUMN "MP"."mp_devops_target"."image_status"
IS
    '镜像状态';
    COMMENT ON COLUMN "MP"."mp_devops_target"."image_type"
IS
    '镜像类型：vm，container,simple, entity';
    COMMENT ON COLUMN "MP"."mp_devops_target"."in_position"
IS
    '在位状态';
    COMMENT ON COLUMN "MP"."mp_devops_target"."init_support"
IS
    '是否支持Cloud init,0:不支持，1：支持';
    COMMENT ON COLUMN "MP"."mp_devops_target"."instance_create_user"
IS
    '靶标实例创建用户';
    COMMENT ON COLUMN "MP"."mp_devops_target"."instance_state"
IS
    '镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败';
    COMMENT ON COLUMN "MP"."mp_devops_target"."is_hide"
IS
    '是否隐藏，0：false,1:true';
    COMMENT ON COLUMN "MP"."mp_devops_target"."is_import"
IS
    '是否导入，0：false,1:true';
    COMMENT ON COLUMN "MP"."mp_devops_target"."is_init"
IS
    '是否初始靶标，0：false，1：true';
    COMMENT ON COLUMN "MP"."mp_devops_target"."is_public"
IS
    '是否公开，0：私有，1：自定义，2：公开';
    COMMENT ON COLUMN "MP"."mp_devops_target"."language"
IS
    '系统语言';
    COMMENT ON COLUMN "MP"."mp_devops_target"."logo"
IS
    '图标地址';
    COMMENT ON COLUMN "MP"."mp_devops_target"."main_category"
IS
    '一级分类';
    COMMENT ON COLUMN "MP"."mp_devops_target"."name"
IS
    '镜像名称';
    COMMENT ON COLUMN "MP"."mp_devops_target"."parent_system_type"
IS
    '一级系统类型';
    COMMENT ON COLUMN "MP"."mp_devops_target"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_devops_target"."quick_create_support"
IS
    '是否支持快速构建，0：不支持，1：支持';
    COMMENT ON COLUMN "MP"."mp_devops_target"."reference_status"
IS
    '引用状态';
    COMMENT ON COLUMN "MP"."mp_devops_target"."role"
IS
    '角色';
    COMMENT ON COLUMN "MP"."mp_devops_target"."sub_category"
IS
    '二级分类';
    COMMENT ON COLUMN "MP"."mp_devops_target"."support_export"
IS
    '是否支持导出，0：false,1:true';
    COMMENT ON COLUMN "MP"."mp_devops_target"."system_architecure"
IS
    '系统架构';
    COMMENT ON COLUMN "MP"."mp_devops_target"."system_type"
IS
    '系统类型';
    COMMENT ON COLUMN "MP"."mp_devops_target"."third_category"
IS
    '三级分类';
    COMMENT ON COLUMN "MP"."mp_devops_target"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_devops_target"."use_status"
IS
    '使用状态';
    COMMENT ON COLUMN "MP"."mp_devops_target"."uuid"
IS
    '镜像UUID';
    COMMENT ON TABLE "MP"."mp_flow_collection"
IS
    '流量集合表';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."attack_flow_count"
IS
    '攻击流量数量';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."background_flow_count"
IS
    '背景流量数量';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."category"
IS
    '集合类型   0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."description"
IS
    '集合描述';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."id"
IS
    '集合id';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."title"
IS
    '集合标题';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."type_flag"
IS
    '集合分类：0背景流量，1攻击流量，2混合流量';
    COMMENT ON COLUMN "MP"."mp_flow_collection"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_pcap"
IS
    '流量包表';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."duration"
IS
    '时长(单位ms)';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."id"
IS
    '流量包id';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."pcap_file"
IS
    'pcap文件地址';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."pcap_size"
IS
    '大小(单位Byte)';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."pkt_num"
IS
    '包数(报文数量)';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."protocols"
IS
    '包含协议';
    COMMENT ON COLUMN "MP"."mp_flow_pcap"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_protocol"
IS
    '流量协议表';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."id"
IS
    '协议id';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."marked"
IS
    '是否勾选：0 否，1 是';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."protocol_name"
IS
    '协议名称';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."rate"
IS
    '比例';
    COMMENT ON COLUMN "MP"."mp_flow_protocol"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_task"
IS
    '流量任务表';
    COMMENT ON COLUMN "MP"."mp_flow_task"."actuator_id"
IS
    '流量执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_task"."content"
IS
    '流量内容';
    COMMENT ON COLUMN "MP"."mp_flow_task"."count"
IS
    '执行次数';
    COMMENT ON COLUMN "MP"."mp_flow_task"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_task"."custom_flag"
IS
    '自定义流量任务标志 0：否， 1：是';
    COMMENT ON COLUMN "MP"."mp_flow_task"."delay"
IS
    '首次执行延迟时间，单位：秒(可以为0)';
    COMMENT ON COLUMN "MP"."mp_flow_task"."flow_id"
IS
    '流量id';
    COMMENT ON COLUMN "MP"."mp_flow_task"."flow_platform"
IS
    '流量发生平台：0 通用，1 北京流量发生器';
    COMMENT ON COLUMN "MP"."mp_flow_task"."flow_port_id"
IS
    '流量网口id';
    COMMENT ON COLUMN "MP"."mp_flow_task"."flow_protocol"
IS
    '流量协议';
    COMMENT ON COLUMN "MP"."mp_flow_task"."id"
IS
    '任务id';
    COMMENT ON COLUMN "MP"."mp_flow_task"."interval_time"
IS
    '周期执行间隔，单位：秒';
    COMMENT ON COLUMN "MP"."mp_flow_task"."mult"
IS
    '倍数:大于等于1的整数';
    COMMENT ON COLUMN "MP"."mp_flow_task"."node_id"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_flow_task"."origin_ips"
IS
    '源ip范围';
    COMMENT ON COLUMN "MP"."mp_flow_task"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_task"."running_count"
IS
    '实际执行次数';
    COMMENT ON COLUMN "MP"."mp_flow_task"."target_ips"
IS
    '目标ip范围';
    COMMENT ON COLUMN "MP"."mp_flow_task"."task_desc"
IS
    '任务描述';
    COMMENT ON COLUMN "MP"."mp_flow_task"."task_status"
IS
    '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止';
    COMMENT ON COLUMN "MP"."mp_flow_task"."third_flow_id"
IS
    '流量第三方uuid';
    COMMENT ON COLUMN "MP"."mp_flow_task"."third_flow_name"
IS
    '流量第三方名称';
    COMMENT ON COLUMN "MP"."mp_flow_task"."third_task_id"
IS
    '流量任务第三方uuid';
    COMMENT ON COLUMN "MP"."mp_flow_task"."title"
IS
    '任务标题';
    COMMENT ON COLUMN "MP"."mp_flow_task"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_task_conf"
IS
    '流量任务配置表';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."conf"
IS
    '配置';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."config"
IS
    '是否开启流量重放和速率配置：0 否，1 是';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."id"
IS
    '配置id';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."keep_running"
IS
    '持续运行：0 否，1 是';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."rate_unlimited"
IS
    '速率无限制：0 否，1 是';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."replay"
IS
    '是否流量重放：0 否，1 是';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."task_id"
IS
    '任务id';
    COMMENT ON COLUMN "MP"."mp_flow_task_conf"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_flow_task_record"
IS
    '流量任务记录表';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."actuator_id"
IS
    '流量执行器id';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."drop_rate"
IS
    '丢包率(单位bytes)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."id"
IS
    '记录id';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."jitter"
IS
    '抖动(单位ms)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."latency"
IS
    '延迟(单位ms)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."obytes"
IS
    '发包总大小(单位byte)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."oerrors"
IS
    '发包错误数';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."queue_full"
IS
    '阻塞队列大小(单位byte)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."record_status"
IS
    '是否有效：0 无效，1 有效';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."task_id"
IS
    '任务id';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."total_pps"
IS
    '总发包数';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."total_rx"
IS
    '接收速率(单位bytes)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."total_tx_L1"
IS
    '发送速率L1(单位bytes)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."total_tx_L2"
IS
    '发送速率L2(单位bytes)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."tx_bps"
IS
    '实时发送速率(单位bytes)';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."tx_pps"
IS
    '实时发包数';
    COMMENT ON COLUMN "MP"."mp_flow_task_record"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_front_admin_menu_extra_info"
IS
    '菜单附加信息';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."activ_keys"
IS
    '系统激活key，多个用逗号隔开';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."editions"
IS
    '系统激活版本，多个用逗号隔开  0:标准版 1:专业版';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."menu"
IS
    '菜单id';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."tenant"
IS
    '所属租户';
    COMMENT ON COLUMN "MP"."mp_front_admin_menu_extra_info"."update_time"
IS
    '更新时间';
CREATE TABLE "MP"."mp_situation_probe_param"
(
    "pk"            INT IDENTITY(1, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "probe_id"      VARCHAR(64 char) NULL,
            "template_id"   VARCHAR(64 char) NULL,
            "name"          VARCHAR(50 char) NOT NULL,
            "type"          INT NOT NULL,
            "default_value" VARCHAR(600 char) NULL,
            "description"   VARCHAR(600 char) NULL,
            "create_time"   TIMESTAMP(0) NOT NULL,
            "update_time"   TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_probe_script"
(
    "pk"       INT IDENTITY(13, 1) NOT NULL,
    "id"       VARCHAR(64 char ) NOT NULL,
            "probe_id" VARCHAR(64 char) NULL,
            "script_content" BLOB NULL,
            "script_url"  VARCHAR(400 char) NULL,
            "script_type" INT NOT NULL,
            "version"     INT DEFAULT 1 NOT NULL,
            "status"      INT DEFAULT 1 NOT NULL,
            "checksum"    VARCHAR(100 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_situation_suricatarule"
(
    "pk"                INT IDENTITY(25116, 1) NOT NULL,
    "id"                VARCHAR(64 char ) NOT NULL,
            "action"            VARCHAR(64 char) NOT NULL,
            "protocol"          VARCHAR(64 char) NOT NULL,
            "source"            VARCHAR(1024 char) NOT NULL,
            "source_ports"      VARCHAR(1024 char) NOT NULL,
            "direction"         VARCHAR(32 char) NOT NULL,
            "destination"       VARCHAR(1024 char) NOT NULL,
            "destination_ports" VARCHAR(1024 char) NOT NULL,
            "rule_msg"          TEXT NOT NULL,
            "rule_name"         VARCHAR(1024 char) NOT NULL,
            "sid"               VARCHAR(64 char) NOT NULL,
            "attck"             VARCHAR(64 char) NULL,
            "create_time"       TIMESTAMP(6) NOT NULL,
            "update_time"       TIMESTAMP(6) NOT NULL,
            "class_type_cn"     VARCHAR(1024 char) DEFAULT '' NOT NULL,
            "class_type_en"     VARCHAR(1024 char) DEFAULT '' NOT NULL,
            "rule_gen_type"     INT DEFAULT 0 NOT NULL
        );

CREATE TABLE "MP"."mp_situation_traffic_group"
(
    "pk"               INT IDENTITY(1, 1) NOT NULL,
    "traffic_group_id" VARCHAR(64 char ) NOT NULL,
            "create_time"      TIMESTAMP(0) NOT NULL,
            "del_flag"         INT DEFAULT 0 NULL
        );

CREATE TABLE "MP"."mp_sys_config"
(
    "pk"          INT IDENTITY(19, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "key_name"    VARCHAR(100 char) NULL,
            "value"       VARCHAR(8000 char) NULL,
            "init_value"  VARCHAR(8000 char) DEFAULT '' NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_model_version"
(
    "pk"            INT IDENTITY(13, 1) NOT NULL,
    "id"            VARCHAR(64 char ) NOT NULL,
            "model_name"    VARCHAR(64 char) DEFAULT '' NOT NULL,
            "model_version" VARCHAR(64 char) DEFAULT '' NOT NULL,
            "visible"       TINYINT NOT NULL,
            "create_time"   TIMESTAMP(0) NOT NULL,
            "update_time"   TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_notice"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "content"     TEXT NULL,
            "title"       VARCHAR(150 char) NOT NULL,
            "publisher"   VARCHAR(64 char) NOT NULL,
            "is_public"   INT DEFAULT 0 NOT NULL,
            "type"        INT DEFAULT 1 NOT NULL,
            "top"         BIGINT NOT NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_notice_org"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "notice_id"   VARCHAR(64 char) NOT NULL,
            "org"         VARCHAR(64 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_notice_read"
(
    "pk"           INT IDENTITY(1, 1) NOT NULL,
    "id"           VARCHAR(64 char ) NOT NULL,
            "tenant"       VARCHAR(64 char) NOT NULL,
            "notice_id"    VARCHAR(64 char) NOT NULL,
            "user"         VARCHAR(64 char) NULL,
            "is_readed"    INT DEFAULT 1 NOT NULL,
            "is_need_push" INT DEFAULT 1 NOT NULL,
            "read_count"   INT DEFAULT 1 NOT NULL,
            "create_time"  TIMESTAMP(0) NOT NULL,
            "update_time"  TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_notice_role"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "notice_id"   VARCHAR(64 char) NOT NULL,
            "role"        VARCHAR(64 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

CREATE TABLE "MP"."mp_sys_notice_user"
(
    "pk"          INT IDENTITY(1, 1) NOT NULL,
    "id"          VARCHAR(64 char ) NOT NULL,
            "tenant"      VARCHAR(64 char) NOT NULL,
            "notice_id"   VARCHAR(64 char) NOT NULL,
            "user"        VARCHAR(64 char) NULL,
            "create_time" TIMESTAMP(0) NOT NULL,
            "update_time" TIMESTAMP(0) NOT NULL
        );

ALTER TABLE "MP"."mp_situation_probe_param" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_probe_script" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_suricatarule" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_situation_traffic_group" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_config" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_model_version" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_notice" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_notice_org" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_notice_read" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_notice_role" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

ALTER TABLE "MP"."mp_sys_notice_user" ADD CONSTRAINT NOT CLUSTER PRIMARY KEY ("pk");

CREATE INDEX "INDEX17511042210299"
    ON "MP"."mp_sceneplugincloud_scene_connection"
        (
         "scene",
         "access_mode_id",
         "access_user"
            );

ALTER INDEX "MP"."INDEX17511042210299" VISIBLE;

CREATE INDEX "INDEX17511051202600"
    ON "MP"."mp_sceneplugincloud_scene_element_network"
        (
         "scene",
         "scene_element"
            );

ALTER INDEX "MP"."INDEX17511051202600" VISIBLE;

CREATE INDEX "INDEX17511064432000"
    ON "MP"."mp_situation_collection_template"
        (
         "create_time"
            );

ALTER INDEX "MP"."INDEX17511064432000" VISIBLE;

CREATE INDEX "OS_type"
    ON "MP"."mp_situation_collection_template"
        (
         "OS_type"
            );

ALTER INDEX "MP"."OS_type" VISIBLE;

CREATE INDEX "template_id"
    ON "MP"."mp_situation_collection_template_probe"
        (
         "template_id",
         "probe_id"
            );

ALTER INDEX "MP"."template_id" VISIBLE;

CREATE INDEX "INDEX17511079402500"
    ON "MP"."mp_situation_probe"
        (
         "name"
            );

ALTER INDEX "MP"."INDEX17511079402500" VISIBLE;

CREATE INDEX "INDEX17511085242700"
    ON "MP"."mp_situation_probe"
        (
         "create_time"
            );

ALTER INDEX "MP"."INDEX17511085242700" VISIBLE;

CREATE INDEX "type"
    ON "MP"."mp_situation_probe"
        (
         "type",
         "probe_category",
         "create_time"
            );

ALTER INDEX "MP"."type" VISIBLE;

ALTER TABLE "MP"."mp_situation_probe_param" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_probe_script" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_suricatarule" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_situation_traffic_group" ADD CONSTRAINT "traffic_group_id" UNIQUE ("traffic_group_id");

ALTER TABLE "MP"."mp_sys_config" ADD UNIQUE ("tenant", "key_name");

ALTER TABLE "MP"."mp_sys_config" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_model_version" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_notice" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_notice_org" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_notice_read" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_notice_role" ADD UNIQUE ("id");

ALTER TABLE "MP"."mp_sys_notice_user" ADD UNIQUE ("id");

COMMENT ON TABLE "MP"."mp_situation_traffic_group"
IS
    '流量索引表';
    COMMENT ON COLUMN "MP"."mp_situation_traffic_group"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_traffic_group"."del_flag"
IS
    '1:删除 0:未删除';
    COMMENT ON COLUMN "MP"."mp_situation_traffic_group"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_traffic_group"."traffic_group_id"
IS
    '流量索引前缀名';
    COMMENT ON TABLE "MP"."mp_sys_config"
IS
    '系统配置信息表';
    COMMENT ON COLUMN "MP"."mp_sys_config"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_config"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_config"."init_value"
IS
    '初始值';
    COMMENT ON COLUMN "MP"."mp_sys_config"."key_name"
IS
    '键';
    COMMENT ON COLUMN "MP"."mp_sys_config"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_config"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_config"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sys_config"."value"
IS
    '值';
    COMMENT ON TABLE "MP"."mp_sys_model_version"
IS
    '模块版本信息表';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."model_name"
IS
    '模块名称';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."model_version"
IS
    '模块版本号';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sys_model_version"."visible"
IS
    '是否可见';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_connection"
IS
    '场景实例云平台插件节点接入信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_ip"
IS
    '接入ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_mode_id"
IS
    '接入id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_pass"
IS
    '接入用户密码';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_port"
IS
    '接入端口号';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_token"
IS
    '接入用户token';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_token_expire_time"
IS
    '接入用户token过期时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."access_user"
IS
    '接入用户';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."connection_enable_recording"
IS
    '是否开启录屏';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."connection_id"
IS
    '接入id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."connection_recording_name"
IS
    '录屏名称';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."connection_url"
IS
    '接入url';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."disable"
IS
    '是否禁用';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."password"
IS
    '密码';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."port"
IS
    '端口号';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."protocol"
IS
    '协议';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."scene_element"
IS
    '节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."scene_element_port"
IS
    '端口id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."token"
IS
    '协议接入token';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_connection"."username"
IS
    '用户名';
    COMMENT ON TABLE "MP"."mp_sceneplugincloud_scene_element_network"
IS
    '场景实例云平台插件网络信息表';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."allocation_pools"
IS
    '虚拟网络地址池';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."auxiliary_network_router_ip"
IS
    '辅助网络辅助路由ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."auxiliary_network_router_on"
IS
    '是否开启辅助网络辅助路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."auxiliary_network_router_resource_id"
IS
    '辅助网络辅助路由id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."cidr"
IS
    '虚拟网络cidr地址段';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."dhcp"
IS
    '虚拟网络是否开启dhcp';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."dhcp_ip"
IS
    '指定的dhcp ip';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."dns_nameservers"
IS
    '虚拟网络dns';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."external"
IS
    '虚拟网络vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."gateway"
IS
    '虚拟网络网关';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."gateway_off"
IS
    '关闭虚拟交换机网关的分配';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."host_routes"
IS
    '虚拟路由静态路由';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."net_mode"
IS
    '云平台网络类型';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."resource_id"
IS
    '虚拟网络资源id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."scene"
IS
    '场景实例id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."scene_element"
IS
    '场景实例节点id';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."vlan"
IS
    '虚拟网络vlan模式';
    COMMENT ON COLUMN "MP"."mp_sceneplugincloud_scene_element_network"."vlan_id"
IS
    '虚拟网络vlanId';
    COMMENT ON TABLE "MP"."mp_situation_collection_template"
IS
    '采集模板表';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."OS_type"
IS
    '(单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."creator"
IS
    '创建者用户名';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."name"
IS
    '模板名称';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_collection_template_probe"
IS
    '采集模板探针关系表';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."OS_type"
IS
    '(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."config"
IS
    '探针配置，json串，数组';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."creator"
IS
    '创建者用户名';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."disabled"
IS
    '0:不启用，1:启用';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."engine"
IS
    '分析引擎';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."interval_sec"
IS
    '探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."name"
IS
    '探针名称';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."output_format"
IS
    '自定义探针输出格式: none, json, plain';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."parameter"
IS
    '探针参数';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."probe_category"
IS
    '0:信息类 1:事件类 2:日志类 ';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."probe_id"
IS
    '探针id';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."script_type"
IS
    '0:shell 1:python 2:bat';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."script_url"
IS
    '探针脚本内容fdfs地址，采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."template_id"
IS
    '采集模板id';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."type"
IS
    '0:通用探针 1:自定义探针';
    COMMENT ON COLUMN "MP"."mp_situation_collection_template_probe"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_probe"
IS
    '探针基本信息';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."OS_type"
IS
    '(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."creator"
IS
    '创建者用户名';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."engine"
IS
    '分析引擎';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."interval_sec"
IS
    '探针执行间隔，为大于1的数值，单位秒';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."is_deleted"
IS
    '1:删除 0:未删除';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."name"
IS
    '探针名称';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."output_format"
IS
    '自定义探针输出格式: none, json, plain';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."probe_category"
IS
    '0:信息类 1:事件类 2:日志类 ';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."type"
IS
    '0:通用探针 1:自定义探针';
    COMMENT ON COLUMN "MP"."mp_situation_probe"."update_time"
IS
    '更新时间';
CREATE INDEX "INDEX17511415954500"
    ON "MP"."mp_situation_probe_param"
        (
         "template_id",
         "probe_id"
            );

ALTER INDEX "MP"."INDEX17511415954500" VISIBLE;

CREATE INDEX "probe_id"
    ON "MP"."mp_situation_probe_script"
        (
         "probe_id",
         "script_type"
            );

ALTER INDEX "MP"."probe_id" VISIBLE;

CREATE INDEX "attck"
    ON "MP"."mp_situation_suricatarule"
        (
         "attck"
            );

ALTER INDEX "MP"."attck" VISIBLE;

CREATE INDEX "protocol"
    ON "MP"."mp_situation_suricatarule"
        (
         "protocol"
            );

ALTER INDEX "MP"."protocol" VISIBLE;

CREATE INDEX "rule_name"
    ON "MP"."mp_situation_suricatarule"
        (
         "rule_name"
            );

ALTER INDEX "MP"."rule_name" VISIBLE;

CREATE INDEX "INDEX17511498725599"
    ON "MP"."mp_sys_notice"
        (
         "title"
            );

ALTER INDEX "MP"."INDEX17511498725599" VISIBLE;

CREATE INDEX "INDEX17511505887399"
    ON "MP"."mp_sys_notice_org"
        (
         "org",
         "notice_id"
            );

ALTER INDEX "MP"."INDEX17511505887399" VISIBLE;

CREATE INDEX "INDEX17511512777900"
    ON "MP"."mp_sys_notice_read"
        (
         "user",
         "notice_id"
            );

ALTER INDEX "MP"."INDEX17511512777900" VISIBLE;

CREATE INDEX "notice_id"
    ON "MP"."mp_sys_notice_read"
        (
         "notice_id"
            );

ALTER INDEX "MP"."notice_id" VISIBLE;

CREATE INDEX "INDEX17511524726600"
    ON "MP"."mp_sys_notice_role"
        (
         "role",
         "notice_id"
            );

ALTER INDEX "MP"."INDEX17511524726600" VISIBLE;

CREATE INDEX "INDEX17511530697100"
    ON "MP"."mp_sys_notice_user"
        (
         "user",
         "notice_id"
            );

ALTER INDEX "MP"."INDEX17511530697100" VISIBLE;

    COMMENT ON TABLE "MP"."mp_situation_probe_param"
IS
    '探针或者采集模板的参数';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."default_value"
IS
    '默认值';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."description"
IS
    '描述';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."name"
IS
    '探针名称';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."probe_id"
IS
    '探针id';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."template_id"
IS
    '采集模板id';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."type"
IS
    '参数类型 0:number 1:string 2:bool 3:ip';
    COMMENT ON COLUMN "MP"."mp_situation_probe_param"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_situation_probe_script"
IS
    '探针脚本';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."checksum"
IS
    '脚本内容md5';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."probe_id"
IS
    '探针id';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."script_content"
IS
    '探针脚本内容';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."script_type"
IS
    '脚本类型 0:shell 1:python 2:bat';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."script_url"
IS
    '探针脚本内容url地址';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."status"
IS
    '脚本状态';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_situation_probe_script"."version"
IS
    '脚本版本';
    COMMENT ON TABLE "MP"."mp_situation_suricatarule"
IS
    '流量分析规则表';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."action"
IS
    '匹配规则时进行的操作';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."attck"
IS
    'Att&ck';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."class_type_cn"
IS
    '流量规则分类类型中文';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."class_type_en"
IS
    '流量规则分类类型英文';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."destination"
IS
    '目的ip';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."destination_ports"
IS
    '目的端口';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."direction"
IS
    '方向';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."protocol"
IS
    '协议';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."rule_gen_type"
IS
    '规则生成类型0-预定义，用户不能编辑 1-自定义，用户可以编辑';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."rule_msg"
IS
    '规则体';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."rule_name"
IS
    '规则名称';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."sid"
IS
    '签名id';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."source"
IS
    '源ip';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."source_ports"
IS
    '源端口';
    COMMENT ON COLUMN "MP"."mp_situation_suricatarule"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sys_notice"
IS
    '系统公告';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."content"
IS
    '公告内容';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."is_public"
IS
    '是否公开，对所有人可见  0:否 1:是 默认是0';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."publisher"
IS
    '发布人';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."title"
IS
    '标题';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."top"
IS
    '置顶数字(采用当前时间戳，越大越靠前)';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."type"
IS
    '公告类型 1:系统、2:课程、3:练习、4:考试';
    COMMENT ON COLUMN "MP"."mp_sys_notice"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sys_notice_org"
IS
    '系统公告组织关系表';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."notice_id"
IS
    '公告ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."org"
IS
    '组织id';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_notice_org"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sys_notice_read"
IS
    '系统公告消息(已读推送)统计表';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."is_need_push"
IS
    '是否推送本条公告 1:是（需要推送）； 0:否（不再推送）默认为1';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."is_readed"
IS
    '是否已读 1:已读 0:未读  首次创建一条数据即为已读';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."notice_id"
IS
    '公告ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."read_count"
IS
    '已读次数 首次创建一条数据即为已读次数为1';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_read"."user"
IS
    '用户ID(读者)';
    COMMENT ON TABLE "MP"."mp_sys_notice_role"
IS
    '系统公告角色关系表';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."notice_id"
IS
    '公告ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."role"
IS
    '角色ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_notice_role"."update_time"
IS
    '更新时间';
    COMMENT ON TABLE "MP"."mp_sys_notice_user"
IS
    '系统公告用户关系表';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."create_time"
IS
    '创建时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."id"
IS
    'ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."notice_id"
IS
    '公告ID';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."pk"
IS
    '主键';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."tenant"
IS
    '租户';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."update_time"
IS
    '更新时间';
    COMMENT ON COLUMN "MP"."mp_sys_notice_user"."user"
IS
    '用户ID';