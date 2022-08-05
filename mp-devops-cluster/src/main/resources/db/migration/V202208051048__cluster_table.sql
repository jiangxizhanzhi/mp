ALTER TABLE `mp_devops_screenshot`
    ADD INDEX `id_index`(`id`) USING BTREE;

ALTER TABLE `mp_devops_alarm_statistics`
    ADD INDEX `id_index`(`id`) USING BTREE;

ALTER TABLE `mp_devops_system_alarm`
    ADD INDEX `id_index`(`id`) USING BTREE;