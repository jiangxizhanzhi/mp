package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

/**
 * NPC模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "NPC模型，对应云平台接口返回信息")
public class NpcOpenStackModel extends BaseOpenStackModel implements Serializable {

    private Integer npc_nums;
}
