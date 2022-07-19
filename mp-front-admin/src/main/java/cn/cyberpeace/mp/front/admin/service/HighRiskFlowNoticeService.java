package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.front.admin.config.FlowMonitorConfig;
import cn.cyberpeace.mp.front.admin.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.front.admin.service.sys.SysNoticeService;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/19 11:18
 */
@Service
public class HighRiskFlowNoticeService {

    @Autowired
    private FlowMonitorConfig flowMonitorConfig;

    @Autowired
    private SysNoticeService sysNoticeService;

    @Async
    public void sendNotice(String content) {
        boolean sendNoticeSuccess = sendSysNotice(content);
        if(sendNoticeSuccess) {
            sendMail(content);
        }
    }

    private boolean sendMail(String content) {
        FlowMonitorConfig.Mail mailConfig = flowMonitorConfig.getMail();
        MailAccount account = new MailAccount();
        account.setHost(mailConfig.getHost());
        account.setPort(mailConfig.getPort());
        account.setAuth(true);
        account.setFrom(mailConfig.getFrom());
        account.setUser(mailConfig.getUsername());
        account.setPass(mailConfig.getPassword());

        MailUtil.send(account, mailConfig.getTo(), "场景流量监控", content, true);

        return true;
    }


    private boolean sendSysNotice(String content) {
        SysNoticeRequest noticeRequest = new SysNoticeRequest();
        noticeRequest.setContent(content);
        noticeRequest.setIsPublic(1);
        noticeRequest.setType(1);
        sysNoticeService.add(noticeRequest);

        return true;
    }




}
