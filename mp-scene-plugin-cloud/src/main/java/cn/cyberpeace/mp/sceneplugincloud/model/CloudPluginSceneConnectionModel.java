package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.stereotype.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
