update mp_sys_config set value = '{
	"referenceNums": 1,
	"useDurationStart": 0,
	"useDurationEnd": 14400,
	"enable": false,
	"cron": "0 0 0 1 * ?",
	"deleteDateTimeField": "cpcloud_update_time",
	"deleteDateTime": "2028-02-26 09:40:23",
	"deleteDateTimeRule": 2
}' where key_name = 'image_del_config';

update mp_sys_config set init_value = '{
	"referenceNums": 1,
	"useDurationStart": 0,
	"useDurationEnd": 14400,
	"enable": false,
	"cron": "0 0 0 1 * ?",
	"deleteDateTimeField": "cpcloud_update_time",
	"deleteDateTime": "2028-02-26 09:40:23",
	"deleteDateTimeRule": 2
}' where key_name = 'image_del_config';