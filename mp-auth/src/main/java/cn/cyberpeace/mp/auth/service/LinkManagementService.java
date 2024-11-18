package cn.cyberpeace.mp.auth.service;


import cn.cyberpeace.mp.auth.model.LinkManagementModel;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequest;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequestPage;
import cn.cyberpeace.mp.base.service.result.BaseObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

public interface LinkManagementService {

    ListObjResult getPageLink(LinkManagementRequestPage linkManagementRequest);

    LinkManagementModel getLink(LinkManagementRequest linkManagementRequest);

    Boolean addLink(LinkManagementRequest linkManagementRequest);

    Boolean patchLink(LinkManagementRequest linkManagementRequest);

    ServiceResult<Integer> deleteLink(String[] ids);
}
