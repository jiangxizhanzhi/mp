package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.repository.TeamMapper;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import cn.cyberpeace.mp.devopscluster.model.TaskManagementModel;
import cn.cyberpeace.mp.devopscluster.repository.AssetManagementMapper;
import cn.cyberpeace.mp.devopscluster.repository.TaskManagementMapper;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementRequest;
import cn.cyberpeace.mp.devopscluster.service.AssetManagementService;
import cn.cyberpeace.mp.devopscluster.service.TaskManagementService;
import cn.cyberpeace.mp.devopscluster.vo.TaskManagementModelVo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskManagementServiceImpl implements TaskManagementService {

    @Autowired
    private TaskManagementMapper taskManagementMapper;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private AssetManagementMapper assetManagementMapper;

    @Autowired
    private TeamMapper teamMapper;


    @Override
    public ListObjResult getPageTaskManagement(TaskManagementPageRequest taskManagementPageRequest) {
        if (StringUtils.isEmpty(taskManagementPageRequest.getSceneId())) {
            throw new MpRuntimeException("获取任务管理场景id不能为空");
        }
        HumanCondition humanCondition = new HumanCondition().orderBy(TaskManagementModel::getCreateTime)
                .pagination(taskManagementPageRequest.getPage(), taskManagementPageRequest.getPageSize())
                .filterEqual(TaskManagementModel::getSceneId,taskManagementPageRequest.getSceneId());
        if(taskManagementPageRequest.getName()!=null){
            humanCondition.filterContains(TaskManagementModel::getName,taskManagementPageRequest.getName());
        }
        int total = taskManagementMapper.total(humanCondition);
        //判断查的数据为不为空
        List<TaskManagementModel> models = taskManagementMapper.list(humanCondition);
        if (CollectionUtil.isEmpty(models)) {
            return new ListObjResult(models, total).asResult();
        }
        //返回参数
        List<TaskManagementModelVo> taskManagementModelVoList = models.stream().map(model -> {
            TaskManagementModelVo taskManagementModelVo = taskModel2Vo(model);
            return taskManagementModelVo;
        }).collect(Collectors.toList());
        //将数据一次性拿出查询出来进行数据封装
        List<String> userId = Lists.newArrayList();
        List<String> assetsId = Lists.newArrayList();
        for (TaskManagementModelVo taskManagementModelVo : taskManagementModelVoList) {
            userId.addAll(taskManagementModelVo.getRedId());
            userId.addAll(taskManagementModelVo.getReferee());
            assetsId.addAll(taskManagementModelVo.getAssetsId());
        }
        //查询资产
        HumanCondition assHumanCondition = new HumanCondition().filterIn(AssetManagementModel::getId, assetsId.toArray());
        List<AssetManagementModel> assList = assetManagementMapper.list(assHumanCondition);
        Map<String, AssetManagementModel> assetManagementModelMap = assList.stream().collect(Collectors.toMap(AssetManagementModel::getId, Function.identity()));

        //判断团队封装数据返回
        if (taskManagementPageRequest.getType().equals("2")) {
            List<UserBo> userBos = tenantUserService.listByUserIds(userId);
            Map<String, UserBo> userBoMap = userBos.stream().collect(Collectors.toMap(UserBo::getId, Function.identity()));
            for (TaskManagementModelVo taskManagementModelVo : taskManagementModelVoList) {
                for (String user : taskManagementModelVo.getRedId()) {
                    if (userBoMap.containsKey(user)) {
                        taskManagementModelVo.getUserBoList().add(userBoMap.get(user));
                    }
                }

                for (String referee : taskManagementModelVo.getReferee()) {
                    if (userBoMap.containsKey(referee)) {
                        taskManagementModelVo.getRefereeList().add(userBoMap.get(referee));
                    }
                }

                for (String assId : taskManagementModelVo.getAssetsId()) {
                    if (assetManagementModelMap.containsKey(assId)) {
                        taskManagementModelVo.getAssetManagementModelList().add(assetManagementModelMap.get(assId));
                    }
                }
            }
        }


        if (taskManagementPageRequest.getType().equals("1")) {

            HumanCondition teamHumanCondition = new HumanCondition().filterIn(TeamModel::getId, userId.toArray());
            List<TeamModel> teamModels = teamMapper.list(teamHumanCondition);
            Map<String, TeamModel> teamModelMap = teamModels.stream().collect(Collectors.toMap(TeamModel::getId, Function.identity()));
            for (TaskManagementModelVo taskManagementModelVo : taskManagementModelVoList) {
                for (String redId : taskManagementModelVo.getRedId()) {
                    if (teamModelMap.containsKey(redId)) {
                        taskManagementModelVo.getUserTeamModelList().add(teamModelMap.get(redId));
                    }
                }

                for (String referee : taskManagementModelVo.getReferee()) {
                    if (teamModelMap.containsKey(referee)) {
                        taskManagementModelVo.getRefereeTeamModelList().add(teamModelMap.get(referee));
                    }
                }

                for (String assId : taskManagementModelVo.getAssetsId()) {
                    if (assetManagementModelMap.containsKey(assId)) {
                        taskManagementModelVo.getAssetManagementModelList().add(assetManagementModelMap.get(assId));
                    }
                }
            }
        }





        return new ListObjResult(taskManagementModelVoList, total).asResult();
    }

    private static TaskManagementModelVo taskModel2Vo(TaskManagementModel model) {
        TaskManagementModelVo taskManagementModelVo = new TaskManagementModelVo();
        BeanUtils.copyProperties(model, taskManagementModelVo);
        taskManagementModelVo.getAssetsName().addAll(JSON.parseArray(model.getAssetsName(), String.class));
        taskManagementModelVo.getReferee().addAll(JSON.parseArray(model.getRefereeId(), String.class));
        taskManagementModelVo.getRedId().addAll(JSON.parseArray(model.getRedId(), String.class));
        return taskManagementModelVo;
    }

    @Override
    public ServiceResult<Object> getTaskManagement(TaskManagementRequest taskManagementRequest) {
        if (StringUtils.isEmpty(taskManagementRequest.getId())) {
            throw new MpRuntimeException("获取任务管理任务id不能为空");
        }
        TaskManagementModel taskManagementModel = taskManagementMapper.get(taskManagementRequest.getId());
        TaskManagementModelVo taskManagementModelVo = taskModel2Vo(taskManagementModel);
        List<String> userIdOrTeamId = Lists.newArrayList();
        userIdOrTeamId.addAll(taskManagementModelVo.getReferee());
        userIdOrTeamId.addAll(taskManagementModelVo.getRedId());

        List<AssetManagementModel> assetManagementModels = assetManagementMapper.list(new HumanCondition().filterIn(AssetManagementModel::getId, taskManagementModelVo.getAssetsId().toArray()));
        taskManagementModelVo.getAssetManagementModelList().addAll(assetManagementModels);
        if (taskManagementRequest.getType().equals("1")){
            List<TeamModel> teamModels = teamMapper.list(new HumanCondition().filterIn(TeamModel::getId, userIdOrTeamId.toArray()));
            Map<String, TeamModel> teamModelMap = teamModels.stream().collect(Collectors.toMap(TeamModel::getId, Function.identity()));
            for (String refId : taskManagementModelVo.getReferee()) {
                if (teamModelMap.containsKey(refId)) {
                    taskManagementModelVo.getRefereeTeamModelList().add(teamModelMap.get(refId));
                }
            }
            for (String redId : taskManagementModelVo.getRedId()) {
                if (teamModelMap.containsKey(redId)) {
                    taskManagementModelVo.getUserTeamModelList().add(teamModelMap.get(redId));
                }
            }

        }
        if (taskManagementRequest.getType().equals("2")){
            List<UserBo> userBos = tenantUserService.listByUserIds(userIdOrTeamId);
            Map<String, UserBo> userBoMap = userBos.stream().collect(Collectors.toMap(UserBo::getId, Function.identity()));
            for (String refId : taskManagementModelVo.getReferee()) {
                if (userBoMap.containsKey(refId)) {
                    taskManagementModelVo.getRefereeList().add(userBoMap.get(refId));
                }
            }
            for (String redId : taskManagementModelVo.getRedId()) {
                if (userBoMap.containsKey(redId)) {
                    taskManagementModelVo.getUserBoList().add(userBoMap.get(redId));
                }
            }
        }

        return new ServiceResult<>(taskManagementModelVo);
    }

    @Override
    public ServiceResult<Object> insertTaskManagement(TaskManagementRequest taskManagementRequest) {
        TaskManagementModel taskManagementModel = new TaskManagementModel();
        BeanUtils.copyProperties(taskManagementRequest,taskManagementModel);
        request2Model(taskManagementRequest, taskManagementModel);
        return new ServiceResult<>(taskManagementMapper.create(taskManagementModel));

    }

    @Override
    public ServiceResult<Object> patchTaskManagement(TaskManagementRequest taskManagementRequests) {
        if (StringUtils.isEmpty(taskManagementRequests.getId())) {
            throw new MpRuntimeException("修改任务管理任务id不能为空");
        }
        TaskManagementModel taskManagementModel = new TaskManagementModel();
        BeanUtils.copyProperties(taskManagementRequests,taskManagementModel);
        request2Model(taskManagementRequests,taskManagementModel);
        return new ServiceResult<>(taskManagementMapper.update(taskManagementModel));
    }

    @Override
    public ServiceResult<Object> deleteTaskManagement(String[] ids) {
        return new ServiceResult<>(taskManagementMapper.bulkDelete(ids));
    }

    private static void request2Model(TaskManagementRequest taskManagementRequest, TaskManagementModel taskManagementModel) {
        taskManagementModel.setAssetsId(JSON.toJSONString(taskManagementRequest.getAssetsId()));
        taskManagementModel.setAssetsName(JSON.toJSONString(taskManagementRequest.getAssetsName()));
        taskManagementModel.setRedId(JSON.toJSONString(taskManagementRequest.getRedId()));
        taskManagementModel.setRefereeId(JSON.toJSONString(taskManagementRequest.getRefereeId()));
    }
}
