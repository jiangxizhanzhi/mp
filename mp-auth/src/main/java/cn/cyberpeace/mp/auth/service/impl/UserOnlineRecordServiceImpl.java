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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
        List<String> onlinedUserIds = new ArrayList<>();
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> userModelCopyOnWriteArrayListEntry : heartbeatMap.entrySet()) {
            if (CollectionUtils.isNotEmpty(userModelCopyOnWriteArrayListEntry.getValue()) && userModelCopyOnWriteArrayListEntry.getValue().size() > 0) {
                String id = userModelCopyOnWriteArrayListEntry.getKey().getId();
                onlinedUserIds.add(id);
            }
        }

        PageHelper.startPage(req.getPage(), req.getPageSize());
        // 查询时需要排前面的用户id
        List<String> frontUserIds = onlinedUserIds;
        // 排后面的userid
        List<String> behindUserIds = req.getUserIds() == null ? new ArrayList<>() : new ArrayList<>(req.getUserIds());
        behindUserIds.removeAll(frontUserIds);
        if (CollectionUtils.isNotEmpty(frontUserIds) && CollectionUtils.isNotEmpty(behindUserIds)) {
            // 先查front中的 再查behind中的 ， union之后分页
        } else if (CollectionUtils.isNotEmpty(frontUserIds)) {

        } else if (CollectionUtils.isNotEmpty(behindUserIds)) {

        } else {

        }

        List<UserOnlineRecordSummaryBo> recordSummaryDtoList = getModelMapper().listRecordSummary(req.getUserIds(), onlinedUserIds, req.getStartTime(), req.getEndTime());
        PageInfo pageInfo = new PageInfo(recordSummaryDtoList);
        List<UserOnlineRecordSummaryBo> summaryDtos = pageInfo.getList();
        if (CollectionUtils.isEmpty(summaryDtos)) {
            return pageInfo;
        }

        Map<String, Integer> heatbeatCntMap = new HashMap<>();
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> entry : heartbeatMap.entrySet()) {
            if (Objects.nonNull(entry.getKey().getId()) && Objects.nonNull(entry.getValue())) {
                heatbeatCntMap.put(entry.getKey().getId(), entry.getValue().size());
            }
        }

        List<UserOnlineReportBo> reportDtos = new ArrayList<>();
        for (UserOnlineRecordSummaryBo userOnlineRecordSummaryBo : summaryDtos) {
            UserOnlineReportBo reportDto = new UserOnlineReportBo();
            BeanUtils.copyProperties(userOnlineRecordSummaryBo, reportDto);

            reportDto.setTotalOnlineHours(0.0);
            Long totalMins = userOnlineRecordSummaryBo.getTotalOnlineMinutes();
            if (totalMins > 0) {
                double totalHrs = totalMins / 60.0;
                BigDecimal decimalValue = BigDecimal.valueOf(totalHrs);
                decimalValue = decimalValue.setScale(2, RoundingMode.HALF_UP);
                reportDto.setTotalOnlineHours(decimalValue.doubleValue());
            }

            // 只获取最新的一条日志
            OperationLogPageRequest logPageRequest = new OperationLogPageRequest();
            logPageRequest.setUserId(userOnlineRecordSummaryBo.getUser());
            logPageRequest.setPage(1);
            logPageRequest.setPerPage(1);
            List<OperationLogPageResponse> logs = userLogService.listOpLog(logPageRequest);
            if (CollectionUtils.isNotEmpty(logs) && Objects.nonNull(logs.get(0))) {
                // 动作
                reportDto.setAction(logs.get(0).getOpType());
                // 对象
                reportDto.setSubject(logs.get(0).getOpTarget());
            }

            boolean isOnline = false;
            if (Objects.nonNull(heatbeatCntMap.get(userOnlineRecordSummaryBo.getUser())) && heatbeatCntMap.get(userOnlineRecordSummaryBo.getUser()) > 0) {
                isOnline = true;
            } else if (Objects.nonNull(userOnlineRecordSummaryBo.getUnCompleteCnt()) && userOnlineRecordSummaryBo.getUnCompleteCnt() > 0) {
                isOnline = true;
            }

            if (isOnline) {
                reportDto.setIsOnline(1);
            } else {
                reportDto.setIsOnline(0);
            }

            reportDtos.add(reportDto);
        }


        // orgpaths
        List<String> userIds = new ArrayList<>();
        for (UserOnlineRecordSummaryBo summaryDto : summaryDtos) {
            String user = summaryDto.getUser();
            userIds.add(user);
        }
        List<UserOrgBo> userOrgBos = tenantUserService.listUserOrgByUserIds(userIds);
        for (UserOnlineReportBo r : reportDtos) {
            List<OrgBO> orgBOList =
                    new ArrayList<>();
            for (UserOrgBo userOrgBo : userOrgBos) {
                if (Objects.equals(userOrgBo.getUserId(), r.getUser())) {
                    OrgBO orgBO = new OrgBO();
                    BeanUtils.copyProperties(userOrgBo, orgBO);
                    OrgBO apply = orgBO;
                    orgBOList.add(apply);
                }
            }
            if (CollectionUtils.isNotEmpty(orgBOList)) {
                StringJoiner joiner = new StringJoiner(",");
                for (OrgBO orgBO : orgBOList) {
                    String path = orgBO.getPath();
                    joiner.add(path);
                }
                r.setOrg(joiner.toString());
            }
        }

        // 替换为reportDto
        pageInfo.setList(reportDtos);
        return pageInfo;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOnlined(List<String> userIds) {
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserOnlineRecordModel::getUser, userIds.toArray());
        }
        humanCondition.filterGreaterThan(UserOnlineRecordModel::getEndTime, fromTime);
        humanCondition.filterEqual(UserOnlineRecordModel::getStatus, AuthConstant.USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0);
        List<UserOnlineRecordModel> onlineList = getModelMapper().list(humanCondition);
        return onlineList;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOfflined(List<String> userIds) {
        // 默认超过5分钟没有更新end_time既判定为离线
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserOnlineRecordModel::getUser, userIds.toArray());
        }
        humanCondition.filterLessThan(UserOnlineRecordModel::getEndTime, fromTime);
        humanCondition.filterEqual(UserOnlineRecordModel::getStatus, AuthConstant.USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0);
        List<UserOnlineRecordModel> onlineList = getModelMapper().list(humanCondition);
        return onlineList;
    }

    /*
    @Override
    public List<UserOnlineRecordModel> listCurrentOnlinedUser() {
        List<UserOnlineRecordModel> list = getModelMapper().listOnlinedUserByPeriod(DEFAULT_MINUTES);
        return list;
    }


    @Override
    public int cntCurrentOnlinedUser() {
        return getModelMapper().cntOnlinedUser(DEFAULT_MINUTES);
    }

    @Override
    public List<UserOnlineRecordModel> listOnlinedUserByPeriod() {
        List<UserOnlineRecordModel> list = getModelMapper().listOnlinedUserByPeriod(DEFAULT_MINUTES);
        return list;
    }*/


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
        if (CollectionUtils.isEmpty(notOnlines)) {
            UserOnlineRecordModel model = new UserOnlineRecordModel();
            model.setPk(-1);
            notOnlines.add(model);
        }
        List<Integer> notOnlinePks = new ArrayList<>();
        for (UserOnlineRecordModel notOnline : notOnlines) {
            Integer userOnlineRecordModelPk = notOnline.getPk();
            notOnlinePks.add(userOnlineRecordModelPk);
        }
        HumanCondition onlineCondition = new HumanCondition()
                .filterNotIn(UserOnlineRecordModel::getPk, notOnlinePks.toArray())
                .select(UserOnlineRecordModel::getPk)
                .groupBy(UserOnlineRecordModel::getUser);
        List<UserOnlineRecordModel> onlines = getModelMapper().list(onlineCondition);
        if (CollectionUtils.isEmpty(onlines)) {
            return 0;
        }
        List<Integer> onlinePks = new ArrayList<>();
        for (UserOnlineRecordModel online : onlines) {
            Integer pk = online.getPk();
            onlinePks.add(pk);
        }
        HumanCondition countCondition = new HumanCondition()
                .filterIn(UserOnlineRecordModel::getPk, onlinePks.toArray())
                .selectAs(Count.f(UserOnlineRecordModel::getUser), "cnt");
        Map<String, Object> countMap = getModelMapper().originFirst(countCondition);
        Integer cnt = ((Long) countMap.get("cnt")).intValue();
        return cnt;
    }
}
