ALTER TABLE mp_token modify COLUMN `jwt` varchar(1024) default null ;
ALTER TABLE mp_token modify COLUMN `access_token` varchar(1024) default null ;