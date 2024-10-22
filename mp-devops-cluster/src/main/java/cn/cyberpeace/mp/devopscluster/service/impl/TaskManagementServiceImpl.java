package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskManagementServiceImpl implements TaskManagementService {

    @Autowired
    private TaskManagementMapper taskManagementMapper;


    @Override
    public ListObjResult getPageTaskManagement(TaskManagementPageRequest taskManagementPageRequest) {
        if (StringUtils.isEmpty(taskManagementPageRequest.getSceneId())) {
            throw new MpRuntimeException("获取任务管理场景id不能为空");
        }
        HumanCondition humanCondition = new HumanCondition().orderBy(TaskManagementModel::getCreateTime)
                .pagination(taskManagementPageRequest.getPage(), taskManagementPageRequest.getPageSize())
                .filterContains(AssetManagementModel::getSceneId,taskManagementPageRequest.getSceneId());
        int total = taskManagementMapper.total(humanCondition);
        List<TaskManagementModel> models = taskManagementMapper.list(humanCondition);
        //TODO：没有筛选人员条件
        return new ListObjResult(models, total).asResult();
    }

    @Override
    public ServiceResult<Object> getTaskManagement(TaskManagementRequest taskManagementRequest) {
        if (StringUtils.isEmpty(taskManagementRequest.getId())) {
            throw new MpRuntimeException("获取任务管理任务id不能为空");
        }
        TaskManagementModel taskManagementModel = taskManagementMapper.get(taskManagementRequest.getId());
        return new ServiceResult<>(taskManagementModel);
    }

    @Override
    public ServiceResult<Object> insertTaskManagement(TaskManagementRequest taskManagementRequest) {
        TaskManagementModel taskManagementModel = new TaskManagementModel();
        BeanUtils.copyProperties(taskManagementRequest,taskManagementModel);

        return new ServiceResult<>(taskManagementMapper.create(taskManagementModel));

    }

    @Override
    public ServiceResult<Object> patchTaskManagement(TaskManagementRequest taskManagementRequests) {
        if (StringUtils.isEmpty(taskManagementRequests.getId())) {
            throw new MpRuntimeException("修改任务管理任务id不能为空");
        }
        TaskManagementModel taskManagementModel = new TaskManagementModel();
        BeanUtils.copyProperties(taskManagementRequests,taskManagementModel);

        return new ServiceResult<>(taskManagementMapper.update(taskManagementModel));
    }

    @Override
    public ServiceResult<Object> deleteTaskManagement(String[] ids) {
        return new ServiceResult<>(taskManagementMapper.bulkDelete(ids));
    }
}
