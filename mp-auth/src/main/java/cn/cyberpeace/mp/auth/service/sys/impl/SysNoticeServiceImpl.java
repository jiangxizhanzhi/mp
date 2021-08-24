package cn.cyberpeace.mp.auth.service.sys.impl;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.SysNoticeOrgModel;
import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.auth.model.SysNoticeReadModel;
import cn.cyberpeace.mp.auth.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.SysNoticeOrgMapper;
import cn.cyberpeace.mp.auth.repository.SysNoticeMapper;
import cn.cyberpeace.mp.auth.repository.SysNoticeReadMapper;
import cn.cyberpeace.mp.auth.repository.SysNoticeRoleMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.sys.SysNoticeService;
import cn.cyberpeace.mp.auth.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjResult;
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
    private SysNoticeOrgMapper noticeOrgMapper;

    @Autowired
    private SysNoticeRoleMapper noticeRoleMapper;

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    public SysNoticeServiceImpl(SysNoticeMapper modelMapper, SysNoticeFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }


    @Override
    public int unReadCount() {
        HumanCondition condition = new HumanCondition();
        condition.distinct();
        int noticeCnt = getModelMapper().total(condition);

        int readedOrUnpushCnt = noticeReadMapper.countIsReadedOrIsUnPush();
        int unreadCnt = noticeCnt - readedOrUnpushCnt;
        return unreadCnt <= 0 ? 0 : unreadCnt;
    }

    @Override
    public void unpush(String noticeId) {
        UserModel userModel = UserUtils.getLoginUser();
        Assert.notNull(userModel, "未获取到当前登录用户信息");

        SysNoticeModel noticeModel = getModelMapper().get(noticeId);
        Assert.notNull(noticeModel, "公告不存在！ID:{}", noticeId);

        SysNoticeReadModel noticeReadModel = noticeReadMapper.selectByUserIdAndNoticeId(userModel.getId(), noticeId);

        if (Objects.isNull(noticeReadModel)) {
            // 创建一个
            SysNoticeReadModel noticeReadModelNew = SysNoticeReadModel.builder().noticeId(noticeId).user(userModel.getId()).isReaded(0).readCount(0).isNeedPush(0).build();
            noticeReadMapper.create(noticeReadModelNew);
        } else {
            // increase read cound
            SysNoticeReadModel noticeReadModelUpdate = new SysNoticeReadModel();
            noticeReadModelUpdate.setId(noticeReadModel.getId());
            noticeReadModelUpdate.setIsNeedPush(0);
            noticeReadMapper.update(noticeReadModelUpdate);
        }
    }


    @Override
    public void read(String noticeId) {
        UserModel userModel = UserUtils.getLoginUser();
        Assert.notNull(userModel, "未获取到当前登录用户信息");

        SysNoticeModel noticeModel = getModelMapper().get(noticeId);
        Assert.notNull(noticeModel, "公告不存在！ID:{}", noticeId);

        SysNoticeReadModel noticeReadModel = noticeReadMapper.selectByUserIdAndNoticeId(userModel.getId(), noticeId);

        if (Objects.isNull(noticeReadModel)) {
            // 创建一个
            SysNoticeReadModel noticeReadModelNew = SysNoticeReadModel.builder().noticeId(noticeId).user(userModel.getId()).isReaded(1).readCount(1).isNeedPush(1).build();
            noticeReadMapper.create(noticeReadModelNew);
        } else {
            // increase read cound
            noticeReadMapper.increaseReadCount(noticeReadModel.getId());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysNoticeModel add(SysNoticeRequest sysNoticeRequest) {
        Assert.notNull(sysNoticeRequest, "公告新增请求不能为空！");
        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        if (StringUtils.isBlank(noticeModel.getPublisher())) {
            UserModel currentUser = UserUtils.getLoginUser();
            noticeModel.setPublisher(currentUser.getId());
        }

        ObjServiceResult<SysNoticeModel> result = create(noticeModel);
        ResultUtils.throwExceptionIfError(result, "保存公告失败！");
        SysNoticeModel resultModel = result.getSource().getObj();
        // 组织
        List<SysNoticeOrgModel> noticeGroupModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(orgId -> {
                SysNoticeOrgModel noticeGroupModel = new SysNoticeOrgModel();
                noticeGroupModel.setOrg(orgId);
                noticeGroupModel.setNoticeId(resultModel.getId());
                noticeGroupModelList.add(noticeGroupModel);
            });
        }
        noticeOrgMapper.bulkCreate(noticeGroupModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
            List<SysNoticeRoleModel> noticeRoleModelList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(sysNoticeRequest.getRoleIds())) {
                sysNoticeRequest.getRoleIds().forEach(roleId -> {
                    SysNoticeRoleModel noticeRoleModel = new SysNoticeRoleModel();
                    noticeRoleModel.setRole(roleId);
                    noticeRoleModel.setNoticeId(resultModel.getId());
                    noticeRoleModelList.add(noticeRoleModel);
                });
            }
            noticeRoleMapper.bulkCreate(noticeRoleModelList);
        }


        return resultModel;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysNoticeModel modify(String id, SysNoticeRequest sysNoticeRequest) {
        Assert.notNull(sysNoticeRequest, "公告更新请求不能为空！");

        ObjServiceResult<SysNoticeModel> noticeModelResult = get(id);
        ResultUtils.throwExceptionIfError(noticeModelResult, "ID:" + id + "对应的公告信息不存在！");

        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        ObjServiceResult<SysNoticeModel> result = update(noticeModel);
        ResultUtils.throwExceptionIfError(result, "更新公告失败！");
        SysNoticeModel resultModel = result.getSource().getObj();

        // 清除之前的绑定关系
        noticeOrgMapper.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));
        noticeRoleMapper.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));

        // 组织
        List<SysNoticeOrgModel> noticeGroupModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(orgId -> {
                SysNoticeOrgModel noticeGroupModel = new SysNoticeOrgModel();
                noticeGroupModel.setOrg(orgId);
                noticeGroupModel.setNoticeId(resultModel.getId());
                noticeGroupModelList.add(noticeGroupModel);

            });
        }
        noticeOrgMapper.bulkCreate(noticeGroupModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
            List<SysNoticeRoleModel> noticeRoleModelList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(sysNoticeRequest.getRoleIds())) {
                sysNoticeRequest.getRoleIds().forEach(roleId -> {
                    SysNoticeRoleModel noticeRoleModel = new SysNoticeRoleModel();
                    noticeRoleModel.setRole(roleId);
                    noticeRoleModel.setNoticeId(resultModel.getId());
                    noticeRoleModelList.add(noticeRoleModel);
                });
            }
            noticeRoleMapper.bulkCreate(noticeRoleModelList);
        }

        return resultModel;
    }

    @Override
    public PageInfo<SysNoticeModel> page(SysNoticePageRequest req) {
        List<String> ids = null;
        if (Objects.nonNull(req.getIds()) && req.getIds().length > 0) {
            ids = Arrays.asList(req.getIds());
        }

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<SysNoticeModel> notices = getModelMapper().listNotices(req.getTitle(), ids, req.getOrgIds(), req.getRoleIds(), null, null);
        PageInfo pageInfo = new PageInfo(notices);
        convertPageInfo(pageInfo);
        return pageInfo;
    }

    @Override
    public PageInfo<SysNoticeModel> currentUserPage(SysNoticePageRequest req) {
        UserModel userModel = UserUtils.getLoginUser();
        if(Objects.isNull(userModel)) {
            return new PageInfo<>();
        }

        // 查找当前用户所有的角色
        // 是否是管理员，管理员拥有所有公告权限
        List<RoleModel> roleModels = userRoleService.listByUserId(userModel.getId());
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getIdentifier().equals(AuthConstant.ROLE_ADMIN));

        if (isAdmin) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
            List<SysNoticeModel> notices = getModelMapper().listCurrentUserNotices(null, null, null, null,null, null, userModel.getId());
            PageInfo pageInfo = new PageInfo(notices);
            convertPageInfo(pageInfo);
            return pageInfo;
        }

        // 非管理员...
        List<String> allRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        List<String> allOrgIds = new ArrayList<>();
        List<OrgModel> orgModels = orgUserService.listOrgsByUserId(userModel.getId());
        orgModels.forEach(o -> {
            // 添加自己的id
            allOrgIds.add(o.getId());
            // 查找所有上级组织id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<SysNoticeModel> notices = getModelMapper().listCurrentUserNotices(null, null, allOrgIds, allRoleIds,null, null, userModel.getId());
        PageInfo pageInfo = new PageInfo(notices);

        convertPageInfo(pageInfo);
        return pageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        ServiceResult result = ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
        if (Objects.nonNull(ids) && ids.length > 0) {
            result = super.bulkDelete(ids);

            noticeOrgMapper.deleteByNoticeIds(CollectionUtil.toList(ids));
            noticeRoleMapper.deleteByNoticeIds(CollectionUtil.toList(ids));
        }

        return result;
    }


    @Override
    public SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel) {
        if (Objects.isNull(noticeModel)) {
            return null;
        }

        SysNoticeDto noticeDto = new SysNoticeDto();
        BeanUtils.copyProperties(noticeModel, noticeDto);

        // username
        if (StringUtils.isNotBlank(noticeModel.getPublisher())) {
            ObjServiceResult<UserModel> userResult = userService.get(noticeDto.getPublisher());
            if (Objects.nonNull(userResult.getSource())) {
                noticeDto.setPublisherUserName(userResult.getSource().getObj().getUsername());
            }
        }

        // org
        List<OrgModel> orgModels = noticeOrgMapper.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        if (CollectionUtil.isNotEmpty(orgModels)) {
            noticeDto.setOrgIds(orgModels.stream().map(OrgModel::getId).collect(Collectors.toList()));
            noticeDto.setOrgPaths(orgModels.stream().map(OrgModel::getPath).collect(Collectors.joining(",")));
        }


        // role
        List<RoleModel> roleModels = noticeRoleMapper.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        if (CollectionUtil.isNotEmpty(roleModels)) {
            noticeDto.setRoleIds(roleModels.stream().map(RoleModel::getId).collect(Collectors.toList()));
            noticeDto.setRoleNames(roleModels.stream().map(RoleModel::getName).collect(Collectors.joining(",")));
        }

        return noticeDto;
    }

    private void convertPageInfo(PageInfo pageInfo) {
        List<SysNoticeModel> noticeModels = pageInfo.getList();

        List<SysNoticeDto> noticeDtos = noticeModels.stream().map(n -> {
            SysNoticeDto noticeDto = noticeModel2NoticeDto(n);
            return noticeDto;
        }).collect(Collectors.toList());
//
        pageInfo.setList(noticeDtos);
    }


}
