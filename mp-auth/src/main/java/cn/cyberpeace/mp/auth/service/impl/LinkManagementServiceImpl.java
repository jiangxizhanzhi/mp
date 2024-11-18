package cn.cyberpeace.mp.auth.service.impl;


import cn.cyberpeace.mp.auth.model.LinkManagementModel;
import cn.cyberpeace.mp.auth.repository.LinkManagementMapper;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequest;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequestPage;
import cn.cyberpeace.mp.auth.service.LinkManagementService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class LinkManagementServiceImpl implements LinkManagementService {
    @Autowired
    private LinkManagementMapper linkManagementMapper;


    @Override
    public ListObjResult getPageLink(LinkManagementRequestPage linkManagementRequest) {
        if (linkManagementRequest.getTeamName()!=null||linkManagementRequest.getName()!=null) {
            HumanCondition humanCondition = new HumanCondition().orderBy(LinkManagementModel::getCreateTime)
                    .pagination(linkManagementRequest.getPage(), linkManagementRequest.getPageSize())
                    .filterEqual(LinkManagementModel::getRoomId, linkManagementRequest.getRoomId())
                    .filterContains(LinkManagementModel::getName, linkManagementRequest.getName())
                    .filterContains(LinkManagementModel::getTeamName, linkManagementRequest.getTeamName());
            int total = linkManagementMapper.total(humanCondition);
            List<LinkManagementModel> models = linkManagementMapper.list(humanCondition);
            return new ListObjResult(models, total).asResult();
        }else {
            HumanCondition humanCondition = new HumanCondition().orderBy(LinkManagementModel::getCreateTime)
                .pagination(linkManagementRequest.getPage(), linkManagementRequest.getPageSize());
            int total = linkManagementMapper.total(humanCondition);
            List<LinkManagementModel> models = linkManagementMapper.list(humanCondition);
            return new ListObjResult(models, total).asResult();
        }
    }

    @Override
    public LinkManagementModel getLink(LinkManagementRequest linkManagementRequest) {
        if (linkManagementRequest.getId()==null) {
            throw new RuntimeException("查询链路管理信息缺少id");
        }
        return linkManagementMapper.get(linkManagementRequest.getId());
    }

    @Override
    public Boolean addLink(LinkManagementRequest linkManagementRequest) {
        checkName(linkManagementRequest);
        LinkManagementModel linkManagementModel = new LinkManagementModel();
        BeanUtils.copyProperties(linkManagementRequest, linkManagementModel);
        return linkManagementMapper.create(linkManagementModel);
    }

    @Override
    public Boolean patchLink(LinkManagementRequest linkManagementRequest) {
        checkName(linkManagementRequest);
        LinkManagementModel linkManagementModel = new LinkManagementModel();
        BeanUtils.copyProperties(linkManagementRequest, linkManagementModel);
        return linkManagementMapper.update(linkManagementModel);
    }

    @Override
    public ServiceResult<Integer> deleteLink(String[] ids) {

        int result;
        try {
            result = linkManagementMapper.bulkDelete(ids).getSuccessCount();
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

    private void checkName(LinkManagementRequest linkManagementRequest) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(LinkManagementModel::getName, linkManagementRequest.getName());
        List<LinkManagementModel> list = linkManagementMapper.list(humanCondition);
        if ((!CollectionUtils.isEmpty(list))&&linkManagementRequest.getId()!=null) {
            for (LinkManagementModel linkManagementModel : list) {
                if (!linkManagementModel.getId().equals(linkManagementRequest.getId())) {
                    throw new MpRuntimeException("链路名称重复");
                }
            }
        }
        if ((!CollectionUtils.isEmpty(list))&&linkManagementRequest.getId()==null){
            throw new MpRuntimeException("链路名称重复");
        }
    }


}
