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
import cn.cyberpeace.mp.frontadmin.service.SysNoticeReadService;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeRoleService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeOrgService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeUserService;
import cn.cyberpeace.mp.frontadmin.util.SQLUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


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
                
                SysNoticeReadModel noticeReadModelNew = SysNoticeReadModel.builder().noticeId(noticeId).user(userModel.getId()).isReaded(0).readCount(0).isNeedPush(0).build();
                noticeReadMapper.create(noticeReadModelNew);
            } else {
                
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
            
            SysNoticeReadModel noticeReadModelNew = SysNoticeReadModel.builder().noticeId(noticeId).user(userModel.getId()).isReaded(1).readCount(1).isNeedPush(1).build();
            noticeReadMapper.create(noticeReadModelNew);
        } else {
            
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
        
        List<SysNoticeOrgModel> noticeOrgModelList = new ArrayList<>();
        if (CollUtil.isNotEmpty(sysNoticeRequest.getOrgIds())) {
            sysNoticeRequest.getOrgIds().forEach(orgId -> {
                SysNoticeOrgModel sysNoticeOrgModel = new SysNoticeOrgModel();
                sysNoticeOrgModel.setOrg(orgId);
                sysNoticeOrgModel.setNoticeId(resultModel.getId());
                noticeOrgModelList.add(sysNoticeOrgModel);
            });
        }
        noticeOrgMapper.bulkCreate(noticeOrgModelList);


        
        List<SysNoticeUserModel> noticeUserModelList = new ArrayList<>();
        if (CollUtil.isNotEmpty(sysNoticeRequest.getUserIds())) {
            sysNoticeRequest.getUserIds().forEach(userId -> {
                SysNoticeUserModel sysNoticeUserModel = new SysNoticeUserModel();
                sysNoticeUserModel.setUser(userId);
                sysNoticeUserModel.setNoticeId(resultModel.getId());
                noticeUserModelList.add(sysNoticeUserModel);
            });
        }
        noticeUserMapper.bulkCreate(noticeUserModelList);

        
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

        

        sysNoticeOrgService.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));
        sysNoticeUserService.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));
        sysNoticeRoleService.deleteByNoticeIds(CollectionUtil.toList(resultModel.getId()));

        
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

        
        
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getId().equals(AuthConstant.ROLE_ADMIN_ID));

        
        if (isAdmin) {
            return listNoticeMessageByCurrentAdminUser(currentUser.getId(), req.getPage(), req.getPageSize());
        }

        
        List<String> allRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = tenantUserService.listOrgsByUserId(currentUser.getId());
        orgBOS.forEach(o -> {
            
            allOrgIds.add(o.getId());
            
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });


        
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

        
        
        List<RoleModel> roleModels = userRoleService.listByUserId(currentUser.getId());
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getId().equals(AuthConstant.ROLE_ADMIN_ID));

        
        if (isAdmin) {
            int totalCnt = getUnreadCountByCurrentAdminUser(currentUser.getId());
            return totalCnt;
        }

        
        List<String> allRoleIds = roleModels.stream().map(RoleModel::getId).collect(Collectors.toList());

        
        List<String> allOrgIds = new ArrayList<>();
        List<OrgBO> orgBOS = tenantUserService.listOrgsByUserId(currentUser.getId());
        orgBOS.forEach(o -> {
            
            allOrgIds.add(o.getId());
            
            List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(o.getId());
            allOrgIds.addAll(orgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        });


        
        int totalCnt = getUnreadCountByCurrentNormalUser(Arrays.asList(currentUser.getId()), allOrgIds, allRoleIds, currentUser.getId());
        return totalCnt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        ServiceResult result = ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
        if (Objects.nonNull(ids) && ids.length > 0) {
            result = super.bulkDelete(ids);

            sysNoticeOrgService.deleteByNoticeIds(CollectionUtil.toList(ids));
            sysNoticeRoleService.deleteByNoticeIds(CollectionUtil.toList(ids));
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

        
        if (StringUtils.isNotBlank(noticeModel.getPublisher())) {
            ObjServiceResult<UserModel> userResult = userService.get(noticeDto.getPublisher());
            if (Objects.nonNull(userResult.getSource())) {
                noticeDto.setPublisherUserName(userResult.getSource().getObj().getUsername());
            }
        }

        
        List<OrgModel> orgModels = sysNoticeOrgService.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        if (CollectionUtil.isNotEmpty(orgModels)) {
            noticeDto.setOrgIds(orgModels.stream().map(OrgModel::getId).collect(Collectors.toList()));
            List<OrgBO> orgBOS = orgModels.stream().map(orgModel -> orgService.orgModel2OrgBO(orgModel)).collect(Collectors.toList());
            noticeDto.setOrgPaths(orgBOS.stream().map(OrgBO::getPath).collect(Collectors.joining(",")));
        }

        
        List<UserModel> userModels = listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        userModels.stream().forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));

        if (CollectionUtil.isNotEmpty(userModels)) {
            List<UserSimpleBo> userSimpleBos = userModels.stream().map(userModel -> {
                UserSimpleBo userSimpleBo = new UserSimpleBo();
                BeanUtils.copyProperties(userModel, userSimpleBo);
                return userSimpleBo;
            }).collect(Collectors.toList());
            noticeDto.setUsers(userSimpleBos);
        }

        
        List<RoleModel> roleModels = sysNoticeRoleService.listByNoticeIds(CollectionUtil.toList(noticeModel.getId()));
        if (CollectionUtil.isNotEmpty(roleModels)) {
            noticeDto.setRoleIds(roleModels.stream().map(RoleModel::getId).collect(Collectors.toList()));
            noticeDto.setRoleNames(roleModels.stream().map(RoleModel::getName).collect(Collectors.joining(",")));
        }

        
        if (checkCurrentUserIsRead) {
            UserModel userModel = UserUtils.getLoginUser();
            if (Objects.nonNull(userModel)) {
                String currentUserId = userModel.getId();
                SysNoticeReadModel noticeReadModel = sysNoticeReadService.selectByUserIdAndNoticeId(currentUserId, noticeModel.getId());
                if (Objects.isNull(noticeReadModel)) {
                    
                    noticeDto.setIsRead(0);
                } else {
                    
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

        pageInfo.setList(noticeDtos);
    }

    private List<UserModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollUtil.isNotEmpty(noticeIds)) {
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
        
        HumanCondition orgNoticeCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        List<SysNoticeOrgModel> orgNotices = noticeOrgMapper.list(orgNoticeCond);
        orgNotices.forEach(model -> noticeIds.add(model.getNoticeId()));
        
        HumanCondition roleNoticeCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        List<SysNoticeRoleModel> roleNotices = noticeRoleMapper.list(roleNoticeCond);
        roleNotices.forEach(model -> noticeIds.add(model.getNoticeId()));
        
        HumanCondition userNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeUserModel::getUser, userId)
                .select(SysNoticeUserModel::getNoticeId);
        List<SysNoticeUserModel> userNotices = noticeUserMapper.list(userNoticeCond);
        userNotices.forEach(model -> noticeIds.add(model.getNoticeId()));

        HumanCondition readNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .select(SysNoticeReadModel::getNoticeId);
        List<SysNoticeReadModel> notPushNotices = noticeReadMapper.list(readNoticeCond);
        List<String> notPushNoticeIds = notPushNotices.stream().map(SysNoticeReadModel::getNoticeId).collect(Collectors.toList());

        HumanCondition noticeCond = new HumanCondition();
        if (CollUtil.isEmpty(noticeIds)) {
            noticeCond.filterEqual(SysNoticeModel::getIsPublic, 1);
        } else {
            noticeCond.filterOrStart()
                    .orEqual(SysNoticeModel::getIsPublic, 1)
                    .orIn(SysNoticeModel::getId, noticeIds.toArray())
                    .filterOrEnd();
        }
        if (CollUtil.isNotEmpty(notPushNoticeIds)) {
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
        List<SysNoticeDto> noticeDtos = noticeModels.stream().map(n -> {
            SysNoticeDto noticeDto = noticeModel2NoticeDto(n, checkCurrentUserIsRead);
            return noticeDto;
        }).collect(Collectors.toList());
        return noticeDtos;
    }

    private int getUnreadCountByCurrentAdminUser(String userId) {
        Set<String> noticeIds = new HashSet<>();
        HumanCondition orgNoticeCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        List<SysNoticeOrgModel> orgNotices = noticeOrgMapper.list(orgNoticeCond);
        orgNotices.forEach(model -> noticeIds.add(model.getNoticeId()));
        HumanCondition roleNoticeCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        List<SysNoticeRoleModel> roleNotices = noticeRoleMapper.list(roleNoticeCond);
        roleNotices.forEach(model -> noticeIds.add(model.getNoticeId()));
        HumanCondition userNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeUserModel::getUser, userId)
                .select(SysNoticeUserModel::getNoticeId);
        List<SysNoticeUserModel> userNotices = noticeUserMapper.list(userNoticeCond);
        userNotices.forEach(model -> noticeIds.add(model.getNoticeId()));

        HumanCondition readNoticeCond = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterOrStart()
                .orEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .orEqual(SysNoticeReadModel::getIsReaded, 1)
                .filterOrEnd()
                .select(SysNoticeReadModel::getNoticeId);
        List<SysNoticeReadModel> notPushNotices = noticeReadMapper.list(readNoticeCond);
        List<String> notPushNoticeIds = notPushNotices.stream().map(SysNoticeReadModel::getNoticeId).collect(Collectors.toList());
        HumanCondition noticeCond = new HumanCondition();
        if (CollUtil.isEmpty(noticeIds)) {
            noticeCond.filterEqual(SysNoticeModel::getIsPublic, 1);
        } else {
            noticeCond.filterOrStart()
                    .orEqual(SysNoticeModel::getIsPublic, 1)
                    .orIn(SysNoticeModel::getId, noticeIds.toArray())
                    .filterOrEnd();
        }
        if (CollUtil.isNotEmpty(notPushNoticeIds)) {
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
        List<String> publicNoticeIds = publicModels.stream().map(SysNoticeModel::getId).collect(Collectors.toList());

        HumanCondition condition = new HumanCondition();
        if (CollUtil.isNotEmpty(ids)) {
            condition.filterIn(SysNoticeModel::getId, ids.toArray());
        }

        List<String> roleOrOrgNoticeIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(orgs) && CollUtil.isNotEmpty(roles)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                    .select(SysNoticeOrgModel::getNoticeId)
                    .distinct();
            List<SysNoticeOrgModel> noticeOrgModelList = noticeOrgMapper.list(orgCondition);
            roleOrOrgNoticeIds.addAll(noticeOrgModelList.stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toList()));

            HumanCondition roleCondition = new HumanCondition()
                    .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                    .select(SysNoticeRoleModel::getNoticeId)
                    .distinct();
            List<SysNoticeRoleModel> noticeRoleModelList = noticeRoleMapper.list(roleCondition);
            roleOrOrgNoticeIds.addAll(noticeRoleModelList.stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toList()));
            if (CollUtil.isEmpty(roleOrOrgNoticeIds)) {
                roleOrOrgNoticeIds.add("");
            }
            condition.filterIn(SysNoticeModel::getId, roleOrOrgNoticeIds.toArray());
        } else if (CollUtil.isNotEmpty(roles)) {
            HumanCondition roleCondition = new HumanCondition()
                    .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                    .select(SysNoticeRoleModel::getNoticeId)
                    .distinct();
            List<SysNoticeRoleModel> noticeRoleModelList = noticeRoleMapper.list(roleCondition);
            roleOrOrgNoticeIds.addAll(noticeRoleModelList.stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toList()));
            if (CollUtil.isEmpty(roleOrOrgNoticeIds)) {
                roleOrOrgNoticeIds.add("");
            }
            condition.filterIn(SysNoticeModel::getId, roleOrOrgNoticeIds.toArray());
        } else if (CollUtil.isNotEmpty(orgs)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                    .select(SysNoticeOrgModel::getNoticeId)
                    .distinct();
            List<SysNoticeOrgModel> noticeOrgModelList = noticeOrgMapper.list(orgCondition);
            roleOrOrgNoticeIds.addAll(noticeOrgModelList.stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toList()));
            if (CollUtil.isEmpty(roleOrOrgNoticeIds)) {
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
        if (StringUtils.isNotBlank(title) || CollUtil.isNotEmpty(ids) || CollUtil.isNotEmpty(orgs) || CollUtil.isNotEmpty(roles) || StringUtils.isNotBlank(publisher) || Objects.nonNull(type)) {
            condition.filterNotEqual(SysNoticeModel::getIsPublic, 1);
        }
        condition.select(SysNoticeModel::getId).distinct();
        List<String> noticeIds = getModelMapper().list(condition).stream().map(SysNoticeModel::getId).collect(Collectors.toList());
        noticeIds.addAll(publicNoticeIds);
        if (CollUtil.isEmpty(noticeIds)) {
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
        
        if (CollUtil.isEmpty(userIds)) {
            userIds.add("");
        }
        HumanCondition userNoticeCond = new HumanCondition()
                .filterIn(SysNoticeUserModel::getUser, userIds.toArray())
                .select(SysNoticeUserModel::getNoticeId);
        Set<String> userNoticeIds = noticeUserMapper.list(userNoticeCond).stream().map(SysNoticeUserModel::getNoticeId).collect(Collectors.toSet());

        
        HumanCondition allNoticeInRoleCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> allNoticeInRoleIds = noticeRoleMapper.list(allNoticeInRoleCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        HumanCondition allNoticePublicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1);
        Set<String> allNoticePublicIds = getModelMapper().list(allNoticePublicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());

        if (CollUtil.isEmpty(orgs)) {
            orgs.add("");
        }
        HumanCondition inOrgsCond = new HumanCondition()
                .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> onlyToOrgNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        onlyToOrgNoticeIds.removeAll(allNoticeInRoleIds);
        onlyToOrgNoticeIds.removeAll(allNoticePublicIds);

        
        HumanCondition allNoticeInOrgCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> allNoticeInOrgIds = noticeOrgMapper.list(allNoticeInOrgCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(roles)) {
            roles.add("");
        }
        HumanCondition inRolesCond = new HumanCondition()
                .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> onlyToRolesNoticeIds = noticeRoleMapper.list(inRolesCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        onlyToRolesNoticeIds.removeAll(allNoticeInOrgIds);

        HumanCondition publicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1)
                .select(SysNoticeModel::getId);
        Set<String> onlyToPublicNoticeIds = getModelMapper().list(publicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());
        onlyToPublicNoticeIds.removeAll(allNoticeInOrgIds);

        
        Set<String> toRoleAndOrg = new HashSet<>();
        toRoleAndOrg.addAll(allNoticeInOrgIds);
        toRoleAndOrg.retainAll(allNoticeInRoleIds);
        Set<String> inRolesAndOrgs = new HashSet<>();
        Set<String>  inRolesNoticeIds = noticeRoleMapper.list(inRolesCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        Set<String>  inOrgsNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        inRolesAndOrgs.addAll(inRolesNoticeIds);
        inRolesAndOrgs.retainAll(inOrgsNoticeIds);
        Set<String> inRolesAndOrgsNoticeids = new HashSet<>();
        for (String id : inRolesAndOrgs) {
            if (toRoleAndOrg.contains(id)) {
                inRolesAndOrgsNoticeids.add(id);
            }
        }

        
        Set<String> allPublicNoticeIds = getModelMapper().list(publicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());
        Set<String> allOrgsNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
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
        if (CollUtil.isEmpty(allMatchIds)) {
            return new ListObjServiceResult(new ListObjResult(new ArrayList(), 0).asResult()).asResult();
        }
        HumanCondition notNeedPush = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getUser, userId)
                .filterEqual(SysNoticeReadModel::getIsNeedPush, 0)
                .select(SysNoticeReadModel::getNoticeId);
        Set<String> notPushNoticeIds = noticeReadMapper.list(notNeedPush).stream().map(SysNoticeReadModel::getNoticeId).collect(Collectors.toSet());
        HumanCondition matchCond = new HumanCondition()
                .filterIn(SysNoticeModel::getId, allMatchIds.toArray());
        if (CollUtil.isNotEmpty(notPushNoticeIds)) {
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
        
        if (CollUtil.isEmpty(userIds)) {
            userIds.add("");
        }
        HumanCondition userNoticeCond = new HumanCondition()
                .filterIn(SysNoticeUserModel::getUser, userIds.toArray())
                .select(SysNoticeUserModel::getNoticeId);
        Set<String> userNoticeIds = noticeUserMapper.list(userNoticeCond).stream().map(SysNoticeUserModel::getNoticeId).collect(Collectors.toSet());

        
        HumanCondition allNoticeInRoleCond = new HumanCondition()
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> allNoticeInRoleIds = noticeRoleMapper.list(allNoticeInRoleCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        HumanCondition allNoticePublicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1);
        Set<String> allNoticePublicIds = getModelMapper().list(allNoticePublicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());

        if (CollUtil.isEmpty(orgs)) {
            orgs.add("");
        }
        HumanCondition inOrgsCond = new HumanCondition()
                .filterIn(SysNoticeOrgModel::getOrg, orgs.toArray())
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> onlyToOrgNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        onlyToOrgNoticeIds.removeAll(allNoticeInRoleIds);
        onlyToOrgNoticeIds.removeAll(allNoticePublicIds);

        
        HumanCondition allNoticeInOrgCond = new HumanCondition()
                .select(SysNoticeOrgModel::getNoticeId);
        Set<String> allNoticeInOrgIds = noticeOrgMapper.list(allNoticeInOrgCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(roles)) {
            roles.add("");
        }
        HumanCondition inRolesCond = new HumanCondition()
                .filterIn(SysNoticeRoleModel::getRole, roles.toArray())
                .select(SysNoticeRoleModel::getNoticeId);
        Set<String> onlyToRolesNoticeIds = noticeRoleMapper.list(inRolesCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        onlyToRolesNoticeIds.removeAll(allNoticeInOrgIds);

        HumanCondition publicCond = new HumanCondition()
                .filterEqual(SysNoticeModel::getIsPublic, 1)
                .select(SysNoticeModel::getId);
        Set<String> onlyToPublicNoticeIds = getModelMapper().list(publicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());
        onlyToPublicNoticeIds.removeAll(allNoticeInOrgIds);

        
        Set<String> toRoleAndOrg = new HashSet<>();
        toRoleAndOrg.addAll(allNoticeInOrgIds);
        toRoleAndOrg.retainAll(allNoticeInRoleIds);
        Set<String> inRolesAndOrgs = new HashSet<>();
        Set<String>  inRolesNoticeIds = noticeRoleMapper.list(inRolesCond).stream().map(SysNoticeRoleModel::getNoticeId).collect(Collectors.toSet());
        Set<String>  inOrgsNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
        inRolesAndOrgs.addAll(inRolesNoticeIds);
        inRolesAndOrgs.retainAll(inOrgsNoticeIds);
        Set<String> inRolesAndOrgsNoticeids = new HashSet<>();
        for (String id : inRolesAndOrgs) {
            if (toRoleAndOrg.contains(id)) {
                inRolesAndOrgsNoticeids.add(id);
            }
        }

        
        Set<String> allPublicNoticeIds = getModelMapper().list(publicCond).stream().map(SysNoticeModel::getId).collect(Collectors.toSet());
        Set<String> allOrgsNoticeIds = noticeOrgMapper.list(inOrgsCond).stream().map(SysNoticeOrgModel::getNoticeId).collect(Collectors.toSet());
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
        Set<String> readedNoticeIds = noticeReadMapper.list(notNeed).stream().map(SysNoticeReadModel::getNoticeId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(allMatchIds)) {
            return 0;
        }
        HumanCondition matchCond = new HumanCondition()
                .filterIn(SysNoticeModel::getId, allMatchIds.toArray());
        if (CollUtil.isNotEmpty(readedNoticeIds)) {
            matchCond.filterNotIn(SysNoticeModel::getId, readedNoticeIds.toArray());
        }
        int total = getModelMapper().total(matchCond);
        return total;
    }
}
