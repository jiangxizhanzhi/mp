package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TeamModel;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class AssetManagementVO {

    private String id;
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
    //个人相关数据
    private List<UserBo>userBoList = Lists.newArrayList();
    //队伍相关数据
    private List<TeamModel>teamModels = Lists.newArrayList();

}
