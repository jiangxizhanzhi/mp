package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.Personnel.management.PersonnelManagementBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.PersonnelManagementModel;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.repository.PersonnelManagementMapper;
import cn.cyberpeace.mp.auth.repository.TeamMapper;
import cn.cyberpeace.mp.auth.request.management.PersonnelManagementPageRequest;
import cn.cyberpeace.mp.auth.request.management.PersonnelManagementRequest;
import cn.cyberpeace.mp.auth.service.PersonnelManagementService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonnelManagementServiceImpl implements PersonnelManagementService {

    @Autowired
    private PersonnelManagementMapper personnelManagementMapper;
    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private TeamMapper teamMapper;


    @Override
    public ListObjResult getPagePersonnelManagement(PersonnelManagementPageRequest personnelManagementPageRequest) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(PersonnelManagementModel::getTeamId, personnelManagementPageRequest.getTeamId())
                .pagination(personnelManagementPageRequest.getPage(), personnelManagementPageRequest.getPageSize());
        int total = personnelManagementMapper.total(humanCondition);
        List<PersonnelManagementModel> list = personnelManagementMapper.list(humanCondition);
        //用来返回的数据
        List<PersonnelManagementBo> personnelBoList= new ArrayList<>();

        for (PersonnelManagementModel personnelManagementModel : list) {
            PersonnelManagementBo personnelManagementBo = new PersonnelManagementBo();
            BeanUtils.copyProperties(personnelManagementModel,personnelManagementBo);
            personnelBoList.add(personnelManagementBo);
        }
        //用来存储用户信息
        Map<String, PersonnelManagementBo> personnelManagementBoMap = personnelBoList.stream().collect(Collectors.toMap(PersonnelManagementBo::getUserId, personnelManagementBo -> personnelManagementBo));

        //过滤出用户id
        List<String> userIds = list.stream().map(PersonnelManagementModel::getUserId).collect(Collectors.toList());
        //查询出orgs和roles

        List<UserBo> userBos = tenantUserService.listByUserIds(userIds);
        for (UserBo userBo : userBos) {
            if (personnelManagementBoMap.containsKey(userBo.getId())) {
                PersonnelManagementBo personnelManagementBo = personnelManagementBoMap.get(userBo.getId());
                personnelManagementBo.setOrgs(userBo.getOrgs());
                personnelManagementBo.setRoles(userBo.getRoles());
            }
        }

        List<PersonnelManagementBo> personnelManagementBoList = new ArrayList<>(personnelManagementBoMap.values());
        return new ListObjResult(personnelManagementBoList, total).asResult();

    }

    /**
     * 修改成员管理
     * @param personnelManagementRequestList
     */
    @Override
    public void patchPersonnelManagement(List<PersonnelManagementRequest> personnelManagementRequestList) {
        //1,首先判空
        if (CollectionUtils.isEmpty(personnelManagementRequestList)) {
            throw new MpRuntimeException(AuthErrorCode.MODIFYING_MEMBER_MANAGEMENT);
        }
        //2,先查询相关成员有没有战队
        String teamId = personnelManagementRequestList.get(0).getTeamId();
        List<String> userIds = personnelManagementRequestList.stream().map(PersonnelManagementRequest::getUserId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userIds)) {
            List<PersonnelManagementModel> userExist = personnelManagementMapper.getUserExist(teamId, userIds);
            //如果查询出结果就抛出异常
            if (!CollectionUtils.isEmpty(userExist)){
                throw new MpRuntimeException(AuthErrorCode.USERS_CANNOT_ASSOCIATE);
            }
        }
        //3,根据集合去查询，删除相关数据，如果有用户关联再进行插入，如果没有就直接返回。
        HumanCondition humanCondition = new HumanCondition().filterEqual(PersonnelManagementModel::getTeamId,teamId);
        personnelManagementMapper.filterDelete(humanCondition);
        //4,判断userids不为空,则新增.
        if (!CollectionUtils.isEmpty(userIds)){
            List<PersonnelManagementModel> personnelManagementModelList = new ArrayList<>();
            List<UserBo> userBos = tenantUserService.listByUserIds(userIds);
            for (UserBo userBo : userBos) {
                PersonnelManagementModel personnelManagementModel = new PersonnelManagementModel();
                personnelManagementModel.setName(userBo.getName());
                personnelManagementModel.setTeamId(teamId);
                personnelManagementModel.setUsername(userBo.getUsername());
                personnelManagementModel.setUserId(userBo.getId());
                personnelManagementModelList.add(personnelManagementModel);
            }
            personnelManagementMapper.bulkCreate(personnelManagementModelList);
        }
        //5,最后查询该战队成员管理人员数量,插入到战队表中.
        HumanCondition personnelHumanCondition = new HumanCondition().filterEqual(PersonnelManagementModel::getTeamId, teamId);
        List<PersonnelManagementModel> list = new ArrayList<>();
        list = personnelManagementMapper.list(personnelHumanCondition);
        int size = list.size();
        TeamModel teamModel = teamMapper.get(teamId);
        teamModel.setUserCount(size);
        teamMapper.update(teamModel);
    }
}
