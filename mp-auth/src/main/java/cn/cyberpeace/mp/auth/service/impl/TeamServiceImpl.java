package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.repository.TeamMapper;
import cn.cyberpeace.mp.auth.request.team.TeamPageRequest;
import cn.cyberpeace.mp.auth.request.team.TeamRequest;
import cn.cyberpeace.mp.auth.service.TeamService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

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
            throw new MpRuntimeException(BaseErrorCode.ERROR, "添加战队失败,缺少战队名称");
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
                    throw new MpRuntimeException(BaseErrorCode.ERROR,"战队名称重复");
                }
            }
        }
        if ((!CollectionUtils.isEmpty(list))&&teamRequest.getId()==null){
            throw new MpRuntimeException(BaseErrorCode.ERROR,"战队名称重复");
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
            throw new MpRuntimeException(BaseErrorCode.ERROR, "战队查询缺少id");
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


}
