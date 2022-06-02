package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.common.CollectionApp;
import cn.cyberpeace.mp.cscene.dto.common.CollectionType;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 16:15
 */
@Slf4j
@Service
public class CollectionTypeService {

    public List<CollectionType> typeTrees(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        List<CollectionType> types = new ArrayList<>();
        types.add(changeEvent("01", null));
        types.add(logEvent("02", CollectionType.TYPE_LOG_EVENT));
        types.add(customCollect("03", CollectionType.TYPE_CUSTOM_COLLECT));

        return types;
    }


    public List<CollectionApp> getAppNames(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);

        CollectionApp appName1 = new CollectionApp();
        appName1.setName("ssh");
        appName1.setCode("001");

        CollectionApp appName2 = new CollectionApp();
        appName2.setName("macfee");
        appName2.setCode("002");

        return Arrays.asList(appName1, appName2);
    }

    /**
     * 变更事件
     * @return
     */
    private CollectionType changeEvent(String code, Integer type) {
        CollectionType changeEvent = CollectionType.builder().name("变更事件").code(code).type(-1).build();

        CollectionType fileChangeEvent = CollectionType.builder().name("文件变更事件").code(code + "01").type(CollectionType.TYPE_CHANGE_EVENT_FILE).build();
        CollectionType registryChangeEvent = CollectionType.builder().name("注册表变更事件").code(code + "02").type(CollectionType.TYPE_CHANGE_EVENT_REGISTRY).build();
        CollectionType sourceLogEvent = CollectionType.builder().name("原始日志").code(code + "03").type(CollectionType.TYPE_LOG_FILE).build();

        changeEvent.setChildren(Arrays.asList(fileChangeEvent, registryChangeEvent, sourceLogEvent));
        return changeEvent;
    }

    /**
     * 自定义采集
     * @return
     */
    private CollectionType customCollect(String code, Integer type) {
        CollectionType customCollect = CollectionType.builder().name("自定义采集").code(code).type(type).build();

        return customCollect;
    }


    /**
     * 日志事件
     * @return
     */
    private CollectionType logEvent(String code, Integer type) {
        CollectionType logEvent = CollectionType.builder().name("日志事件").code(code).type(-1).build();
        CollectionType osType = logEventOSType(code + "01", type);
        CollectionType app = logEventApp(code + "02", type);
        CollectionType securitySystem = logEventSecuritySystem(code + "03", type);
        logEvent.setChildren(Arrays.asList(osType, app, securitySystem));
        return logEvent;
    }

    /**
     * 日志事件/操作系统日志
     * @return
     */
    private CollectionType logEventOSType(String code, Integer type) {
        CollectionType osType = CollectionType.builder().name("操作系统日志").code(code).type(-1).build();

        CollectionType windows = CollectionType.builder().name("Windows").code(code + "01").type(type).build();
        CollectionType linux = CollectionType.builder().name("Linux").code(code + "02").type(type).build();
        CollectionType cisco = CollectionType.builder().name("Cisco").code(code + "03").type(type).build();
        CollectionType hp = CollectionType.builder().name("HP").code(code + "04").type(type).build();
        CollectionType freebsd = CollectionType.builder().name("FreeBSD").code(code + "05").type(type).build();
        CollectionType junos = CollectionType.builder().name("Junos").code(code + "06").type(type).build();
        List<CollectionType> osSubTypes = Arrays.asList(windows, linux, cisco, hp, freebsd, junos);
        osType.setChildren(osSubTypes);

        return osType;
    }


    /**
     * 日志事件/应用系统日志
     * @param code
     * @param type
     * @return
     */
    private CollectionType logEventApp(String code, Integer type) {
        CollectionType appType = CollectionType.builder().name("应用系统日志").code(code).type(-1).build();

        CollectionType mailserver = CollectionType.builder().name("邮件服务器").code(code + "01").type(type).build();
        CollectionType webserver = CollectionType.builder().name("WEB服务器").code(code + "02").type(type).build();
        CollectionType database = CollectionType.builder().name("数据库").code(code + "03").type(type).build();
        CollectionType networkserver = CollectionType.builder().name("网络服务").code(code + "04").type(type).build();
        CollectionType app = CollectionType.builder().name("应用软件").code(code + "05").type(type).build();
        CollectionType cloud = CollectionType.builder().name("云").code(code + "06").type(type).build();
        CollectionType loadbalancer = CollectionType.builder().name("负载均衡").code(code + "07").type(type).build();
        CollectionType appframework = CollectionType.builder().name("应用框架").code(code + "08").type(type).build();
        List<CollectionType> appSubTypes = Arrays.asList(mailserver, webserver, database, networkserver, app, cloud, loadbalancer, appframework);
        appType.setChildren(appSubTypes);

        return appType;
    }


    /**
     * 日志事件/安全系统日志
     */
    private CollectionType logEventSecuritySystem(String code, Integer type) {
        CollectionType secSysType = CollectionType.builder().name("安全系统日志").code(code).type(-1).build();

        CollectionType av = CollectionType.builder().name("AV").code(code + "01").type(type).build();
        CollectionType fw = CollectionType.builder().name("FW").code(code + "02").type(type).build();
        CollectionType ds_ips = CollectionType.builder().name("DS/IPS").code(code + "03").type(type).build();
        CollectionType vpn = CollectionType.builder().name("VPN").code(code + "04").type(type).build();
        CollectionType other = CollectionType.builder().name("其他").code(code + "05").type(type).build();
        List<CollectionType> secSubTypes = Arrays.asList(av, fw, ds_ips, vpn, other);
        secSysType.setChildren(secSubTypes);

        return secSysType;
    }


}
