 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneConnectionModel.java.
 // File size 1708.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.stereotype.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName CloudPluginSceneConnectionModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincloud_scene_connection")
public class CloudPluginSceneConnectionModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultStr
    private String sceneElement;
    @DefaultStr
    private String sceneElementPort;
    @DefaultStr
    private String accessModeId;

    @DefaultStr
    private String protocol;
    @DefaultInt
    private Integer port;
    @DefaultStr
    private String username;
    @DefaultStr
    private String password;
    @DefaultStr
    private String token;

    @DefaultStr
    private String accessIp;
    @DefaultInt
    private Integer accessPort;
    @DefaultStr
    private String accessUser;
    @DefaultStr
    private String accessPass;
    @DefaultStr
    private String accessToken;
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime accessTokenExpireTime;
    @DefaultStr
    private String connectionId;
    @DefaultBool
    private Boolean connectionEnableRecording;
    @DefaultStr
    private String connectionRecordingName;
    @DefaultStr
    private String connectionUrl;
    @DefaultBool
    private Boolean disable;
}
