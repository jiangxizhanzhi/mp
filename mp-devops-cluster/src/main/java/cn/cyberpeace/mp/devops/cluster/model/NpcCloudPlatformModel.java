package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.NpcModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * NPC模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "NPC模型，对应云平台接口返回信息")
public class NpcCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * NPC列表
     */
    private List<NpcModel> npcModels;

}
