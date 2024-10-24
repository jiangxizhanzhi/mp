package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskManagementModelVo extends BaseVO{

    //任务名称
    private String name;
    //资产id
    private List<AssetManagementModel> assetManagementModelList = Lists.newArrayList();
    //资产id
    private List<String>assetsName = Lists.newArrayList();
    //资产id
    private List<String> assetsId = Lists.newArrayList();
    //红方人员或者队伍id
    private List<String>redId = Lists.newArrayList();

    private List<String>referee = Lists.newArrayList();

    private List<UserBo> userBoList = Lists.newArrayList();
    private List<TeamModel>userTeamModelList = Lists.newArrayList();
    //裁判id或者裁判队伍id
    private List<UserBo> refereeList = Lists.newArrayList();
    private List<TeamModel>refereeTeamModelList = Lists.newArrayList();

}
