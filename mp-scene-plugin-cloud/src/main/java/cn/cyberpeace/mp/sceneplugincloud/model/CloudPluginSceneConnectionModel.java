package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String accessToken;
    @DefaultStr
    private String connectionId;
    @DefaultStr
    private String connectionUrl;
}
