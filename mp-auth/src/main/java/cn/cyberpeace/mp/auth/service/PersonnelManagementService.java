package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.request.management.PersonnelManagementPageRequest;
import cn.cyberpeace.mp.auth.request.management.PersonnelManagementRequest;
import cn.cyberpeace.mp.base.service.result.ListObjResult;

import java.util.List;

public interface PersonnelManagementService {
    ListObjResult getPagePersonnelManagement(PersonnelManagementPageRequest personnelManagementPageRequest);

    void patchPersonnelManagement(List<PersonnelManagementRequest> personnelManagementRequestList);
}
