package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.Personnel.management.PersonnelManagementBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportPersonnelManagementBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportTeamBO;
import cn.cyberpeace.mp.auth.model.PersonnelManagementModel;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.PersonnelManagementMapper;
import cn.cyberpeace.mp.auth.repository.TeamMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.request.team.TeamIdAndUserInfo;
import cn.cyberpeace.mp.auth.request.team.TeamPageRequest;
import cn.cyberpeace.mp.auth.request.team.TeamRequest;
import cn.cyberpeace.mp.auth.service.TeamService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.IdGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PersonnelManagementMapper personnelManagementMapper;

    @Autowired
    private TenantUserService tenantUserService;

    /**
     *分页查询
     * @param teamPageRequest
     * @return
     */
    @Override
    public ListObjResult getPageTeam(TeamPageRequest teamPageRequest) {
        if (StringUtils.isEmpty(teamPageRequest.getName())) {
            HumanCondition humanCondition = new HumanCondition().orderBy(TeamModel::getCreateTime)
                                                                .pagination(teamPageRequest.getPage(), teamPageRequest.getPageSize());
            int total = teamMapper.total(humanCondition);
            List<TeamModel> models = teamMapper.list(humanCondition);
            return new ListObjResult(models, total).asResult();

        } else {
            HumanCondition humanCondition = new HumanCondition().filterContains(TeamModel::getName, teamPageRequest.getName()).orderBy(TeamModel::getCreateTime)
                                                                .pagination(teamPageRequest.getPage(), teamPageRequest.getPageSize());
            int total = teamMapper.total(humanCondition);
            List<TeamModel> list = teamMapper.list(humanCondition);
            return new ListObjResult(list, total).asResult();
        }
    }

    /**
     *添加
     * @param teamRequest
     * @return
     */
    @Override
    public TeamModel addTeam(TeamRequest teamRequest) {
        if (teamRequest.getName().isEmpty()) {
            throw new MpRuntimeException( "添加战队失败,缺少战队名称");
        }
        //检查战队名称是否重复
        checkName(teamRequest);
        String name = UserUtils.getLoginUser().getName();
        teamRequest.setCreator(name);
        TeamModel teamModel = new TeamModel();
        BeanUtils.copyProperties(teamRequest, teamModel);
        teamMapper.create(teamModel);
        return teamModel;
    }


    //检查战队名称是否重复
    private void checkName(TeamRequest teamRequest) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(TeamModel::getName, teamRequest.getName());
        List<TeamModel> list = teamMapper.list(humanCondition);
        if ((!CollectionUtils.isEmpty(list))&&teamRequest.getId()!=null) {
            for (TeamModel teamModel : list) {
                if (!teamModel.getId().equals(teamRequest.getId())) {
                    throw new MpRuntimeException("战队名称重复");
                }
            }
        }
        if ((!CollectionUtils.isEmpty(list))&&teamRequest.getId()==null){
            throw new MpRuntimeException("战队名称重复");
        }
    }

    /**
     *获取
     * @param teamRequest
     * @return
     */
    @Override
    public TeamModel getTeam(TeamRequest teamRequest) {
        if (StringUtils.isEmpty(teamRequest.getId())) {
            throw new MpRuntimeException( "战队查询缺少id");
        }
        TeamModel teamModel = teamMapper.get(teamRequest.getId());
        return teamModel;
    }

    /**
     * 修改
     * @param teamRequest
     * @return
     */
    @Override
    public TeamModel patchTeam(TeamRequest teamRequest) {
        checkName(teamRequest);
        TeamModel teamModel = new TeamModel();
        BeanUtils.copyProperties(teamRequest,teamModel);
        teamMapper.update(teamModel);
        return teamModel;
    }

    @Override
    public ServiceResult<Integer> deleteTeam(String[] ids) {
        int result;
        try {
            result = teamMapper.bulkDelete(ids).getSuccessCount();
        } catch (Exception e) {
            log.error("Service bulk delete {} error: {}", ids, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < ids.length) {
            log.error("Service bulk delete {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    /**
     * 导出战队模板
     * @return
     */
    @Override
    public void exportTeamTemplate(HttpServletResponse response) {
        String fname = "战队信息模板";
        List<ExportTeamBO> exportTeamBOS = new ArrayList<>();
        try {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExportTeamBO.class).sheet("战队模板").doWrite(exportTeamBOS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServiceResult<String> importTeamTemplate(MultipartFile file) {
        List<ExportTeamBO> exportTeamBOS = new ArrayList<>();
        try {
            exportTeamBOS = EasyExcel.read(new BufferedInputStream(file.getInputStream())).head(ExportTeamBO.class).sheet().doReadSync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (exportTeamBOS.size()>1000){
            throw new MpRuntimeException("超过最多处理条数,最多处理1000条");
        }

        if (!CollectionUtils.isEmpty(exportTeamBOS)) {
            String userName = UserUtils.getLoginUser().getName();
            List<TeamModel> teamModels = new ArrayList<>();
            for (ExportTeamBO exportTeamBO : exportTeamBOS) {
                TeamModel teamModel = new TeamModel();
                BeanUtils.copyProperties(exportTeamBO,teamModel);
                teamModel.setCreator(userName);
                teamModels.add(teamModel);
            }
            List<String> teamList = teamModels.stream().map(TeamModel::getName).collect(Collectors.toList());
            List<TeamModel> teamModelList = teamMapper.existTeamList(teamList);
            if (!CollectionUtils.isEmpty(teamModelList)) {
                throw new MpRuntimeException("战队名重复");
            }
            teamMapper.bulkCreate(teamModels);

        }
        return ResultCommon.success();
    }

    @Override
    public void exportPersonnelTemplate(HttpServletResponse response) {
        String fname = "关联关系导入模板";
        List<ExportPersonnelManagementBo> exportPersonnelManagementBos = new ArrayList<>();
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExportPersonnelManagementBo.class).sheet("关联关系导入模板").doWrite(exportPersonnelManagementBos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServiceResult<String> importPersonnelTemplate(MultipartFile file) {
        List<ExportPersonnelManagementBo> exportPersonnelManagementBos = new ArrayList<>();
        try {
            exportPersonnelManagementBos = EasyExcel.read(new BufferedInputStream(file.getInputStream())).head(ExportPersonnelManagementBo.class).sheet().doReadSync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (exportPersonnelManagementBos.size()>1000){
            throw new MpRuntimeException("超过最多处理条数,最多处理1000条");
        }
        //1,排查用户加入不同战队
        List<String> usernameList = exportPersonnelManagementBos.stream().map(ExportPersonnelManagementBo::getUsername).distinct().collect(Collectors.toList());
        List<PersonnelManagementModel> personnelManagementModelList = personnelManagementMapper.selectALl();
        List<String> selectUserName = personnelManagementModelList.stream().map(PersonnelManagementModel::getUsername).collect(Collectors.toList());
        usernameList.addAll(selectUserName);
        if (usernameList.size() != exportPersonnelManagementBos.size()){
            throw new MpRuntimeException("一个用户不能加入2支战队");
        }
        //2,战队名称不存在直接抛异常
        List<String> teamNameList = exportPersonnelManagementBos.stream().map(ExportPersonnelManagementBo::getTeamName).collect(Collectors.toList());
        List<String> distinctTeamNameList = teamNameList.stream().distinct().collect(Collectors.toList());
        //防止filterIn方法查询报错
        if (distinctTeamNameList.size()==0) {
            distinctTeamNameList.add("1");
        }
        HumanCondition humanCondition = new HumanCondition().filterIn(TeamModel::getName, distinctTeamNameList.toArray());
        List<TeamModel> selectByNameTeamList = teamMapper.list(humanCondition);
        if (distinctTeamNameList.size()!=selectByNameTeamList.size()) {
            throw new MpRuntimeException("战队名不存在");
        }
        //3,用户名称不存在直接抛异常
        List<String> userNameList = exportPersonnelManagementBos.stream().map(ExportPersonnelManagementBo::getUsername).collect(Collectors.toList());
        List<String> checkUserNameExistList = checkUserNameExist(userNameList);
        if (userNameList.size()!=checkUserNameExistList.size()) {
            throw new MpRuntimeException("用户名称不存在");
        }
        //增加成员管理信息
        Map<String, PersonnelManagementModel> usernamePersonnelManagementModelHashMap = new HashMap<>();
        for (String username : userNameList) {
            usernamePersonnelManagementModelHashMap.put(username,new PersonnelManagementModel());
        }
        //处理用户名和战队id
        Map<String, ExportPersonnelManagementBo> teamNameToTeamIdExportPersonnelManagementBoMap = exportPersonnelManagementBos.stream().collect(Collectors.toMap(ExportPersonnelManagementBo::getTeamName, exportPersonnelManagementBo -> exportPersonnelManagementBo));
        for (TeamModel teamModel : selectByNameTeamList) {
            if (teamNameToTeamIdExportPersonnelManagementBoMap.containsKey(teamModel.getName())) {
                teamNameToTeamIdExportPersonnelManagementBoMap.get(teamModel.getName()).setTeamName(teamModel.getId());
            }
        }
        List<ExportPersonnelManagementBo> usernameAndTeamId = new ArrayList<>(teamNameToTeamIdExportPersonnelManagementBoMap.values());
        Map<String, String> usernameAndTeamIdMap = usernameAndTeamId.stream().collect(Collectors.toMap(ExportPersonnelManagementBo::getUsername, ExportPersonnelManagementBo::getTeamName));

        HumanCondition userHumanCondition = new HumanCondition().filterIn(UserModel::getUsername, userNameList.toArray());
        List<UserModel> userModelList = userMapper.list(userHumanCondition);
        for (UserModel userModel : userModelList) {
            if (usernamePersonnelManagementModelHashMap.containsKey(userModel.getUsername()) &&usernameAndTeamIdMap.containsKey(userModel.getUsername())) {
                PersonnelManagementModel personnelManagementModel = usernamePersonnelManagementModelHashMap.get(userModel.getUsername());
                personnelManagementModel.setTeamId(usernameAndTeamIdMap.get(userModel.getUsername()));
                personnelManagementModel.setName(userModel.getName());
                personnelManagementModel.setUserId(userModel.getId());
                personnelManagementModel.setUsername(userModel.getUsername());
            }
        }
        List<PersonnelManagementModel> personnelManagementModels = new ArrayList<>(usernamePersonnelManagementModelHashMap.values());
        personnelManagementMapper.bulkCreate(personnelManagementModels);
        return ResultCommon.success();
    }

    /**
     *
     * @param list 战队id集合
     * @return
     */
    @Override
    public List<TeamIdAndUserInfo>  getTeamList(List<String> list) {
        //根据队伍id查询该队伍下的人员信息
        List<TeamIdAndUserInfo> teamIdAndUserInfos = Lists.newArrayList();
        for (String teamId : list) {
            TeamIdAndUserInfo teamIdAndUserInfo = new TeamIdAndUserInfo();
            teamIdAndUserInfo.setTeamId(teamId);
            HumanCondition humanCondition = new HumanCondition().filterEqual(PersonnelManagementModel::getTeamId, teamId);
            List<String> userIds = personnelManagementMapper.list(humanCondition).stream().map(PersonnelManagementModel::getUserId).collect(Collectors.toList());
            teamIdAndUserInfo.getUserBos().addAll(tenantUserService.listByUserIds(userIds));
            teamIdAndUserInfos.add(teamIdAndUserInfo);
        }

        return teamIdAndUserInfos;
    }

    @Override
    public JSONObject getListTeamInfo(List<String> list) {
        HumanCondition humanCondition = new HumanCondition().filterIn(TeamModel::getId, list.toArray());
        List<TeamModel> teamModelList = teamMapper.list(humanCondition);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("teamModelList",teamModelList);
        return jsonObject;
    }

    @Override
    public String getTeamInfoByUserId(String userId) {
        HumanCondition humanCondition = new HumanCondition();
        List<PersonnelManagementModel> list = personnelManagementMapper.list(humanCondition);
        Map<String, String> map = list.stream().collect(Collectors.toMap(PersonnelManagementModel::getUserId, PersonnelManagementModel::getTeamId));
        if (map.containsKey(userId)) {
            return map.get(userId);
        }
        throw new RuntimeException("该人员没有参加战队");
    }

    @Override
    public ServiceResult<JSONObject> getTeamInfoByUser(String userId) {

        return personnelManagementMapper.selectByUserIdToTeamInfo(userId);
    }

    private List<String> checkUserNameExist(List<String> userNameList) {
        HumanCondition condition = new HumanCondition().filterIn(UserModel::getUsername, userNameList.toArray());
        List<UserModel> list = userMapper.list(condition);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(UserModel::getUsername).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


}
