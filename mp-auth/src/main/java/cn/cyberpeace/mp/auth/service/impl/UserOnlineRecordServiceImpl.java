package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineRecordMapper;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineRecordFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.response.OperationLogPageResponse;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class UserOnlineRecordServiceImpl extends BaseModelServiceImpl<UserOnlineRecordModel, UserOnlineRecordMapper, UserOnlineRecordFields, QueryParams> implements UserOnlineRecordService {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserService userService;

    @Autowired
    public UserOnlineRecordServiceImpl(UserOnlineRecordMapper modelMapper, UserOnlineRecordFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }


    @Override
    public PageInfo<UserOnlineRecordSummaryDto> recordSummaryPage(UserOnlineRecordSummaryPageRequest req) {
        req.ensure();
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineRecordSummaryDto> recordSummaryList = getModelMapper().listRecordSummary(req.getUserId(), req.getStartTime(), req.getEndTime());
        PageInfo pageInfo = new PageInfo(recordSummaryList);

        List<UserOnlineRecordModel> onlineRecordModels = listCurrentOnlined(null);

        List<UserOnlineRecordSummaryDto> summaryDtos = pageInfo.getList();
        List<UserOnlineReportDto> reportDtos = new ArrayList<>();
        summaryDtos.forEach(r -> {
            UserOnlineReportDto reportDto = new UserOnlineReportDto();
            BeanUtils.copyProperties(r, reportDto);

            // orgpaths
            List<OrgModel> orgs = orgUserService.listOrgsByUserId(r.getUser());
            String orgpaths = orgs.stream().map(OrgModel::getPath).collect(Collectors.joining(","));
            reportDto.setOrg(orgpaths);

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

            boolean isOnline = onlineRecordModels.stream().filter(model -> model.getUser().equals(r.getUser())).findAny().isPresent();
            if (isOnline) {
                reportDto.setIsOnline(1);
            } else {
                reportDto.setIsOnline(0);
            }

            reportDtos.add(reportDto);
        });

        // 替换为reportDto
        pageInfo.setList(reportDtos);
        return pageInfo;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOnlined(String userId) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<UserOnlineRecordModel> onlineList = getModelMapper().listCurrentOnlined(userId, deadline);
        return onlineList;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOfflined(String userId) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<UserOnlineRecordModel> list = getModelMapper().listCurrentOfflined(userId, deadline);
        return list;
    }

    @Override
    public List<UserOnlineRecordModel> listCurrentOnlinedUser() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(5);
        List<UserOnlineRecordModel> list = getModelMapper().listOnlinedUserByPeriod(startTime, endTime);
        return list;
    }

    @Override
    public List<UserOnlineRecordModel> listOnlinedUserByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        List<UserOnlineRecordModel> list = getModelMapper().listOnlinedUserByPeriod(startTime, endTime);
        return list;
    }

    @Override
    public UserOnlineCntDto onlineUserCnt() {
        List<UserOnlineRecordModel> onlineRecordModels = listCurrentOnlinedUser();
        int olcnt = onlineRecordModels.size();
        int totalUser = userService.totalUserCnt();

        UserOnlineCntDto userOnlineCntDto = new UserOnlineCntDto();
        userOnlineCntDto.setOnlineUser(olcnt);
        userOnlineCntDto.setTotalUser(totalUser);

        return userOnlineCntDto;
    }

}
