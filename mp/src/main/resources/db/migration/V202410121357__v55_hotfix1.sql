update mp_auth_target set name = 'ATT&CK框架' where name = 'ATT-CK框架';
update mp_menu_template_info set name = 'ATT&CK框架' where name = 'ATT-CK框架';

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'nginx_rules.xml', 'etc/rules', '35100', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'nginx 404 error', '2024-09-13 15:12:22', '2024-09-13 15:12:22');
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '35100', '8', '2024-09-13 15:12:22', '2024-09-13 15:12:22');

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'httpd_rules.xml', 'etc/rules', '35150', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'httpd 404 error', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '35150', '8', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'tomcat_rules.xml', 'etc/rules', '35300', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'tomcat 404 error', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '35300', '8', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'weblogic_rules.xml', 'etc/rules', '35200', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'weblogic 404 error', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '35200', '8', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'jboss_rules.xml', 'etc/rules', '35250', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'jboss 404 error', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '35250', '8', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34150', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'Redis_rules.xml', 'etc/rules', '34150', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'Redis messages grouped.', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34151', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'Redis_rules.xml', 'etc/rules', '34151', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'Redis: started', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34152', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'Redis_rules.xml', 'etc/rules', '34152', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'Redis: shutdown', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34153', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'Redis_rules.xml', 'etc/rules', '34153', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'Redis: Warning / Error', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34250', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'mysql_rules.xml', 'etc/rules', '34250', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'mysql user login failed', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34251', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'mysql_rules.xml', 'etc/rules', '34251', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'mysql started', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34200', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'oracle_rules.xml', 'etc/rules', '34200', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'oracle CJQ0 server started', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34201', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'oracle_rules.xml', 'etc/rules', '34201', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'oracle AQPC server started', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34100', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'dm_database_rules.xml', 'etc/rules', '34100', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'dm database message', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34101', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'dm_database_rules.xml', 'etc/rules', '34101', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'dm database started', now(), now());

INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule_type_relation`( `id`, `rule_id`, `type_id`, `create_time`, `update_time`) VALUES ( NULL, '34300', '9', now(), now());
INSERT IGNORE INTO `mp_sceneplugincollection_wazuh_rule`( `file`, `relative_dirname`, `id`, `level`, `status`, `details`, `pci_dss`, `gpg13`, `gdpr`, `hipaa`, `nist`, `tsc`, `mitre`, `groups`, `description`, `create_time`, `update_time`) VALUES ( 'postgres_rules.xml', 'etc/rules', '34300', 7, 'enabled', '', '[]', '[]', '[]', '[]', '[]', '[]', '[]', '[\"syslog\"]', 'postgres database started', now(), now());

INSERT IGNORE INTO `mp_common_option`( `id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES ( 'fe6dd6f30f0411ed996d0242ac110002', '5', '', '授权', 'licence', 5, 'notice_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', now(), now());
