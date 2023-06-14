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
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: cn.cyberpeace.mp.frontadmin.service.cron
 * @Author: hhuang
 * @CreateTime: 2023/05/23  21:02
 * @Description: 系统过期检查定时任务
 * @Version: 1.0
 */
@Component
@Slf4j
@EnableScheduling
public class SysDespireCronJob implements SchedulingConfigurer {

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

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //系统过期提醒开关需开启
        if (!Boolean.TRUE.equals(frontAdminConfig.isLicenseRemindEnable())) {
            log.info("System expiration cron job is not enable");
            return;
        }
        taskRegistrar.addTriggerTask(new TriggerTask(() -> {
            //执行检查提醒
            check();
        }, triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(frontAdminConfig.getCronJobExpression());
            return cronTrigger.nextExecutionTime(triggerContext);
        }));
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
        if(daysBetween > licenseRemindTime){
            //没到时间 do nothing
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
        var humanCondition = new HumanCondition()
                .link(UserModel.class, UserRoleModel.class)
                .filterLink(UserModel.class, UserModel::getId, UserRoleModel.class, UserRoleModel::getUser)
                .currentLink(UserModel.class)
                .filterEqual(UserModel::getDelayRemindLicenseTime, -1)
                .currentLink(UserRoleModel.class)
                .filterEqual(UserRoleModel::getRole, AuthConstant.ROLE_ADMIN_ID);
        var users = userMapper.list(humanCondition);
        List<String> userIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(users)) {
            userIds = users.stream().map(UserModel::getId).collect(Collectors.toList());
        }
        SysNoticeRequest sysNoticeRequest =
                SysNoticeRequest.builder()
                        .title(LICENSE_REMIND_TITLE)
                        .content(remindContent)
                        .type(NoticeTypeEnum.LICENSE.getCode())
                        .isPublic(0)
                        .publisher(AuthConstant.ROOT_USER_ID)
                        .userIds(userIds).build();
        //新增系统通过公告，提醒用户范围：具有管理员权限的用户均收到提醒
        noticeService.add(sysNoticeRequest);
    }
}
