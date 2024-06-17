package cn.cyberpeace.mp.scenepluginconfig.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_config_config_agent")
public class ConfigAgentModel extends BaseModel {
    @DefaultStr
    private String name;
    @DefaultStr
    private String resourceId;
    @DefaultStr
    private String ip;
    @DefaultStr
    private String server;
    @DefaultStr
    private String logicNetworkId;
    @DefaultStr
    private String nics;
    @DefaultLong
    private Long createConsumeSeconds;
    @DefaultLong
    private Long cloudCreateConsumeSeconds;
    @DefaultLong
    private Long execCmdConsumeSeconds;
}
