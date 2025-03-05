package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.Personnel.management.PersonnelManagementBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportPersonnelManagementBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportTeamBO;
import cn.cyberpeace.mp.auth.bo.user.TeamInfoIdAndName;
import cn.cyberpeace.mp.auth.bo.user.TeamInfoResult;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
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
import cn.cyberpeace.mp.base.service.code.ErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.IdGenerator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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

        String url = "http://10.110.3.1:8377/api/ct/admin/nbi/teams/";
        ServiceResult ctTeamInfo = getCtTeamInfo(url);
        JSONArray data = (JSONArray) ctTeamInfo.getData();
        List<TeamModel> teamModel = Lists.newArrayList();
        for (Object datum : data) {
            JSONObject jsonObject = (JSONObject) datum;
            TeamModel model = new TeamModel();
            model.setLogo(jsonObject.get("team_logo").toString());
            model.setName(jsonObject.get("team_name").toString());
            model.setDescription(jsonObject.get("team_desc").toString());
            model.setId(jsonObject.get("team_id").toString());
            model.setTag(jsonObject.get("team_tag").toString());
            teamModel.add(model);
        }
        return new ListObjResult(teamModel,teamModel.size()).asResult();
    }

    /**
     *添加
     * @param teamRequest
     * @return
     */
    @Override
    public TeamModel addTeam(TeamRequest teamRequest) {
        if (teamRequest.getName().isEmpty()) {
            throw new MpRuntimeException(AuthErrorCode.MISSING_CLAN_NAME);
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
                    throw new MpRuntimeException(AuthErrorCode.CLAN_NAME_DUPLICATION);
                }
            }
        }
        if ((!CollectionUtils.isEmpty(list))&&teamRequest.getId()==null){
            throw new MpRuntimeException(AuthErrorCode.CLAN_NAME_DUPLICATION);
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
            throw new MpRuntimeException( AuthErrorCode.CLAN_QUERY_MISSING_PARAMETERS);
        }
        String url = "http://10.110.3.1:8377/api/ct/admin/nbi/team/?team_id=" + teamRequest.getId();
        ServiceResult serviceResult = getCtTeamInfo(url);
        TeamModel teamModel = new TeamModel();
        TeamInfoResult teamInfoResult = JSON.parseObject(serviceResult.getData().toString(), TeamInfoResult.class);
        if (!ObjectUtils.isEmpty(teamInfoResult)) {
            teamModel.setName(teamInfoResult.getTeamName());
            teamModel.setId(teamInfoResult.getTeamId());
            teamModel.setDescription(teamInfoResult.getTeamDesc());
            teamModel.setLogo(teamInfoResult.getTeamLogo());
        }

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
            throw new MpRuntimeException(AuthErrorCode.UPLOAD_EXCESS_NUMBER);
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
                throw new MpRuntimeException(AuthErrorCode.CLAN_NAME_DUPLICATION);
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
            throw new MpRuntimeException(AuthErrorCode.UPLOAD_EXCESS_NUMBER);
        }
        //1,排查用户加入不同战队
        List<String> usernameList = exportPersonnelManagementBos.stream().map(ExportPersonnelManagementBo::getUsername).distinct().collect(Collectors.toList());
        List<PersonnelManagementModel> personnelManagementModelList = personnelManagementMapper.selectALl();
        List<String> selectUserName = personnelManagementModelList.stream().map(PersonnelManagementModel::getUsername).collect(Collectors.toList());
        usernameList.addAll(selectUserName);
        if (usernameList.size() != exportPersonnelManagementBos.size()){
            throw new MpRuntimeException(AuthErrorCode.USERS_CANNOT_ADD_TWO_TEAMS);
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
            throw new MpRuntimeException(AuthErrorCode.TEAM_NAME_DOES_NOT_EXIST);
        }
        //3,用户名称不存在直接抛异常
        List<String> userNameList = exportPersonnelManagementBos.stream().map(ExportPersonnelManagementBo::getUsername).collect(Collectors.toList());
        List<String> checkUserNameExistList = checkUserNameExist(userNameList);
        if (userNameList.size()!=checkUserNameExistList.size()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_USERNAME_EXISTED);
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
        String teamId = list.get(0);
        String url = "http://10.110.3.1:8377/api/ct/admin/nbi/team_users/?team_id=" + teamId;
        List<TeamIdAndUserInfo> teamIdAndUserInfos = Lists.newArrayList();

        ServiceResult serviceResult = getCtTeamInfo(url);

        //获取数据
        String data = serviceResult.getData().toString();
        //收集用户id
        List<String>userIds = Lists.newArrayList();
        JSONArray jsonData = JSONArray.parseArray(data);

        for (Object jsonDatum : jsonData) {
            JSONObject jsonObject = (JSONObject) jsonDatum;
            String userId = (String) jsonObject.get("id");
            userIds.add(userId);
        }
        List<UserBo> userBos = tenantUserService.listByUserIds(userIds);
        TeamIdAndUserInfo teamIdAndUserInfo = new TeamIdAndUserInfo();
        teamIdAndUserInfo.setTeamId(teamId);
        teamIdAndUserInfo.setUserBos(userBos);
        teamIdAndUserInfos.add(teamIdAndUserInfo);

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
        throw new MpRuntimeException(AuthErrorCode.PEOPLE_NOT_JOIN_TEAM);
    }

    @Override
    public TeamInfoIdAndName getTeamInfoByUser(String userId) {
        String url = "http://10.110.3.1:8377/api/ct/admin/nbi/user_team/?user_id=" + userId;
        ServiceResult ctTeamInfo = getCtTeamInfo(url);
        JSONObject data = (JSONObject) ctTeamInfo.getData();
        TeamInfoIdAndName teamInfoIdAndName = new TeamInfoIdAndName();
        teamInfoIdAndName.setId((String) data.get("team_id"));
        teamInfoIdAndName.setName((String) data.get("team_name"));
        return teamInfoIdAndName;
    }

    @Override
    public String getTeamInfo(String teamId) {
        String url = "http://10.110.3.1:8377/api/ct/admin/nbi/team/?team_id=" + teamId;
        String teamName = null;
        //获取数据
        ServiceResult ctTeamInfo = getCtTeamInfo(url);
        JSONObject data = (JSONObject) ctTeamInfo.getData();
        teamName = (String) data.get("team_name");
        return teamName;
    }

    private static ServiceResult getCtTeamInfo(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            // 设置请求头
            request.addHeader("accept", "application/json");
            request.addHeader("CLIENTID", "84261d495c414e");
            request.addHeader("CLIENTSECRET", "2112f4b855264ad7b1a4209737fc7836");

            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            ServiceResult serviceResult = JSON.parseObject(responseBody, ServiceResult.class);
            return serviceResult;
        } catch (Exception e) {
            throw new MpRuntimeException(AuthErrorCode.GET_CT_TEAM,e.getMessage());
        }
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
