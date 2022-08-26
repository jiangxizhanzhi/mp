package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.dto.org.UserOrgMapDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineRecordMapper;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineRecordFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private OrgUserService orgUserService;

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
    public PageInfo<UserOnlineReportDto> recordSummaryPage(UserOnlineRecordSummaryPageRequest req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineRecordSummaryDto> recordSummaryDtoList = getModelMapper().listRecordSummary(req.getUserIds(), req.getStartTime(), req.getEndTime());
        PageInfo pageInfo = new PageInfo(recordSummaryDtoList);

        ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = heartbeatService.getHeartbeatMap();
        Map<String, Integer> heatbeatCntMap = new HashMap<String, Integer>();
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> entry : heartbeatMap.entrySet()) {
            if (Objects.nonNull(entry.getKey().getId()) && Objects.nonNull(entry.getValue())) {
                heatbeatCntMap.put(entry.getKey().getId(), entry.getValue().size());
            }
        }

        List<UserOnlineRecordSummaryDto> summaryDtos = pageInfo.getList();
        List<UserOnlineReportDto> reportDtos = new ArrayList<>();
        summaryDtos.forEach(r -> {
            UserOnlineReportDto reportDto = new UserOnlineReportDto();
            BeanUtils.copyProperties(r, reportDto);

            reportDto.setTotalOnlineHours(0.0);
            Long totalMins = r.getTotalOnlineMinutes();
            if (totalMins > 0) {
                double totalHrs = totalMins / 60.0;
                reportDto.setTotalOnlineHours(NumberUtil.round(totalHrs, 2).doubleValue());
            }

            // 只获取最新的一条日志
            OperationLogPageRequest logPageRequest = new OperationLogPageRequest();
            logPageRequest.setUserName(r.getUsername());
            logPageRequest.setPage(1);
            logPageRequest.setPerPage(1);
            List<OperationLogPageResponse> logs = userLogService.listOpLog(logPageRequest);
            if (CollectionUtil.isNotEmpty(logs) && Objects.nonNull(logs.get(0))) {
                // 动作
                reportDto.setAction(logs.get(0).getOpType());
                // 对象
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


        // orgpaths
        List<String> userIds = summaryDtos.stream().map(UserOnlineRecordSummaryDto::getUser).collect(Collectors.toList());
        List<UserOrgMapDto> userOrgMapDtos = orgUserService.listUserOrgMapDtosByUserIds(userIds);
        reportDtos.forEach(r -> {
            Optional<UserOrgMapDto> userOrgMapDtoOptional = userOrgMapDtos.stream().filter(mapDto -> Objects.equals(mapDto.getUserId(), r.getUser())).findFirst();
            if (userOrgMapDtoOptional.isPresent()) {
                r.setOrg(userOrgMapDtoOptional.get().getOrgPaths());
            }
        });

        // 替换为reportDto
        pageInfo.setList(reportDtos);
        return pageInfo;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOnlined(List<String> userIds) {
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        List<UserOnlineRecordModel> onlineList = getModelMapper().listCurrentOnlined(userIds, fromTime);
        return onlineList;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOfflined(List<String> userIds) {
        // 默认超过5分钟没有更新end_time既判定为离线
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(DEFAULT_MINUTES);
        List<UserOnlineRecordModel> list = getModelMapper().listCurrentOfflined(userIds,fromTime);
        return list;
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
        Integer cnt = getModelMapper().getOnlinedUserCntByPeriod(startTime, endTime);
        return cnt;
    }

    @Override
    public List<UserOnlineRecordModel> listOnlineHistoryTrackByUser(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<UserOnlineRecordModel> list = getModelMapper().listOnlineHistoryTrackByUser(userId, startTime, endTime);
        return list;
    }

    @Override
    @Cacheable(value = CACHE_KEY_USER_OL_CNT)
    public UserOnlineCntDto onlineUserCnt() {
        LocalDateTime now = LocalDateTime.now();
        int olcnt = 0;
        int mapcnt = heartbeatService.getHeartbeatMap().size();
        int dbcnt = getOnlinedUserCntByPeriod(now.minusMinutes(5), now);
        olcnt = Math.max(mapcnt, dbcnt);
        int totalUser = userService.totalUserCnt();

        UserOnlineCntDto userOnlineCntDto = new UserOnlineCntDto();
        userOnlineCntDto.setOnlineUser(olcnt);
        userOnlineCntDto.setTotalUser(totalUser);

        return userOnlineCntDto;
    }

}
