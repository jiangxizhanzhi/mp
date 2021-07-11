package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.UserOrgBo;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineCntBo;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineRecordSummaryBo;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineRecordMapper;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineRecordFields;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Service
@Slf4j
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class UserOnlineRecordServiceImpl extends BaseModelServiceImpl<UserOnlineRecordModel, UserOnlineRecordMapper, UserOnlineRecordFields, QueryParams> implements UserOnlineRecordService {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserHeartbeatService heartbeatService;

    public static final String CACHE_KEY_USER_OL_CNT = "USER_OL_CNT";

    private static final int DEFAULT_MINUTES = 5;



    @Autowired
    public UserOnlineRecordServiceImpl(UserOnlineRecordMapper modelMapper, UserOnlineRecordFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }


    @Override
    public PageInfo<UserOnlineReportBo> recordSummaryPage(UserOnlineRecordSummaryPageRequest req) {

        ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = heartbeatService.getHeartbeatMap();
        List<String> onlinedUserIds = heartbeatMap.entrySet().stream()
                .filter(entry -> CollectionUtil.isNotEmpty(entry.getValue()) && entry.getValue().size() > 0)
                .map(entry -> entry.getKey().getId()).collect(Collectors.toList());

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineRecordSummaryBo> recordSummaryDtoList = getModelMapper().listRecordSummary(req.getUserIds(), onlinedUserIds, req.getStartTime(), req.getEndTime());
        PageInfo pageInfo = new PageInfo(recordSummaryDtoList);
        List<UserOnlineRecordSummaryBo> summaryDtos = pageInfo.getList();
        if (CollectionUtil.isEmpty(summaryDtos)) {
            return pageInfo;
        }

        Map<String, Integer> heatbeatCntMap = new HashMap<>();
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> entry : heartbeatMap.entrySet()) {
            if (Objects.nonNull(entry.getKey().getId()) && Objects.nonNull(entry.getValue())) {
                heatbeatCntMap.put(entry.getKey().getId(), entry.getValue().size());
            }
        }

        List<UserOnlineReportBo> reportDtos = new ArrayList<>();
        summaryDtos.forEach(r -> {
            UserOnlineReportBo reportDto = new UserOnlineReportBo();
            BeanUtils.copyProperties(r, reportDto);

            reportDto.setTotalOnlineHours(0.0);
            Long totalMins = r.getTotalOnlineMinutes();
            if (totalMins > 0) {
                double totalHrs = totalMins / 60.0;
                reportDto.setTotalOnlineHours(NumberUtil.round(totalHrs, 2).doubleValue());
            }

            
            OperationLogPageRequest logPageRequest = new OperationLogPageRequest();
            logPageRequest.setUserId(r.getUser());
            logPageRequest.setPage(1);
            logPageRequest.setPerPage(1);
            List<OperationLogPageResponse> logs = userLogService.listOpLog(logPageRequest);
            if (CollectionUtil.isNotEmpty(logs) && Objects.nonNull(logs.get(0))) {
                
                reportDto.setAction(logs.get(0).getOpType());
                
                reportDto.setSubject(logs.get(0).getOpTarget());
            }

            boolean isOnline = false;
            if (Objects.nonNull(heatbeatCntMap.get(r.getUser())) && heatbeatCntMap.get(r.getUser()) > 0) {
                isOnline = true;
            } else if (Objects.nonNull(r.getUnCompleteCnt()) && r.getUnCompleteCnt() > 0) {
                isOnline = true;
            }

            if (isOnline) {
                reportDto.setIsOnline(1);
            } else {
                reportDto.setIsOnline(0);
            }

            reportDtos.add(reportDto);
        });


        
        List<String> userIds = summaryDtos.stream().map(UserOnlineRecordSummaryBo::getUser).collect(Collectors.toList());
        List<UserOrgBo> userOrgBos = tenantUserService.listUserOrgByUserIds(userIds);
        reportDtos.forEach(r -> {
            List<OrgBO> orgBOList =
                    userOrgBos.stream().filter(userOrgBo -> Objects.equals(userOrgBo.getUserId(), r.getUser())).map(dto-> {
                        OrgBO orgBO = new OrgBO();
                        BeanUtils.copyProperties(dto, orgBO);
                        return orgBO;
                    }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orgBOList)) {
                r.setOrg(orgBOList.stream().map(OrgBO::getPath).collect(Collectors.joining(",")));
            }
        });

        
        pageInfo.setList(reportDtos);
        return pageInfo;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOnlined(List<String> userIds) {
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserOnlineRecordModel::getUser, userIds.toArray());
        }
        humanCondition.filterGreaterThan(UserOnlineRecordModel::getEndTime, fromTime);
        humanCondition.filterEqual(UserOnlineRecordModel::getStatus, AuthConstant.USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0);
        List<UserOnlineRecordModel> onlineList = getModelMapper().list(humanCondition);
        return onlineList;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOfflined(List<String> userIds) {
        
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserOnlineRecordModel::getUser, userIds.toArray());
        }
        humanCondition.filterLessThan(UserOnlineRecordModel::getEndTime, fromTime);
        humanCondition.filterEqual(UserOnlineRecordModel::getStatus, AuthConstant.USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0);
        List<UserOnlineRecordModel> onlineList = getModelMapper().list(humanCondition);
        return onlineList;
    }

    


    @Override
    public Integer getOnlinedUserCntByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        Integer cnt = findOnlineUserCntByPeriod(startTime, endTime);
        return cnt;
    }

    @Override
    @Cacheable(value = CACHE_KEY_USER_OL_CNT)
    public UserOnlineCntBo onlineUserCnt() {
        LocalDateTime now = LocalDateTime.now();
        int olcnt = 0;
        int mapcnt = heartbeatService.getHeartbeatMap().size();
        int dbcnt = getOnlinedUserCntByPeriod(now.minusMinutes(5), now);
        olcnt = Math.max(mapcnt, dbcnt);
        int totalUser = userService.totalUserCnt();

        UserOnlineCntBo userOnlineCntBo = new UserOnlineCntBo();
        userOnlineCntBo.setOnlineUser(olcnt);
        userOnlineCntBo.setTotalUser(totalUser);

        return userOnlineCntBo;
    }

    private Integer findOnlineUserCntByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        HumanCondition notOnlineCondition = new HumanCondition()
                .filterOrStart()
                .orGreaterThanOrEqual(UserOnlineRecordModel::getStartTime, endTime)
                .orLessThanOrEqual(UserOnlineRecordModel::getEndTime, startTime)
                .filterOrEnd()
                .select(UserOnlineRecordModel::getPk);
        List<UserOnlineRecordModel> notOnlines = getModelMapper().list(notOnlineCondition);
        if (CollUtil.isEmpty(notOnlines)) {
            UserOnlineRecordModel model = new UserOnlineRecordModel();
            model.setPk(-1);
            notOnlines.add(model);
        }
        List<Integer> notOnlinePks = notOnlines.stream()
                .map(UserOnlineRecordModel::getPk)
                .collect(Collectors.toList());
        HumanCondition onlineCondition = new HumanCondition()
                .filterNotIn(UserOnlineRecordModel::getPk, notOnlinePks.toArray())
                .select(UserOnlineRecordModel::getPk)
                .groupBy(UserOnlineRecordModel::getUser);
        List<UserOnlineRecordModel> onlines = getModelMapper().list(onlineCondition);
        if (CollUtil.isEmpty(onlines)) {
            return 0;
        }
        List<Integer> onlinePks = onlines.stream().map(UserOnlineRecordModel::getPk).collect(Collectors.toList());
        HumanCondition countCondition = new HumanCondition()
                .filterIn(UserOnlineRecordModel::getPk, onlinePks.toArray())
                .selectAs(Count.f(UserOnlineRecordModel::getUser), "cnt");
        Map<String, Object> countMap = getModelMapper().originFirst(countCondition);
        Integer cnt = ((Long) countMap.get("cnt")).intValue();
        return cnt;
    }
}
