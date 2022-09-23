package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.org.OrgBO;
import cn.cyberpeace.mp.auth.dto.org.OrgDto;
import cn.cyberpeace.mp.auth.dto.user.UserSimpleDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.enums.NoticeTopTypeEnum;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeModel;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeMapper;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeOrgMapper;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeReadMapper;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeRoleMapper;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeUserMapper;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeUserService;
import cn.cyberpeace.mp.frontadmin.util.SQLUtils;
import cn.hutool.core.collection.CollectionUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class SysNoticeServiceImpl extends BaseModelServiceImpl<SysNoticeModel, SysNoticeMapper, SysNoticeFields, QueryParams>
        implements SysNoticeService {

    @Autowired
    private SysNoticeOrgMapper noticeOrgMapper;

    @Autowired
    private SysNoticeUserMapper noticeUserMapper;

    @Autowired
    private SysNoticeUserService sysNoticeUserService;

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
        int totalCnt = getCurrentUserNoticeMessagesUnreadCount();
        return totalCnt;
    }

    @Override
    public void unpush(List<String> noticeIds) {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.isNull(userModel)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
        }

        for (String noticeId : noticeIds) {

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

    }


    @Override
    public void read(String noticeId) {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.isNull(userModel)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
        }

        SysNoticeModel noticeModel = getModelMapper().get(noticeId);
        if (Objects.isNull(noticeModel)) {
            throw new MpRuntimeException(AdminErrorCode.SYS_NOTICE_NOT_EXISTED.format(noticeId));
        }

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
    public void top(String noticeId, Integer type) {
        SysNoticeModel noticeModel = getModelMapper().get(noticeId);
        if (Objects.isNull(noticeModel)) {
            throw new MpRuntimeException(AdminErrorCode.SYS_NOTICE_NOT_EXISTED.format(noticeId));
        }

        if (Objects.isNull(type)) {
            type = 0;
        }

        SysNoticeModel newModel = new SysNoticeModel();
        newModel.setId(noticeModel.getId());
        if (NoticeTopTypeEnum.TOP.getCode().equals(type)) {
            newModel.setTop(System.currentTimeMillis());
        } else if (NoticeTopTypeEnum.UNTOP.getCode().equals(type)) {
            newModel.setTop(0L);
        } else {
            log.error("unknow type:{}", type);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        getModelMapper().update(newModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysNoticeModel add(SysNoticeRequest sysNoticeRequest) {
        ParamCheck.notNull(sysNoticeRequest, AuthBasei18nCode.REQUEST_PARAMS);
        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        if (StringUtils.isBlank(noticeModel.getPublisher())) {
            UserModel currentUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentUser)) {
                noticeModel.setPublisher(currentUser.getId());
            }
        }

        ObjServiceResult<SysNoticeModel> result = create(noticeModel);
        if (Objects.isNull(result.getSource())) {
            log.error("save notice error!");
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
        SysNoticeModel resultModel = result.getSource().getObj();
        // 组织
        List<SysNoticeOrgModel> noticeOrgModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(orgId -> {
                SysNoticeOrgModel sysNoticeOrgModel = new SysNoticeOrgModel();
                sysNoticeOrgModel.setOrg(orgId);
                sysNoticeOrgModel.setNoticeId(resultModel.getId());
                noticeOrgModelList.add(sysNoticeOrgModel);
            });
        }
        noticeOrgMapper.bulkCreate(noticeOrgModelList);


        // 用户
        List<SysNoticeUserModel> noticeUserModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getUserIds())) {
            sysNoticeRequest.getUserIds().forEach(userId -> {
                SysNoticeUserModel sysNoticeUserModel = new SysNoticeUserModel();
                sysNoticeUserModel.setUser(userId);
                sysNoticeUserModel.setNoticeId(resultModel.getId());
                noticeUserModelList.add(sysNoticeUserModel);
            });
        }
        noticeUserMapper.bulkCreate(noticeUserModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (Objects.nonNull(sysNoticeRequest.getIsPublic()) && sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
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
        ParamCheck.notNull(sysNoticeRequest, AuthBasei18nCode.REQUEST_PARAMS);

        ObjServiceResult<SysNoticeModel> noticeModelResult = get(id);
        if (Objects.isNull(noticeModelResult.getSource())) {
            throw new MpRuntimeException(AdminErrorCode.SYS_NOTICE_NOT_EXISTED.format(id));
        }


        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);
        noticeModel.setId(id);

        ObjServiceResult<SysNoticeModel> result = update(noticeModel);
        if (Objects.isNull(noticeModelResult.getSource())) {
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
        SysNoticeModel resultModel = result.getSource().getObj();

        // 清除之前的绑定关系
        noticeOrgMapper.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));
        sysNoticeUserService.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));
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

        // 用户
        List<SysNoticeUserModel> noticeUserModelList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysNoticeRequest.getUserIds())) {
            sysNoticeRequest.getUserIds().forEach(userId -> {
                SysNoticeUserModel sysNoticeUserModel = new SysNoticeUserModel();
                sysNoticeUserModel.setUser(userId);
                sysNoticeUserModel.setNoticeId(resultModel.getId());
                noticeUserModelList.add(sysNoticeUserModel);
            });
        }
        noticeUserMapper.bulkCreate(noticeUserModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (Objects.nonNull(sysNoticeRequest.getIsPublic()) && sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
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
        String likeTitle = SQLUtils.escapeLike(req.getTitle());
        List<SysNoticeModel> notices = getModelMapper().listNotices(likeTitle, ids, req.getOrgIds(), req.getRoleIds(), null, null);
        PageInfo pageInfo = new PageInfo(notices);
        convert2NoticeDtoPageInfo(pageInfo, false);
        return pageInfo;
    }

    @Override
    public PageInfo<SysNoticeModel> getCurrentUserNoticeMessagesPage(SysNoticePageRequest req) {
        UserModel currentUser = UserUtils.getLoginUser();
        if (Objects.isNull(currentUser)) {
            log.error("user not login!");
            return new PageInfo<>();
        }

        // 查找当前用户所有的角色
        // 是否是管理员，管理员拥有所有公告权限
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getId().equals(AuthConstant.ROLE_ADMIN_ID));

        // 管理员，查看所有公告消息
        if (isAdmin) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
            List<SysNoticeModel> notices = getModelMapper().listNoticeMessageByCurrentAdminUser(currentUser.getId());
            PageInfo pageInfo = new PageInfo(notices);
            convert2NoticeDtoPageInfo(pageInfo, true);
            return pageInfo;
        }

        // 非管理员...
        List<String> allRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = orgUserService.listOrgsByUserId(currentUser.getId());
        orgBOS.forEach(o -> {
            // 添加自己的id
            allOrgIds.add(o.getId());
            // 查找所有上级组织id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });


        /*
        1. 用户没有角色，没有组织
        (is_public = 1) and (notice_org.org is null)

        2. 用户有角色，没有组织
        (is_public = 1 or notice_role in roles) and (notice_org.org is null)

        3. 用户没有角色，有组织
        (is_public = 1) and (notice_org.org is null or notice_org.org in orgs)

        4. 用户有角色，有组织
        (is_public = 1 or notice_role in roles) and (notice_org.org is null or notice_org.org in orgs)
        */
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<SysNoticeModel> notices = getModelMapper().listNoticeMessageByCurrentNormalUser(Arrays.asList(currentUser.getId()), allOrgIds, allRoleIds, currentUser.getId());
        PageInfo pageInfo = new PageInfo(notices);

        convert2NoticeDtoPageInfo(pageInfo, true);
        return pageInfo;
    }

    @Override
    public int getCurrentUserNoticeMessagesUnreadCount() {
        UserModel currentUser = UserUtils.getLoginUser();
        if (Objects.isNull(currentUser)) {
            log.error("user not login!");
            return 0;
        }

        // 查找当前用户所有的角色
        // 是否是管理员，管理员拥有所有公告权限
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getId().equals(AuthConstant.ROLE_ADMIN_ID));

        // 管理员，查看所有公告消息
        if (isAdmin) {
            int totalCnt = getModelMapper().getUnreadCountByCurrentAdminUser(currentUser.getId());
            return totalCnt;
        }

        // 非管理员...
        List<String> allRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = orgUserService.listOrgsByUserId(currentUser.getId());
        orgBOS.forEach(o -> {
            // 添加自己的id
            allOrgIds.add(o.getId());
            // 查找所有上级组织id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });


        /*
        1. 用户没有角色，没有组织
        (is_public = 1) and (notice_org.org is null)

        2. 用户有角色，没有组织
        (is_public = 1 or notice_role in roles) and (notice_org.org is null)

        3. 用户没有角色，有组织
        (is_public = 1) and (notice_org.org is null or notice_org.org in orgs)

        4. 用户有角色，有组织
        (is_public = 1 or notice_role in roles) and (notice_org.org is null or notice_org.org in orgs)
        */
        int totalCnt = getModelMapper().getUnreadCountByCurrentNormalUser(Arrays.asList(currentUser.getId()), allOrgIds, allRoleIds, currentUser.getId());
        return totalCnt;
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
    public SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel, boolean checkCurrentUserIsRead) {
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
            List<OrgBO> orgBOS = orgModels.stream().map(orgModel -> orgService.orgModel2OrgBO(orgModel)).collect(Collectors.toList());
            noticeDto.setOrgPaths(orgBOS.stream().map(OrgBO::getPath).collect(Collectors.joining(",")));
        }

        // user
        List<UserModel> userModels = noticeUserMapper.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        userModels.stream().forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));

        if (CollectionUtil.isNotEmpty(userModels)) {
            List<UserSimpleDto> userSimpleDtos = userModels.stream().map(userModel -> {
                UserSimpleDto userSimpleDto = new UserSimpleDto();
                BeanUtils.copyProperties(userModel, userSimpleDto);
                return userSimpleDto;
            }).collect(Collectors.toList());
            noticeDto.setUsers(userSimpleDtos);
        }

        // role
        List<RoleModel> roleModels = noticeRoleMapper.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        if (CollectionUtil.isNotEmpty(roleModels)) {
            noticeDto.setRoleIds(roleModels.stream().map(RoleModel::getId).collect(Collectors.toList()));
            noticeDto.setRoleNames(roleModels.stream().map(RoleModel::getName).collect(Collectors.joining(",")));
        }

        // 检查当前用户对此条公告是否已读
        if (checkCurrentUserIsRead) {
            UserModel userModel = UserUtils.getLoginUser();
            if (Objects.nonNull(userModel)) {
                String currentUserId = userModel.getId();
                SysNoticeReadModel noticeReadModel = noticeReadMapper.selectByUserIdAndNoticeId(currentUserId, noticeModel.getId());
                if (Objects.isNull(noticeReadModel)) {
                    // 未读
                    noticeDto.setIsRead(0);
                } else {
                    // 已读
                    noticeDto.setIsRead(1);
                }
            }
        }

        return noticeDto;
    }

    private void convert2NoticeDtoPageInfo(PageInfo pageInfo, boolean checkCurrentUserIsRead) {
        List<SysNoticeModel> noticeModels = pageInfo.getList();

        List<SysNoticeDto> noticeDtos = noticeModels.stream().map(n -> {
            SysNoticeDto noticeDto = noticeModel2NoticeDto(n, checkCurrentUserIsRead);
            return noticeDto;
        }).collect(Collectors.toList());
//
        pageInfo.setList(noticeDtos);
    }


}
