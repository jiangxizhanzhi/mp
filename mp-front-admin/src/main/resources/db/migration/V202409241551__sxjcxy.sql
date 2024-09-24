UPDATE `mp_sys_config`
SET `value`      = '1',
    `init_value` = '1'
WHERE `tenant` = 'default' and `key_name` = 'screen_recording';
