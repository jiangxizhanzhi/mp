alter table mp_sceneplugincloud_scene_connection
    add column connection_disable_download tinyint(1) null comment '是否禁用文件下载',
    add column connection_disable_upload tinyint(1) null comment '是否禁用文件上传';
