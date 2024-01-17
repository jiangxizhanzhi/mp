package cn.cyberpeace.mp.frontadmin.service.cron;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.enums.NoticeTypeEnum;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 系统过期检查定时任务
 * @Author qiaokang
 * @Date：2023/8/21 17:17
 */
@Service
@Slf4j
public class SysDespireCronJob {

    //默认系统名称
    private static final String DEFAULT_SYS_NAME = "数字化靶场";

    //提醒标题
    private static final String LICENSE_REMIND_TITLE = "license到期提醒";

    //提醒内容
    private static final String LICENSE_REMIND_CONTENT = "%s的license授权将在%s天后到期，请及时处理";


    @Resource
    private SysActivationService activationService;

    @Resource
    private FrontAdminConfig frontAdminConfig;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private SysNoticeService noticeService;

    @Resource
    private UserMapper userMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void configureTasks() {
        log.info("start system activation task");
        //系统过期提醒开关需开启
        if (!Boolean.TRUE.equals(frontAdminConfig.isLicenseRemindEnable())) {
            log.info("System activation cron job is not enable");
            return;
        }

        check();
        log.info("end system activation task");
    }


    /**
     * @description: 检查是否到期
     * @throws
     * @return void
     */
    private void check(){
        ActivationResponse activationResponse = activationService.status();
        if(Objects.isNull(activationResponse)){
            log.error("system activation status not found!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_NONE);
        }
        LocalDate expiredDate = LocalDate.parse(activationResponse.getExpiredDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate now = LocalDate.now();
        // 计算两个日期之间的天数
        Long daysBetweenLong = Long.valueOf(ChronoUnit.DAYS.between(now, expiredDate));
        Integer daysBetween = Integer.valueOf(daysBetweenLong.toString());
        //配置的提醒过期天数
        Integer licenseRemindTime = frontAdminConfig.getLicenseRemindTime();
        if(daysBetween > licenseRemindTime || daysBetween < 0){
            // 没到提醒时间或者已经过期
            log.info("system activation remind time not reach");
            return;
        }
        //提醒处理
        remindAction(daysBetween);
    }

    /**
     * @description: 提醒处理
     * @param daysBetween 剩余到期天数
     * @return void
     */
    public void remindAction(Integer daysBetween) {
        //获取系统名称
        SysConfigModel sysConfigModel = sysConfigService.getByKeyName("system_name");
        String sysName = !Objects.isNull(sysConfigModel) && StringUtils.isNotEmpty(sysConfigModel.getValue())?sysConfigModel.getValue():DEFAULT_SYS_NAME;
        //激活到期前提醒内容
        String remindContent = String.format(LICENSE_REMIND_CONTENT, sysName, daysBetween);
        // 设置了延期提醒的用户不通知
        HumanCondition humanCondition = new HumanCondition()
                .link(UserModel.class, UserRoleModel.class)
                .filterLink(UserModel.class, UserModel::getId, UserRoleModel.class, UserRoleModel::getUser)
                .currentLink(UserModel.class)
                .filterLessThan(UserModel::getDelayRemindLicenseTime, LocalDateTime.now())
                .currentLink(UserRoleModel.class)
                .filterEqual(UserRoleModel::getRole, AuthConstant.ROLE_ADMIN_ID);
        List<UserModel> users = userMapper.list(humanCondition);

        List<String> userIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(users)) {
            List<String> list = new ArrayList<>();
            for (UserModel user : users) {
                String id = user.getId();
                list.add(id);
            }
            userIds = list;
        }
        SysNoticeRequest sysNoticeRequest =
                SysNoticeRequest.builder()
                        .title(LICENSE_REMIND_TITLE)
                        .content(remindContent)
                        .type(NoticeTypeEnum.LICENSE.getCode())
                        .isPublic(0)
                        .publisher(AuthConstant.ROOT_USER_ID)
                        .userIds(userIds).build();
        log.info("system activation remind notice: {}", sysNoticeRequest);
        //新增系统通过公告，提醒用户范围：具有管理员权限的用户均收到提醒
        noticeService.add(sysNoticeRequest);
    }
}
