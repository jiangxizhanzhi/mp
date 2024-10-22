package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table("mp_asset_management")
public class AssetManagementModel extends BaseModel {

    //资产名称
    private String name;
    //资产地址
    private String address;
    //分值
    private String score;
    //资产描述
    private String description;
    //队伍或者人员id
    private String teamId;
    //相关联的场景ID
    private String sceneId;

}
