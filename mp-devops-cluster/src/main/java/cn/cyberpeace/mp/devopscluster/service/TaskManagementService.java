package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.result.BaseObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementRequest;

import java.util.List;

public interface TaskManagementService {

    ListObjResult getPageTaskManagement(TaskManagementPageRequest taskManagementPageRequest);

    ServiceResult<Object> getTaskManagement(TaskManagementRequest taskManagementRequest);

    ServiceResult<Object> insertTaskManagement(TaskManagementRequest taskManagementRequest);

    ServiceResult<Object> patchTaskManagement(TaskManagementRequest taskManagementRequests);

    ServiceResult<Object> deleteTaskManagement(String[] ids);
}
