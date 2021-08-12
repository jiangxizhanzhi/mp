package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysNoticeGroupModel;
import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.SysNoticeGroupMapper;
import cn.cyberpeace.mp.auth.repository.SysNoticeMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.SysNoticeService;
import cn.cyberpeace.mp.auth.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysNoticeServiceImpl extends BaseModelServiceImpl<SysNoticeModel, SysNoticeMapper, SysNoticeFields, QueryParams>
        implements SysNoticeService {

    @Autowired
    private SysNoticeGroupMapper noticeGroupMapper;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private OrgService orgService;

    @Autowired
    public SysNoticeServiceImpl(SysNoticeMapper modelMapper, SysNoticeFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysNoticeModel add(SysNoticeRequest sysNoticeRequest) {
        Assert.notNull(sysNoticeRequest, "公告新增请求不能为空！");
        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        ObjServiceResult<SysNoticeModel> result = create(noticeModel);
        ResultUtils.throwExceptionIfError(result, "保存公告失败！");

        SysNoticeModel resultModel = result.getSource().getObj();
        List<SysNoticeGroupModel> noticeGroupModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(id -> {
                SysNoticeGroupModel noticeGroupModel = new SysNoticeGroupModel();
                noticeGroupModel.setOwner(id);
                noticeGroupModel.setNoticeId(resultModel.getId());
                noticeGroupModelList.add(noticeGroupModel);
            });
        }
        noticeGroupMapper.bulkCreate(noticeGroupModelList);

        // todo 群组？

        return resultModel;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysNoticeModel modify(SysNoticeRequest sysNoticeRequest) {
        Assert.notNull(sysNoticeRequest, "公告更新请求不能为空！");

        ObjServiceResult<SysNoticeModel> noticeModelResult = get(sysNoticeRequest.getId());
        ResultUtils.throwExceptionIfError(noticeModelResult, "ID:" + sysNoticeRequest.getId() + "对应的公告信息不存在！");

        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        ObjServiceResult<SysNoticeModel> result = update(noticeModel);
        ResultUtils.throwExceptionIfError(result, "更新公告失败！");
        SysNoticeModel resultModel = result.getSource().getObj();

        // 清除之前的绑定关系
        noticeGroupMapper.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));

        List<SysNoticeGroupModel> noticeGroupModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(id -> {
                SysNoticeGroupModel noticeGroupModel = new SysNoticeGroupModel();
                noticeGroupModel.setOwner(id);
                noticeGroupModel.setNoticeId(resultModel.getId());
                noticeGroupModelList.add(noticeGroupModel);
            });
        }

        noticeGroupMapper.bulkCreate(noticeGroupModelList);

        // todo 群组？

        return resultModel;
    }

    @Override
    public PageInfo<SysNoticeModel> page(SysNoticePageRequest req) {
        List<String> ids = null;
        if (Objects.nonNull(req.getIds()) && req.getIds().length > 0) {
            ids = Arrays.asList(req.getIds());
        }

        List<String> orgs = null;
        if (StringUtils.isNotBlank(req.getOrg())) {
            orgs = Collections.singletonList(req.getOrg());
        }

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<SysNoticeModel> notices = getModelMapper().listNotices(req.getTitle(), ids, orgs, null, null);
        PageInfo pageInfo = new PageInfo(notices);

        return pageInfo;
    }

    @Override
    public PageInfo<SysNoticeModel> currentUserPage(SysNoticePageRequest req) {
        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        UserModel userModel = UserUtils.getLoginUser();
        if(Objects.isNull(userModel)) {
            return new PageInfo<>();
        }
        List<OrgModel> orgModels = orgUserService.listOrgsByUserId(userModel.getId());
        List<String> allOrgIds = new ArrayList<>();
        orgModels.forEach(o -> {
            allOrgIds.add(o.getId()); // 添加自己的id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<SysNoticeModel> notices = getModelMapper().listNotices(null, null, allOrgIds, null, null);
        PageInfo pageInfo = new PageInfo(notices);

        return pageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        ServiceResult result = ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
        if (Objects.nonNull(ids) && ids.length > 0) {
            result = super.bulkDelete(ids);

            noticeGroupMapper.deleteByNoticeIds(CollectionUtil.toList(ids));
        }
        // todo 群组

        return result;
    }


}
