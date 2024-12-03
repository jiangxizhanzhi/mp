package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
public class MailService {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    public void sendMail(StringBuffer content) {
        String to = situationAwareConfig.getTarget();
        String from = situationAwareConfig.getUsername();
        String host = situationAwareConfig.getHost();
        String password = situationAwareConfig.getPassword();
        String sendname = situationAwareConfig.getSendname();
        String title = situationAwareConfig.getTitle();


        // 获取系统属性
        //Properties properties = System.getProperties();
        Properties properties = new Properties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        // 邮件发送协议
        properties.setProperty("mail.transport.protocol", "smtp");
        //是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）
//        properties.setProperty("mail.debug","true");
        properties.setProperty("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            @Override
            public PasswordAuthentication getPasswordAuthentication()
            {
                //发件人邮件用户名、授权码，换成自己的发件箱及授权码
                return new PasswordAuthentication(from, password);
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From，设置发件人
            InternetAddress fromMail = new InternetAddress(from);
            //设置发件人名称，TODO 换成自己的发件箱
            fromMail.setPersonal(MimeUtility.encodeText(sendname));
            message.setFrom(fromMail);

            // Set To: 设置收件人
            InternetAddress toMail = new InternetAddress(to);
            // TODO 换成自己的收件箱
//            InternetAddress toMail2 = new InternetAddress("858379880@qq.com");
            //发一个邮箱
            message.setRecipient(Message.RecipientType.TO, toMail);

            //发多个邮箱
//            Address[] allRecipients = {toMail, toMail2};
//            message.setRecipients(Message.RecipientType.TO, allRecipients);

            // Set Subject: 邮件主体
            message.setSubject(title);

            // 设置消息体
            message.setSentDate(new Date());

            // 指定为混合关系
            MimeMultipart msgMultipart = new MimeMultipart("mixed");
            message.setContent(msgMultipart);

            // 邮件信息组装
            //组装的顺序非常重要，一定要先组装文本域，再组装文件
            MimeBodyPart htmlPart = new MimeBodyPart();
            // 组装内容
            htmlPart.setContent(content.toString(), "text/html;charset=UTF-8");
            msgMultipart.addBodyPart(htmlPart);

            /*// 组装附件
            MimeBodyPart filePart = new MimeBodyPart();
            //TODO 换成自己的附件地址
            FileDataSource file_datasource = new FileDataSource("F:\\resource\\xxx.zip");
            DataHandler dh = new DataHandler(file_datasource);
            filePart.setDataHandler(dh);
            // 附件区别内嵌内容的一个特点是有文件名，为防止中文乱码要编码
            filePart.setFileName(MimeUtility.encodeText(dh.getName()));
            msgMultipart.addBodyPart(filePart);*/

            message.saveChanges();
            Transport.send(message);
            log.error("邮件发送成功");
        }catch (MessagingException | UnsupportedEncodingException mex) {
            log.error("邮件发送失败："+mex.getMessage());
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }

}

