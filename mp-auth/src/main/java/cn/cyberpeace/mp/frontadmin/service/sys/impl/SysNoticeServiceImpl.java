package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.bo.user.UserSimpleBo;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.enums.NoticeTopTypeEnum;
import cn.cyberpeace.mp.frontadmin.model.*;
import cn.cyberpeace.mp.frontadmin.repository.*;
import cn.cyberpeace.mp.frontadmin.request.notice.DelayNoticeRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeReadService;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeRoleService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeOrgService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeUserService;
import cn.cyberpeace.mp.frontadmin.util.SQLUtils;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private OrgService orgService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SysNoticeOrgService sysNoticeOrgService;

    @Autowired
    private SysNoticeReadService sysNoticeReadService;

    @Autowired
    private SysNoticeRoleService sysNoticeRoleService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysActivationService activationService;

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

            SysNoticeReadModel noticeReadModel = sysNoticeReadService.selectByUserIdAndNoticeId(userModel.getId(), noticeId);

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

        SysNoticeReadModel noticeReadModel = sysNoticeReadService.selectByUserIdAndNoticeId(userModel.getId(), noticeId);

        if (Objects.isNull(noticeReadModel)) {
            // 创建一个
            SysNoticeReadModel noticeReadModelNew = SysNoticeReadModel.builder().noticeId(noticeId).user(userModel.getId()).isReaded(1).readCount(1).isNeedPush(1).build();
            noticeReadMapper.create(noticeReadModelNew);
        } else {
            // increase read cound
            sysNoticeReadService.increaseReadCount(noticeReadModel.getId());
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
        if (CollectionUtils.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            for (String orgId : sysNoticeRequest.getOrgIds()) {
                SysNoticeOrgModel sysNoticeOrgModel = new SysNoticeOrgModel();
                sysNoticeOrgModel.setOrg(orgId);
                sysNoticeOrgModel.setNoticeId(resultModel.getId());
                noticeOrgModelList.add(sysNoticeOrgModel);
            }
        }
        noticeOrgMapper.bulkCreate(noticeOrgModelList);


        // 用户
        List<SysNoticeUserModel> noticeUserModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysNoticeRequest.getUserIds())) {
            for (String userId : sysNoticeRequest.getUserIds()) {
                SysNoticeUserModel sysNoticeUserModel = new SysNoticeUserModel();
                sysNoticeUserModel.setUser(userId);
                sysNoticeUserModel.setNoticeId(resultModel.getId());
                noticeUserModelList.add(sysNoticeUserModel);
            }
        }
        noticeUserMapper.bulkCreate(noticeUserModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (Objects.nonNull(sysNoticeRequest.getIsPublic()) && sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
            List<SysNoticeRoleModel> noticeRoleModelList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sysNoticeRequest.getRoleIds())) {
                for (String roleId : sysNoticeRequest.getRoleIds()) {
                    SysNoticeRoleModel noticeRoleModel = new SysNoticeRoleModel();
                    noticeRoleModel.setRole(roleId);
                    noticeRoleModel.setNoticeId(resultModel.getId());
                    noticeRoleModelList.add(noticeRoleModel);
                }
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

        sysNoticeOrgService.deleteByNoticeIds(Collections.singletonList(resultModel.getId()));
        sysNoticeUserService.deleteByNoticeIds(Collections.singletonList(resultModel.getId()));
        sysNoticeRoleService.deleteByNoticeIds(Collections.singletonList(resultModel.getId()));

        // 组织
        List<SysNoticeOrgModel> noticeGroupModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            for (String orgId : sysNoticeRequest.getOrgIds()) {
                SysNoticeOrgModel noticeGroupModel = new SysNoticeOrgModel();
                noticeGroupModel.setOrg(orgId);
                noticeGroupModel.setNoticeId(resultModel.getId());
                noticeGroupModelList.add(noticeGroupModel);

            }
        }
        noticeOrgMapper.bulkCreate(noticeGroupModelList);

        // 用户
        List<SysNoticeUserModel> noticeUserModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysNoticeRequest.getUserIds())) {
            for (String userId : sysNoticeRequest.getUserIds()) {
                SysNoticeUserModel sysNoticeUserModel = new SysNoticeUserModel();
                sysNoticeUserModel.setUser(userId);
                sysNoticeUserModel.setNoticeId(resultModel.getId());
                noticeUserModelList.add(sysNoticeUserModel);
            }
        }
        noticeUserMapper.bulkCreate(noticeUserModelList);

        // 角色，如果是公开的，则忽略角色字段内的值
        if (Objects.nonNull(sysNoticeRequest.getIsPublic()) && sysNoticeRequest.getIsPublic().equals(SysNoticeModel.NOTICE_NOT_PUBLIC)) {
            List<SysNoticeRoleModel> noticeRoleModelList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sysNoticeRequest.getRoleIds())) {
                for (String roleId : sysNoticeRequest.getRoleIds()) {
                    SysNoticeRoleModel noticeRoleModel = new SysNoticeRoleModel();
                    noticeRoleModel.setRole(roleId);
                    noticeRoleModel.setNoticeId(resultModel.getId());
                    noticeRoleModelList.add(noticeRoleModel);
                }
            }
            noticeRoleMapper.bulkCreate(noticeRoleModelList);
        }

        return resultModel;
    }

    @Override
    public ServiceResult page(SysNoticePageRequest req) {
        List<String> ids = null;
        if (Objects.nonNull(req.getIds()) && req.getIds().length > 0) {
            ids = Arrays.asList(req.getIds());
        }

        String likeTitle = SQLUtils.escapeLike(req.getTitle());
        ServiceResult result = listNotices(likeTitle, ids, req.getOrgIds(), req.getRoleIds(), null, null, req.getPage(), req.getPageSize());
        return result;
    }

    @Override
    public ListObjServiceResult getCurrentUserNoticeMessagesPage(SysNoticePageRequest req) {
        UserModel currentUser = UserUtils.getLoginUser();
        if (Objects.isNull(currentUser)) {
            log.error("user not login!");
            return new ListObjServiceResult<>();
        }

        // 查找当前用户所有的角色
        // 是否是管理员，如果公告只发给指定用户且未指定管理员，管理员无阅读权限，其余均有管理员拥有所有公告权限
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = false;
        for (RoleModel r : roleModels) {
            if (r.getId().equals(AuthConstant.ROLE_ADMIN_ID)) {
                isAdmin = true;
                break;
            }
        }

        // 管理员，查看所有有阅读权限的公告消息
        if (isAdmin) {
            return listNoticeMessageByCurrentAdminUser(currentUser.getId(), req.getPage(), req.getPageSize());
        }

        // 非管理员...
        List<String> allRoleIds = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            String roleModelId = roleModel.getId();
            allRoleIds.add(roleModelId);
        }

        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = tenantUserService.listOrgsByUserId(currentUser.getId());
        for (OrgBO o : orgBOS) {// 添加自己的id
            allOrgIds.add(o.getId());
            // 查找所有上级组织id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            List<String> list = new ArrayList<>();
            for (OrgDto orgDto : orgDtos) {
                String id = orgDto.getId();
                list.add(id);
            }
            allOrgIds.addAll(list);
        }


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
        ListObjServiceResult result = listNoticeMessageByCurrentNormalUser(Arrays.asList(currentUser.getId()), allOrgIds, allRoleIds, currentUser.getId(), req.getPage(), req.getPageSize());
        return result;
    }

    @Override
    public int getCurrentUserNoticeMessagesUnreadCount() {
        UserModel currentUser = UserUtils.getLoginUser();
        if (Objects.isNull(currentUser)) {
            log.error("user not login!");
            return 0;
        }

        // 查找当前用户所有的角色
        // 是否是管理员，如果公告只发给指定用户且未指定管理员，管理员无阅读权限，其余均有管理员拥有所有公告权限
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = false;
        for (RoleModel r : roleModels) {
            if (r.getId().equals(AuthConstant.ROLE_ADMIN_ID)) {
                isAdmin = true;
                break;
            }
        }

        // 管理员，查看所有有阅读权限的公告消息
        if (isAdmin) {
            int totalCnt = getUnreadCountByCurrentAdminUser(currentUser.getId());
            return totalCnt;
        }

        // 非管理员...
        List<String> allRoleIds = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            String roleModelId = roleModel.getId();
            allRoleIds.add(roleModelId);
        }

        // 根据当前登录用户查出所有的组织，包括组织的上级，直到顶级组织
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = tenantUserService.listOrgsByUserId(currentUser.getId());
        for (OrgBO o : orgBOS) {// 添加自己的id
            allOrgIds.add(o.getId());
            // 查找所有上级组织id
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            List<String> list = new ArrayList<>();
            for (OrgDto orgDto : orgDtos) {
                String id = orgDto.getId();
                list.add(id);
            }
            allOrgIds.addAll(list);
        }


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
        int totalCnt = getUnreadCountByCurrentNormalUser(Arrays.asList(currentUser.getId()), allOrgIds, allRoleIds, currentUser.getId());
        return totalCnt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        ServiceResult result = ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
        if (Objects.nonNull(ids) && ids.length > 0) {
            result = super.bulkDelete(ids);

            sysNoticeOrgService.deleteByNoticeIds(Arrays.asList(ids));
            sysNoticeRoleService.deleteByNoticeIds(Arrays.asList(ids));
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
                noticeDto.setPublisherName(userResult.getSource().getObj().getName());
            }
        }

        // org
        List<OrgModel> orgModels = sysNoticeOrgService.listByNoticeIds(Collections.singletonList(noticeModel.getId()));
        if (CollectionUtils.isNotEmpty(orgModels)) {
            List<String> list = new ArrayList<>();
            for (OrgModel model : orgModels) {
                String id = model.getId();
                list.add(id);
            }
            noticeDto.setOrgIds(list);
            List<OrgBO> orgBOS = new ArrayList<>();
            for (OrgModel orgModel : orgModels) {
                OrgBO bo = orgService.orgModel2OrgBO(orgModel);
                orgBOS.add(bo);
            }
            StringJoiner joiner = new StringJoiner(",");
            for (OrgBO orgBO : orgBOS) {
                String path = orgBO.getPath();
                joiner.add(path);
            }
            noticeDto.setOrgPaths(joiner.toString());
        }

        // user
        List<UserModel> userModels = listByNoticeIds(Collections.singletonList(noticeModel.getId()));
        for (UserModel userModel1 : userModels) {
            userService.setDefaultIfNoAvatar(userModel1);
        }

        if (CollectionUtils.isNotEmpty(userModels)) {
            List<UserSimpleBo> userSimpleBos = new ArrayList<>();
            for (UserModel userModel : userModels) {
                UserSimpleBo userSimpleBo = getUserSimpleBo(userModel);
                userSimpleBos.add(userSimpleBo);
            }
            noticeDto.setUsers(userSimpleBos);
        }

        // role
        List<RoleModel> roleModels = sysNoticeRoleService.listByNoticeIds(Collections.singletonList(noticeModel.getId()));
        if (CollectionUtils.isNotEmpty(roleModels)) {
            List<String> list = new ArrayList<>();
            for (RoleModel model : roleModels) {
                String id = model.getId();
                list.add(id);
            }
            noticeDto.setRoleIds(list);
            StringJoiner joiner = new StringJoiner(",");
            for (RoleModel roleModel : roleModels) {
                String name = roleModel.getName();
                joiner.add(name);
            }
            noticeDto.setRoleNames(joiner.toString());
        }

        // 检查当前用户对此条公告是否已读
        if (checkCurrentUserIsRead) {
            UserModel userModel = UserUtils.getLoginUser();
            if (Objects.nonNull(userModel)) {
                String currentUserId = userModel.getId();
                SysNoticeReadModel noticeReadModel = sysNoticeReadService.selectByUserIdAndNoticeId(currentUserId, noticeModel.getId());
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

    private static UserSimpleBo getUserSimpleBo(UserModel userModel) {
        UserSimpleBo userSimpleBo = new UserSimpleBo();
        BeanUtils.copyProperties(userModel, userSimpleBo);
        return userSimpleBo;
    }

    private void convert2NoticeDtoPageInfo(PageInfo pageInfo, boolean checkCurrentUserIsRead) {
        List<SysNoticeModel> noticeModels = pageInfo.getList();

        List<SysNoticeDto> noticeDtos = new ArrayList<>();
        for (SysNoticeModel n : noticeModels) {
            SysNoticeDto sysNoticeDto = getSysNoticeDto(checkCurrentUserIsRead, n);
            noticeDtos.add(sysNoticeDto);
        }
//
        pageInfo.setList(noticeDtos);
    }

    private SysNoticeDto getSysNoticeDto(boolean checkCurrentUserIsRead, SysNoticeModel n) {
        SysNoticeDto noticeDto = noticeModel2NoticeDto(n, checkCurrentUserIsRead);
        return noticeDto;
    }

    private List<UserModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(noticeIds)) {
            humanCondition.link(UserModel.class, SysNoticeUserModel.class)
                    .filterLink(UserModel.class, UserModel::getId, SysNoticeUserModel.class, SysNoticeUserModel::getUser)
                    .currentLink(SysNoticeUserModel.class)
                    .filterIn(SysNoticeUserModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition.distinct();
        List<UserModel> userModels = userMapper.list(humanCondition);
        return userModels;
    }

    public ListObjServiceResult listNoticeMessageByCurrentAdminUser(String userId, int page, int pageSize) {
        Set<String> noticeIds = new HashSet<>();
        // 查询发给组织的noticeId
        HumanCondition orgNoticeCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        List<SysNoticeOrgModel> orgNotices = noticeOrgMapper.list(orgNoticeCond);
        for (SysNoticeOrgModel orgNotice : orgNotices) {
            noticeIds.add(orgNotice.getNoticeId());
        }
        // 查询发给角色的noticeId，不包括发给所有角色
        HumanCondition roleNoticeCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        List<SysNoticeRoleModel> roleNotices = noticeRoleMapper.list(roleNoticeCond);
        for (SysNoticeRoleModel roleNotice : roleNotices) {
            noticeIds.add(roleNotice.getNoticeId());
        }
        // 查询发给指定用户的noticeId
        HumanCondition userNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeUserModel::getUser, userId)
                .select(SysNoticeUserModel::getNoticeId);
        List<SysNoticeUserModel> userNotices = noticeUserMapper.list(userNoticeCond);
        for (SysNoticeUserModel model : userNotices) {
            noticeIds.add(model.getNoticeId());
        }

        HumanCondition readNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .select(SysNoticeReadModel::getNoticeId);
        List<SysNoticeReadModel> notPushNotices = noticeReadMapper.list(readNoticeCond);
        List<String> notPushNoticeIds = new ArrayList<>();
        for (SysNoticeReadModel notPushNotice : notPushNotices) {
            String noticeId = notPushNotice.getNoticeId();
            notPushNoticeIds.add(noticeId);
        }

        HumanCondition noticeCond = new HumanCondition();
        if (CollectionUtils.isEmpty(noticeIds)) {
            noticeCond.filterEqual(SysNoticeModel::getIsPublic, 1);
        } else {
            noticeCond.filterOrStart()
                    .orEqual(SysNoticeModel::getIsPublic, 1)
                    .orIn(SysNoticeModel::getId, noticeIds.toArray())
                    .filterOrEnd();
        }
        if (CollectionUtils.isNotEmpty(notPushNoticeIds)) {
            noticeCond.filterNotIn(SysNoticeModel::getId, notPushNoticeIds.toArray());
        }
        noticeCond.pagination(page, pageSize)
                .orderBy(SysNoticeModel::getTop, true)
                .orderBy(SysNoticeModel::getPk, true);
        int total = getModelMapper().total(noticeCond);
        List<SysNoticeModel> noticeModels = getModelMapper().list(noticeCond);
        List<SysNoticeDto> noticeDtos = convert2NoticeDto(noticeModels, true);
        ListObjServiceResult result = new ListObjServiceResult(new ListObjResult(noticeDtos, total).asResult()).asResult();
        return result;
    }

    private List<SysNoticeDto> convert2NoticeDto(List<SysNoticeModel> noticeModels, boolean checkCurrentUserIsRead) {
        List<SysNoticeDto> noticeDtos = new ArrayList<>();
        for (SysNoticeModel n : noticeModels) {
            SysNoticeDto sysNoticeDto = getSysNoticeDto(checkCurrentUserIsRead, n);
            noticeDtos.add(sysNoticeDto);
        }
        return noticeDtos;
    }

    private int getUnreadCountByCurrentAdminUser(String userId) {
        Set<String> noticeIds = new HashSet<>();
        HumanCondition orgNoticeCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        List<SysNoticeOrgModel> orgNotices = noticeOrgMapper.list(orgNoticeCond);
        for (SysNoticeOrgModel orgNotice : orgNotices) {
            noticeIds.add(orgNotice.getNoticeId());
        }
        HumanCondition roleNoticeCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        List<SysNoticeRoleModel> roleNotices = noticeRoleMapper.list(roleNoticeCond);
        for (SysNoticeRoleModel roleNotice : roleNotices) {
            noticeIds.add(roleNotice.getNoticeId());
        }
        HumanCondition userNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeUserModel::getUser, userId)
                .select(SysNoticeUserModel::getNoticeId);
        List<SysNoticeUserModel> userNotices = noticeUserMapper.list(userNoticeCond);
        for (SysNoticeUserModel model : userNotices) {
            noticeIds.add(model.getNoticeId());
        }

        HumanCondition readNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterOrStart()
                .orEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .orEqual(SysNoticeReadModel::getIsReaded, 1)
                .filterOrEnd()
                .select(SysNoticeReadModel::getNoticeId);
        List<SysNoticeReadModel> notPushNotices = noticeReadMapper.list(readNoticeCond);
        List<String> notPushNoticeIds = new ArrayList<>();
        for (SysNoticeReadModel notPushNotice : notPushNotices) {
            String noticeId = notPushNotice.getNoticeId();
            notPushNoticeIds.add(noticeId);
        }
        HumanCondition noticeCond = new HumanCondition();
        if (CollectionUtils.isEmpty(noticeIds)) {
            noticeCond.filterEqual(SysNoticeModel::getIsPublic, 1);
        } else {
            noticeCond.filterOrStart()
                    .orEqual(SysNoticeModel::getIsPublic, 1)
                    .orIn(SysNoticeModel::getId, noticeIds.toArray())
                    .filterOrEnd();
        }
        if (CollectionUtils.isNotEmpty(notPushNoticeIds)) {
            noticeCond.filterNotIn(SysNoticeModel::getId, notPushNoticeIds.toArray());
        }
        int total = getModelMapper().total(noticeCond);
        return total;
    }

    public ListObjServiceResult listNotices(String title, List<String> ids, List<String> orgs, List<String> roles, String publisher, Integer type, Integer page, Integer pageSize) {
        HumanCondition publicCondition = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1)
                .select(SysNoticeModel::getId)
                .distinct();
        List<SysNoticeModel> publicModels = getModelMapper().list(publicCondition);
        List<String> publicNoticeIds = new ArrayList<>();
        for (SysNoticeModel publicModel : publicModels) {
            String sysNoticeModel1Id = publicModel.getId();
            publicNoticeIds.add(sysNoticeModel1Id);
        }

        HumanCondition condition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(ids)) {
            condition.filterIn(SysNoticeModel::getId, ids.toArray());
        }

        List<String> roleOrOrgNoticeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orgs) && CollectionUtils.isNotEmpty(roles)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                    .select(SysNoticeOrgModel::getNoticeId)
                    .distinct();
            List<SysNoticeOrgModel> noticeOrgModelList = noticeOrgMapper.list(orgCondition);
            List<String> result = new ArrayList<>();
            for (SysNoticeOrgModel sysNoticeOrgModel : noticeOrgModelList) {
                String id = sysNoticeOrgModel.getNoticeId();
                result.add(id);
            }
            roleOrOrgNoticeIds.addAll(result);

            HumanCondition roleCondition = new HumanCondition()
                    .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                    .select(SysNoticeRoleModel::getNoticeId)
                    .distinct();
            List<SysNoticeRoleModel> noticeRoleModelList = noticeRoleMapper.list(roleCondition);
            List<String> list = new ArrayList<>();
            for (SysNoticeRoleModel sysNoticeRoleModel : noticeRoleModelList) {
                String noticeId = sysNoticeRoleModel.getNoticeId();
                list.add(noticeId);
            }
            roleOrOrgNoticeIds.addAll(list);
            if (CollectionUtils.isEmpty(roleOrOrgNoticeIds)) {
                roleOrOrgNoticeIds.add("");
            }
            condition.filterIn(SysNoticeModel::getId, roleOrOrgNoticeIds.toArray());
        } else if (CollectionUtils.isNotEmpty(roles)) {
            HumanCondition roleCondition = new HumanCondition()
                    .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                    .select(SysNoticeRoleModel::getNoticeId)
                    .distinct();
            List<SysNoticeRoleModel> noticeRoleModelList = noticeRoleMapper.list(roleCondition);
            List<String> list = new ArrayList<>();
            for (SysNoticeRoleModel sysNoticeRoleModel : noticeRoleModelList) {
                String noticeId = sysNoticeRoleModel.getNoticeId();
                list.add(noticeId);
            }
            roleOrOrgNoticeIds.addAll(list);
            if (CollectionUtils.isEmpty(roleOrOrgNoticeIds)) {
                roleOrOrgNoticeIds.add("");
            }
            condition.filterIn(SysNoticeModel::getId, roleOrOrgNoticeIds.toArray());
        } else if (CollectionUtils.isNotEmpty(orgs)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                    .select(SysNoticeOrgModel::getNoticeId)
                    .distinct();
            List<SysNoticeOrgModel> noticeOrgModelList = noticeOrgMapper.list(orgCondition);
            List<String> list = new ArrayList<>();
            for (SysNoticeOrgModel sysNoticeOrgModel : noticeOrgModelList) {
                String noticeId = sysNoticeOrgModel.getNoticeId();
                list.add(noticeId);
            }
            roleOrOrgNoticeIds.addAll(list);
            if (CollectionUtils.isEmpty(roleOrOrgNoticeIds)) {
                roleOrOrgNoticeIds.add("");
            }
            condition.filterIn(SysNoticeModel::getId, roleOrOrgNoticeIds.toArray());
        }

        if (StringUtils.isNotBlank(publisher)) {
            condition.filterEqual(SysNoticeModel::getPublisher, publisher);
        }
        if (Objects.nonNull(type)) {
            condition.filterEqual(SysNoticeModel::getType, type);
        }
        if (StringUtils.isNotBlank(title)) {
            condition.filterContains(SysNoticeModel::getTitle, title);
        }
        if (StringUtils.isNotBlank(title) || CollectionUtils.isNotEmpty(ids) || CollectionUtils.isNotEmpty(orgs) || CollectionUtils.isNotEmpty(roles) || StringUtils.isNotBlank(publisher) || Objects.nonNull(type)) {
            condition.filterNotEqual(SysNoticeModel::getIsPublic, 1);
        }
        condition.select(SysNoticeModel::getId).distinct();
        List<String> noticeIds = new ArrayList<>();
        for (SysNoticeModel sysNoticeModel : getModelMapper().list(condition)) {
            String id = sysNoticeModel.getId();
            noticeIds.add(id);
        }
        noticeIds.addAll(publicNoticeIds);
        if (CollectionUtils.isEmpty(noticeIds)) {
            return new ListObjServiceResult(new ListObjResult(new ArrayList(), 0).asResult()).asResult();
        }
        HumanCondition humanCondition = new HumanCondition()
                .filterIn(SysNoticeModel::getId, noticeIds.toArray())
                .pagination(page, pageSize)
                .orderBy(SysNoticeModel::getTop, true)
                .orderBy(SysNoticeModel::getPk, true);
        int total = getModelMapper().total(humanCondition);
        List<SysNoticeModel> models = getModelMapper().list(humanCondition);
        List<SysNoticeDto> dtos = convert2NoticeDto(models, false);

        ListObjServiceResult result = new ListObjServiceResult(new ListObjResult(dtos, total).asResult()).asResult();
        return result;
    }

    public ListObjServiceResult listNoticeMessageByCurrentNormalUser(List<String> userIds, List<String> orgs, List<String> roles, String userId, int page, int pageSize) {
        // 查询发送给指定用户的notice
        if (CollectionUtils.isEmpty(userIds)) {
            userIds.add("");
        }
        HumanCondition userNoticeCond = new HumanCondition()
                .filterIn(SysNoticeUserModel::getUser, userIds.toArray())
                .select(SysNoticeUserModel::getNoticeId);
        Set<String> userNoticeIds = new HashSet<>();
        for (SysNoticeUserModel sysNoticeUserModel : noticeUserMapper.list(userNoticeCond)) {
            String sysNoticeUserModelNoticeId = sysNoticeUserModel.getNoticeId();
            userNoticeIds.add(sysNoticeUserModelNoticeId);
        }

        // 发送目标为仅为org
        HumanCondition allNoticeInRoleCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> allNoticeInRoleIds = new HashSet<>();
        for (SysNoticeRoleModel roleModel : noticeRoleMapper.list(allNoticeInRoleCond)) {
            String noticeRoleModel1NoticeId = roleModel.getNoticeId();
            allNoticeInRoleIds.add(noticeRoleModel1NoticeId);
        }
        HumanCondition allNoticePublicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1);
        Set<String> allNoticePublicIds = new HashSet<>();
        for (SysNoticeModel sysNoticeModel1 : getModelMapper().list(allNoticePublicCond)) {
            String noticeModel1Id = sysNoticeModel1.getId();
            allNoticePublicIds.add(noticeModel1Id);
        }

        if (CollectionUtils.isEmpty(orgs)) {
            orgs.add("");
        }
        HumanCondition inOrgsCond = new HumanCondition()
                .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> onlyToOrgNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel model : noticeOrgMapper.list(inOrgsCond)) {
            String orgModel1NoticeId = model.getNoticeId();
            onlyToOrgNoticeIds.add(orgModel1NoticeId);
        }
        onlyToOrgNoticeIds.removeAll(allNoticeInRoleIds);
        onlyToOrgNoticeIds.removeAll(allNoticePublicIds);

        // 发动目标仅为role
        HumanCondition allNoticeInOrgCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> allNoticeInOrgIds = new HashSet<>();
        for (SysNoticeOrgModel orgModel : noticeOrgMapper.list(allNoticeInOrgCond)) {
            String noticeOrgModel1NoticeId = orgModel.getNoticeId();
            allNoticeInOrgIds.add(noticeOrgModel1NoticeId);
        }
        if (CollectionUtils.isEmpty(roles)) {
            roles.add("");
        }
        HumanCondition inRolesCond = new HumanCondition()
                .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> onlyToRolesNoticeIds = new HashSet<>();
        for (SysNoticeRoleModel noticeRoleModel : noticeRoleMapper.list(inRolesCond)) {
            String sysNoticeRoleModel1NoticeId = noticeRoleModel.getNoticeId();
            onlyToRolesNoticeIds.add(sysNoticeRoleModel1NoticeId);
        }
        onlyToRolesNoticeIds.removeAll(allNoticeInOrgIds);

        HumanCondition publicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1)
                .select(SysNoticeModel::getId);
        Set<String> onlyToPublicNoticeIds = new HashSet<>();
        for (SysNoticeModel noticeModel : getModelMapper().list(publicCond)) {
            String sysNoticeModel1Id = noticeModel.getId();
            onlyToPublicNoticeIds.add(sysNoticeModel1Id);
        }
        onlyToPublicNoticeIds.removeAll(allNoticeInOrgIds);

        // 发送目标为role和org
        Set<String> toRoleAndOrg = new HashSet<>();
        toRoleAndOrg.addAll(allNoticeInOrgIds);
        toRoleAndOrg.retainAll(allNoticeInRoleIds);
        Set<String> inRolesAndOrgs = new HashSet<>();
        Set<String> inRolesNoticeIds = new HashSet<>();
        for (SysNoticeRoleModel sysNoticeRoleModel : noticeRoleMapper.list(inRolesCond)) {
            String sysNoticeRoleModelNoticeId = sysNoticeRoleModel.getNoticeId();
            inRolesNoticeIds.add(sysNoticeRoleModelNoticeId);
        }
        Set<String> inOrgsNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel noticeOrgModel : noticeOrgMapper.list(inOrgsCond)) {
            String sysNoticeOrgModel1NoticeId = noticeOrgModel.getNoticeId();
            inOrgsNoticeIds.add(sysNoticeOrgModel1NoticeId);
        }
        inRolesAndOrgs.addAll(inRolesNoticeIds);
        inRolesAndOrgs.retainAll(inOrgsNoticeIds);
        Set<String> inRolesAndOrgsNoticeids = new HashSet<>();
        for (String id : inRolesAndOrgs) {
            if (toRoleAndOrg.contains(id)) {
                inRolesAndOrgsNoticeids.add(id);
            }
        }

        // 发送目标为 public和org
        Set<String> allPublicNoticeIds = new HashSet<>();
        for (SysNoticeModel sysNoticeModel : getModelMapper().list(publicCond)) {
            String sysNoticeModelId = sysNoticeModel.getId();
            allPublicNoticeIds.add(sysNoticeModelId);
        }
        Set<String> allOrgsNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel sysNoticeOrgModel : noticeOrgMapper.list(inOrgsCond)) {
            String sysNoticeOrgModelNoticeId = sysNoticeOrgModel.getNoticeId();
            allOrgsNoticeIds.add(sysNoticeOrgModelNoticeId);
        }
        Set<String> toPublicAndOrg = new HashSet<>();
        toPublicAndOrg.addAll(allPublicNoticeIds);
        toPublicAndOrg.retainAll(allNoticeInOrgIds);
        Set<String> inPublicAndOrgs = new HashSet<>();
        inPublicAndOrgs.addAll(allPublicNoticeIds);
        inPublicAndOrgs.retainAll(allOrgsNoticeIds);
        Set<String> inPublicAndOrgsNoticeids = new HashSet<>();
        for (String id : inPublicAndOrgs) {
            if (toPublicAndOrg.contains(id)) {
                inPublicAndOrgsNoticeids.add(id);
            }
        }

        Set<String> allMatchIds = new HashSet<>();
        allMatchIds.addAll(userNoticeIds);
        allMatchIds.addAll(onlyToOrgNoticeIds);
        allMatchIds.addAll(onlyToRolesNoticeIds);
        allMatchIds.addAll(onlyToPublicNoticeIds);
        allMatchIds.addAll(inRolesAndOrgsNoticeids);
        allMatchIds.addAll(inPublicAndOrgsNoticeids);
        if (CollectionUtils.isEmpty(allMatchIds)) {
            return new ListObjServiceResult(new ListObjResult(new ArrayList(), 0).asResult()).asResult();
        }
        HumanCondition notNeedPush = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .select(SysNoticeReadModel::getNoticeId);
        Set<String> notPushNoticeIds = new HashSet<>();
        for (SysNoticeReadModel sysNoticeReadModel : noticeReadMapper.list(notNeedPush)) {
            String noticeId = sysNoticeReadModel.getNoticeId();
            notPushNoticeIds.add(noticeId);
        }
        HumanCondition matchCond = new HumanCondition()
                .filterIn(SysNoticeModel::getId, allMatchIds.toArray());
        if (CollectionUtils.isNotEmpty(notPushNoticeIds)) {
            matchCond.filterNotIn(SysNoticeModel::getId, notPushNoticeIds.toArray());
        }

        matchCond.orderBy(SysNoticeModel::getTop, true)
                .orderBy(SysNoticeModel::getPk, true);
        matchCond.pagination(page, pageSize);
        int total = getModelMapper().total(matchCond);
        List<SysNoticeModel> models = getModelMapper().list(matchCond);
        List<SysNoticeDto> dtos = convert2NoticeDto(models, true);
        ListObjServiceResult<SysNoticeDto> result = new ListObjServiceResult(new ListObjResult(dtos, total).asResult()).asResult();
        return result;
    }

    private int getUnreadCountByCurrentNormalUser(List<String> userIds, List<String> orgs, List<String> roles, String userId) {
        // 查询发送给指定用户的notice
        if (CollectionUtils.isEmpty(userIds)) {
            userIds.add("");
        }
        HumanCondition userNoticeCond = new HumanCondition()
                .filterIn(SysNoticeUserModel::getUser, userIds.toArray())
                .select(SysNoticeUserModel::getNoticeId);
        Set<String> userNoticeIds = new HashSet<>();
        for (SysNoticeUserModel sysNoticeUserModel : noticeUserMapper.list(userNoticeCond)) {
            String sysNoticeUserModelNoticeId = sysNoticeUserModel.getNoticeId();
            userNoticeIds.add(sysNoticeUserModelNoticeId);
        }

        // 发送目标为仅为org
        HumanCondition allNoticeInRoleCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> allNoticeInRoleIds = new HashSet<>();
        for (SysNoticeRoleModel roleModel : noticeRoleMapper.list(allNoticeInRoleCond)) {
            String noticeRoleModel1NoticeId = roleModel.getNoticeId();
            allNoticeInRoleIds.add(noticeRoleModel1NoticeId);
        }
        HumanCondition allNoticePublicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1);
        Set<String> allNoticePublicIds = new HashSet<>();
        for (SysNoticeModel sysNoticeModel1 : getModelMapper().list(allNoticePublicCond)) {
            String noticeModel1Id = sysNoticeModel1.getId();
            allNoticePublicIds.add(noticeModel1Id);
        }

        if (CollectionUtils.isEmpty(orgs)) {
            orgs.add("");
        }
        HumanCondition inOrgsCond = new HumanCondition()
                .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> onlyToOrgNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel model : noticeOrgMapper.list(inOrgsCond)) {
            String orgModel1NoticeId = model.getNoticeId();
            onlyToOrgNoticeIds.add(orgModel1NoticeId);
        }
        onlyToOrgNoticeIds.removeAll(allNoticeInRoleIds);
        onlyToOrgNoticeIds.removeAll(allNoticePublicIds);

        // 发动目标仅为role
        HumanCondition allNoticeInOrgCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> allNoticeInOrgIds = new HashSet<>();
        for (SysNoticeOrgModel orgModel : noticeOrgMapper.list(allNoticeInOrgCond)) {
            String noticeOrgModel1NoticeId = orgModel.getNoticeId();
            allNoticeInOrgIds.add(noticeOrgModel1NoticeId);
        }
        if (CollectionUtils.isEmpty(roles)) {
            roles.add("");
        }
        HumanCondition inRolesCond = new HumanCondition()
                .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> onlyToRolesNoticeIds = new HashSet<>();
        for (SysNoticeRoleModel noticeRoleModel : noticeRoleMapper.list(inRolesCond)) {
            String sysNoticeRoleModel1NoticeId = noticeRoleModel.getNoticeId();
            onlyToRolesNoticeIds.add(sysNoticeRoleModel1NoticeId);
        }
        onlyToRolesNoticeIds.removeAll(allNoticeInOrgIds);

        HumanCondition publicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1)
                .select(SysNoticeModel::getId);
        Set<String> onlyToPublicNoticeIds = new HashSet<>();
        for (SysNoticeModel noticeModel : getModelMapper().list(publicCond)) {
            String sysNoticeModel1Id = noticeModel.getId();
            onlyToPublicNoticeIds.add(sysNoticeModel1Id);
        }
        onlyToPublicNoticeIds.removeAll(allNoticeInOrgIds);

        // 发送目标为role和org
        Set<String> toRoleAndOrg = new HashSet<>();
        toRoleAndOrg.addAll(allNoticeInOrgIds);
        toRoleAndOrg.retainAll(allNoticeInRoleIds);
        Set<String> inRolesAndOrgs = new HashSet<>();
        Set<String> inRolesNoticeIds = new HashSet<>();
        for (SysNoticeRoleModel sysNoticeRoleModel : noticeRoleMapper.list(inRolesCond)) {
            String sysNoticeRoleModelNoticeId = sysNoticeRoleModel.getNoticeId();
            inRolesNoticeIds.add(sysNoticeRoleModelNoticeId);
        }
        Set<String> inOrgsNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel noticeOrgModel : noticeOrgMapper.list(inOrgsCond)) {
            String sysNoticeOrgModel1NoticeId = noticeOrgModel.getNoticeId();
            inOrgsNoticeIds.add(sysNoticeOrgModel1NoticeId);
        }
        inRolesAndOrgs.addAll(inRolesNoticeIds);
        inRolesAndOrgs.retainAll(inOrgsNoticeIds);
        Set<String> inRolesAndOrgsNoticeids = new HashSet<>();
        for (String id : inRolesAndOrgs) {
            if (toRoleAndOrg.contains(id)) {
                inRolesAndOrgsNoticeids.add(id);
            }
        }

        // 发送目标为 public和org
        Set<String> allPublicNoticeIds = new HashSet<>();
        for (SysNoticeModel sysNoticeModel : getModelMapper().list(publicCond)) {
            String sysNoticeModelId = sysNoticeModel.getId();
            allPublicNoticeIds.add(sysNoticeModelId);
        }
        Set<String> allOrgsNoticeIds = new HashSet<>();
        for (SysNoticeOrgModel sysNoticeOrgModel : noticeOrgMapper.list(inOrgsCond)) {
            String sysNoticeOrgModelNoticeId = sysNoticeOrgModel.getNoticeId();
            allOrgsNoticeIds.add(sysNoticeOrgModelNoticeId);
        }
        Set<String> toPublicAndOrg = new HashSet<>();
        toPublicAndOrg.addAll(allPublicNoticeIds);
        toPublicAndOrg.retainAll(allNoticeInOrgIds);
        Set<String> inPublicAndOrgs = new HashSet<>();
        inPublicAndOrgs.addAll(allPublicNoticeIds);
        inPublicAndOrgs.retainAll(allOrgsNoticeIds);
        Set<String> inPublicAndOrgsNoticeids = new HashSet<>();
        for (String id : inPublicAndOrgs) {
            if (toPublicAndOrg.contains(id)) {
                inPublicAndOrgsNoticeids.add(id);
            }
        }

        Set<String> allMatchIds = new HashSet<>();
        allMatchIds.addAll(userNoticeIds);
        allMatchIds.addAll(onlyToOrgNoticeIds);
        allMatchIds.addAll(onlyToRolesNoticeIds);
        allMatchIds.addAll(onlyToPublicNoticeIds);
        allMatchIds.addAll(inRolesAndOrgsNoticeids);
        allMatchIds.addAll(inPublicAndOrgsNoticeids);

        HumanCondition notNeed = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterOrStart()
                .orEqual(SysNoticeReadModel::getIsReaded, 1)
                .orEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .filterOrEnd();
        Set<String> readedNoticeIds = new HashSet<>();
        for (SysNoticeReadModel sysNoticeReadModel : noticeReadMapper.list(notNeed)) {
            String noticeId = sysNoticeReadModel.getNoticeId();
            readedNoticeIds.add(noticeId);
        }
        if (CollectionUtils.isEmpty(allMatchIds)) {
            return 0;
        }
        HumanCondition matchCond = new HumanCondition()
                .filterIn(SysNoticeModel::getId, allMatchIds.toArray());
        if (CollectionUtils.isNotEmpty(readedNoticeIds)) {
            matchCond.filterNotIn(SysNoticeModel::getId, readedNoticeIds.toArray());
        }
        int total = getModelMapper().total(matchCond);
        return total;
    }

    @Override
    public Boolean delayNotice(DelayNoticeRequest delayNoticeRequest) {
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser) || StringUtils.isBlank(loginUser.getId())) {
            log.error("current user not exist");
            return false;
        }

        UserModel userModel = new UserModel();
        userModel.setId(loginUser.getId());
        LocalDateTime delayRemindLicenseTime;
        if (delayNoticeRequest.getDelayType() == 0) {
            delayRemindLicenseTime = LocalDateTime.now().plusDays(6);
        } else {
            ActivationResponse content = activationService.status();
            delayRemindLicenseTime = TimeUtils.stringToLocalDateTime(content.getExpiredDate() + " 23:59:59");
        }
        userModel.setDelayRemindLicenseTime(delayRemindLicenseTime);
        userMapper.update(userModel);

        return true;
    }

}
