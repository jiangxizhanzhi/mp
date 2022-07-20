package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.front.admin.config.FlowMonitorConfig;
import cn.cyberpeace.mp.front.admin.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.front.admin.service.sys.SysNoticeService;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/19 11:18
 */
@Slf4j
@Service
public class HighRiskFlowNoticeService {

    @Autowired
    private FlowMonitorConfig flowMonitorConfig;

    @Autowired
    private SysNoticeService sysNoticeService;

    @Async
    public void sendNotice(String content) {
        log.info("content:{}", content);
        if (StringUtils.isNotBlank(content)) {
            sendSysNotice(content);
            sendMail(content);
        }
    }

    private void sendMail(String content) {
        FlowMonitorConfig.Mail mailConfig = flowMonitorConfig.getMail();
        if (Objects.isNull(mailConfig) || mailConfig.getEnabled()) {
            return;
        }

        MailAccount account = new MailAccount();

        if (StringUtils.isBlank(mailConfig.getHost()) || StringUtils.isBlank(mailConfig.getFrom()) ||
                StringUtils.isBlank(mailConfig.getUsername()) || StringUtils.isBlank(mailConfig.getPassword())) {
            log.error("mail config error!");
            return;
        }

        account.setHost(mailConfig.getHost());
        account.setPort(mailConfig.getPort());
        account.setAuth(true);
        account.setFrom(mailConfig.getFrom());
        account.setUser(mailConfig.getUsername());
        account.setPass(mailConfig.getPassword());

        MailUtil.send(account, mailConfig.getTo(), "场景流量监控", content, true);
        log.info("mail send success!");
    }


    private void sendSysNotice(String content) {
        FlowMonitorConfig.SysNotice sysNoticeConfig = flowMonitorConfig.getSysNotice();
        if (Objects.isNull(sysNoticeConfig) || sysNoticeConfig.getEnabled()) {
            return;
        }

        SysNoticeRequest noticeRequest = new SysNoticeRequest();
        noticeRequest.setContent(content);
        noticeRequest.setIsPublic(1);
        noticeRequest.setType(1);
        sysNoticeService.add(noticeRequest);
        log.info("sysnotice send success!");
    }


}
