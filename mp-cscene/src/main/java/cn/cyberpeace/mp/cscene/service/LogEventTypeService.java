package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.LogEventType;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 16:15
 */
@Slf4j
@Service
public class LogEventTypeService {

    public List<LogEventType> typeTrees(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        return mockTypes();
    }


    private List<LogEventType> mockTypes() {
        // 操作系统日志
        LogEventType osType = LogEventType.builder().name("操作系统日志").code("01").build();

        LogEventType windows = LogEventType.builder().name("Windows").code("0101").build();
        LogEventType linux = LogEventType.builder().name("Linux").code("0102").build();
        LogEventType cisco = LogEventType.builder().name("Cisco").code("0103").build();
        LogEventType hp = LogEventType.builder().name("HP").code("0104").build();
        LogEventType freebsd = LogEventType.builder().name("FreeBSD").code("0105").build();
        LogEventType junos = LogEventType.builder().name("Junos").code("0106").build();
        List<LogEventType> osSubTypes = Arrays.asList(windows, linux, cisco, hp, freebsd, junos);
        osType.setSubTypes(osSubTypes);


        // 应用系统日志
        LogEventType appType = LogEventType.builder().name("应用系统日志").code("02").build();

        LogEventType mailserver = LogEventType.builder().name("邮件服务器").code("0201").build();
        LogEventType webserver = LogEventType.builder().name("WEB服务器").code("0202").build();
        LogEventType database = LogEventType.builder().name("数据库").code("0203").build();
        LogEventType networkserver = LogEventType.builder().name("网络服务").code("0204").build();
        LogEventType app = LogEventType.builder().name("应用软件").code("0205").build();
        LogEventType cloud = LogEventType.builder().name("云").code("0206").build();
        LogEventType loadbalancer = LogEventType.builder().name("负载均衡").code("0206").build();
        LogEventType appframework = LogEventType.builder().name("应用框架").code("0206").build();
        List<LogEventType> appSubTypes = Arrays.asList(mailserver, webserver, networkserver, app, cloud, loadbalancer, appframework);
        appType.setSubTypes(appSubTypes);


        // 安全系统日志
        LogEventType secSysType = LogEventType.builder().name("安全系统日志").code("03").build();

        LogEventType av = LogEventType.builder().name("AV").code("0301").build();
        LogEventType fw = LogEventType.builder().name("FW").code("0302").build();
        LogEventType ds_ips = LogEventType.builder().name("DS/IPS").code("0303").build();
        LogEventType vpn = LogEventType.builder().name("VPN").code("0304").build();
        LogEventType other = LogEventType.builder().name("其他").code("0305").build();
        List<LogEventType> secSubTypes = Arrays.asList(av, fw, ds_ips, vpn, other);
        secSysType.setSubTypes(secSubTypes);

        return Arrays.asList(osType, appType, secSysType);
    }


}
