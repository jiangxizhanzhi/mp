package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TeamMapper;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import cn.cyberpeace.mp.devopscluster.repository.AssetManagementMapper;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.service.AssetManagementService;
import cn.cyberpeace.mp.devopscluster.vo.AssetManagementVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssetManagementServiceImpl implements AssetManagementService {

    @Autowired
    private AssetManagementMapper assetManagementMapper;
    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TenantUserService tenantUserService;

    @Override
    public ListObjResult getPageAssetManagement(AssetManagementPageRequest assetManagementPageRequest) {
        if (StringUtils.isEmpty(assetManagementPageRequest.getSceneId())) {
            throw new MpRuntimeException("获取资产管理场景id不能为空");
        }
            HumanCondition humanCondition = new HumanCondition().orderBy(AssetManagementModel::getCreateTime)
                    .pagination(assetManagementPageRequest.getPage(), assetManagementPageRequest.getPageSize())
                    .filterContains(AssetManagementModel::getName,assetManagementPageRequest.getName())
                    .filterContains(AssetManagementModel::getSceneId,assetManagementPageRequest.getSceneId());
            int total = assetManagementMapper.total(humanCondition);
            List<AssetManagementModel> models = assetManagementMapper.list(humanCondition);
            //封装返回
        List<AssetManagementVO> assetManagementVOS = models.stream().map(model -> {
            AssetManagementVO assetManagementVO = new AssetManagementVO();
            BeanUtils.copyProperties(model, assetManagementVO);
            return assetManagementVO;
        }).collect(Collectors.toList());
        //用户或者是战队的id
        List<String> teamIdsOrUserIds = Lists.newArrayList();
        for (AssetManagementVO assetManagementVO : assetManagementVOS) {
            List<String> ids = JSON.parseArray(assetManagementVO.getTeamId(), String.class);
            teamIdsOrUserIds.addAll(ids);
        }

        //1是团队2是个人
        if (1==assetManagementPageRequest.getType()) {
            HumanCondition teamHumanCondition = new HumanCondition().filterIn(TeamModel::getId, teamIdsOrUserIds.toArray());
            List<TeamModel> list = teamMapper.list(teamHumanCondition);
            Map<String, TeamModel> teamModelMap = list.stream().collect(Collectors.toMap(TeamModel::getId, Function.identity()));
            for (AssetManagementVO assetManagementVO : assetManagementVOS) {
                List<String> ids = JSON.parseArray(assetManagementVO.getTeamId(), String.class);
                for (String id : ids) {
                    if (teamModelMap.containsKey(id)){
                        assetManagementVO.getTeamModels().add(teamModelMap.get(id));
                    }
                }
            }
        }
        if (2==assetManagementPageRequest.getType()){
            List<UserBo> userBos = tenantUserService.listByUserIds(teamIdsOrUserIds);
            Map<String, UserBo> userBoMap = userBos.stream().collect(Collectors.toMap(UserBo::getId, Function.identity()));
            for (AssetManagementVO assetManagementVO : assetManagementVOS) {
                List<String> userIds = JSON.parseArray(assetManagementVO.getTeamId(), String.class);
                for (String userId : userIds) {
                    if (userBoMap.containsKey(userId)) {
                        assetManagementVO.getUserBoList().add(userBoMap.get(userId));
                    }
                }
            }
            }

        return new ListObjResult(assetManagementVOS, total).asResult();

        }





    @Override
    public ServiceResult<Object> getAssetManagement(AssetManagementRequest assetManagementRequest) {
        if (StringUtils.isEmpty(assetManagementRequest.getId())) {
            throw new MpRuntimeException("获取资产管理资产id不能为空");
        }
        AssetManagementModel assetManagementModel = assetManagementMapper.get(assetManagementRequest.getId());
        AssetManagementVO assetManagementVO = new AssetManagementVO();
        BeanUtils.copyProperties(assetManagementModel,assetManagementVO);

        List<String> ids = JSON.parseArray(assetManagementModel.getTeamId(), String.class);
        if (1==assetManagementRequest.getType()){
            HumanCondition humanCondition = new HumanCondition().filterIn(TeamModel::getId, ids.toArray());
            List<TeamModel> list = teamMapper.list(humanCondition);
            assetManagementVO.getTeamModels().addAll(list);
        }
        if (2==assetManagementRequest.getType()){
            List<UserBo> userBos = tenantUserService.listByUserIds(ids);
            assetManagementVO.getUserBoList().addAll(userBos);
        }
        return new ServiceResult<>(assetManagementVO);
    }

    @Override
    public ServiceResult<Object> insertPageAssetManagement(AssetManagementRequest assetManagementRequest) {
        AssetManagementModel assetManagementModel = new AssetManagementModel();
        BeanUtils.copyProperties(assetManagementRequest,assetManagementModel);
        assetManagementModel.setTeamId(JSON.toJSONString(assetManagementRequest.getTeamId()));

        return new ServiceResult<>(assetManagementMapper.create(assetManagementModel));
    }

    @Override
    public ServiceResult<Object> patchPageAssetManagement(AssetManagementRequest assetManagementRequest) {
        if (StringUtils.isEmpty(assetManagementRequest.getId())) {
            throw new MpRuntimeException("修改资产管理资产id不能为空");
        }
        AssetManagementModel assetManagementModel = new AssetManagementModel();
        BeanUtils.copyProperties(assetManagementRequest,assetManagementModel);
        assetManagementModel.setTeamId(JSON.toJSONString(assetManagementRequest.getTeamId()));

        return new ServiceResult<>(assetManagementMapper.update(assetManagementModel));
    }

    @Override
    public ServiceResult<Object> deletePageAssetManagement(String[] ids) {

        return new ServiceResult<>(assetManagementMapper.bulkDelete(ids));
    }

    @Override
    public ServiceResult<Object> insertListAssetManagement(List<AssetManagementRequest> assetManagementRequest) {
        List<AssetManagementModel> list = assetManagementRequest.stream().map(s -> {
            AssetManagementModel assetManagementModel = new AssetManagementModel();
            BeanUtils.copyProperties(s, assetManagementModel);
            assetManagementModel.setTeamId(JSON.toJSONString(s.getTeamId()));
            return assetManagementModel;
        }).collect(Collectors.toList());
        return new ServiceResult<>(assetManagementMapper.bulkCreate(list));
    }
}
