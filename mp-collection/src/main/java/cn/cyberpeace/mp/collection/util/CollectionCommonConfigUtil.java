package cn.cyberpeace.mp.collection.util;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CollectionCommonConfigUtil {
    private CollectionCommonConfigUtil() {

    }

    private static final String CONFIG_PATH_SUFFIX = ".json";

    public static final String INTERNATIONAL_MESSAGE = "international_message";

    private static final String[] CONFIGS = new String[]{INTERNATIONAL_MESSAGE};

    private static volatile boolean LOADED = false;

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    public static String getConfigString(String config) {
        init();
        if (CONFIG_MAP.containsKey(config)) {
            return CONFIG_MAP.get(config);
        }
        return StringUtils.EMPTY;
    }

    private static synchronized void init() {
        if (LOADED) {
            return;
        }
        for (String config : CONFIGS) {
            try {
                Resource resource = new ClassPathResource("config/" + config + CONFIG_PATH_SUFFIX);
                String content = getContent();
                CONFIG_MAP.put(config, content);
            } catch (Exception e) {
                log.error("failed to read config : {}, {}", config, ExceptionUtils.getStackTrace(e));
            }
        }
        LOADED = true;
    }

    private static String getContent() {
        return "[{\"zh_name\":\"非可疑流量\",\"name\":\"Not Suspicious Traffic\",\"en_name\":\"Not Suspicious Traffic\"},{\"zh_name\":\"未知流量\",\"name\":\"Unknown Traffic\",\"en_name\":\"Unknown Traffic\"},{\"zh_name\":\"构造流量攻击\",\"name\":\"Potentially Bad Traffic\",\"en_name\":\"Potentially Bad Traffic\"},{\"zh_name\":\"信息泄露\",\"name\":\"Attempted Information Leak\",\"en_name\":\"Attempted Information Leak\"},{\"zh_name\":\"小规模信息泄漏\",\"name\":\"Information Leak\",\"en_name\":\"Information Leak\"},{\"zh_name\":\"大规模信息泄露\",\"name\":\"Large Scale Information Leak\",\"en_name\":\"Large Scale Information Leak\"},{\"zh_name\":\"尝试拒绝服务攻击\",\"name\":\"Attempted Denial of Service\",\"en_name\":\"Attempted Denial of Service\"},{\"zh_name\":\"成功拒绝服务攻击\",\"name\":\"Denial of Service\",\"en_name\":\"Denial of Service\"},{\"zh_name\":\"提权攻击尝试\",\"name\":\"Attempted User Privilege Gain\",\"en_name\":\"Attempted User Privilege Gain\"},{\"zh_name\":\"提权攻击失败\",\"name\":\"Unsuccessful User Privilege Gain\",\"en_name\":\"Unsuccessful User Privilege Gain\"},{\"zh_name\":\"提权攻击成功\",\"name\":\"Successful User Privilege Gain\",\"en_name\":\"Successful User Privilege Gain\"},{\"zh_name\":\"ROOT提权攻击尝试\",\"name\":\"Attempted Administrator Privilege Gain\",\"en_name\":\"Attempted Administrator Privilege Gain\"},{\"zh_name\":\"ROOT提权攻击成功\",\"name\":\"Successful Administrator Privilege Gain\",\"en_name\":\"Successful Administrator Privilege Gain\"},{\"zh_name\":\"远程桌面连接\",\"name\":\"Decode of an RPC Query\",\"en_name\":\"Decode of an RPC Query\"},{\"zh_name\":\"代码注入\",\"name\":\"Executable code was detected\",\"en_name\":\"Executable code was detected\"},{\"zh_name\":\"可疑字符串\",\"name\":\"A suspicious string was detected\",\"en_name\":\"A suspicious string was detected\"},{\"zh_name\":\"文件攻击\",\"name\":\"A suspicious filename was detected\",\"en_name\":\"A suspicious filename was detected\"},{\"zh_name\":\"异常登录\",\"name\":\"An attempted login using a suspicious username was detected\",\"en_name\":\"An attempted login using a suspicious username was detected\"},{\"zh_name\":\"系统调用检测\",\"name\":\"A system call was detected\",\"en_name\":\"A system call was detected\"},{\"zh_name\":\"异常链接\",\"name\":\"A TCP connection was detected\",\"en_name\":\"A TCP connection was detected\"},{\"zh_name\":\"木马攻击\",\"name\":\"A Network Trojan was detected\",\"en_name\":\"A Network Trojan was detected\"},{\"zh_name\":\"异常客户端攻击\",\"name\":\"A client was using an unusual port\",\"en_name\":\"A client was using an unusual port\"},{\"zh_name\":\"网络扫描\",\"name\":\"Detection of a Network Scan\",\"en_name\":\"Detection of a Network Scan\"},{\"zh_name\":\"拒绝服务攻击\",\"name\":\"Detection of a Denial of Service Attack\",\"en_name\":\"Detection of a Denial of Service Attack\"},{\"zh_name\":\"检测非标准协议或事件\",\"name\":\"Detection of a non-standard protocol or event\",\"en_name\":\"Detection of a non-standard protocol or event\"},{\"zh_name\":\"通用协议命令解码\",\"name\":\"Generic Protocol Command Decode\",\"en_name\":\"Generic Protocol Command Decode\"},{\"zh_name\":\"web漏洞尝试利用\",\"name\":\"access to a potentially vulnerable web application\",\"en_name\":\"access to a potentially vulnerable web application\"},{\"zh_name\":\"Web应用攻击\",\"name\":\"Web Application Attack\",\"en_name\":\"Web Application Attack\"},{\"zh_name\":\"其他攻击\",\"name\":\"Misc activity\",\"en_name\":\"Misc activity\"},{\"zh_name\":\"Misc攻击\",\"name\":\"Misc Attack\",\"en_name\":\"Misc Attack\"},{\"zh_name\":\"ICMP攻击\",\"name\":\"Generic ICMP event\",\"en_name\":\"Generic ICMP event\"},{\"zh_name\":\"色情内容\",\"name\":\"SCORE! Get the lotion!\",\"en_name\":\"SCORE! Get the lotion!\"},{\"zh_name\":\"违反隐私策略\",\"name\":\"Potential Corporate Privacy Violation\",\"en_name\":\"Potential Corporate Privacy Violation\"},{\"zh_name\":\"密码爆破\",\"name\":\"Attempt to login by a default username and password\",\"en_name\":\"Attempt to login by a default username and password\"},{\"zh_name\":\"恶意行为\",\"name\":\"Targeted Malicious Activity was Detected\",\"en_name\":\"Targeted Malicious Activity was Detected\"},{\"zh_name\":\"恶意工具包\",\"name\":\"Exploit Kit Activity Detected\",\"en_name\":\"Exploit Kit Activity Detected\"},{\"zh_name\":\"外部域名解析\",\"name\":\"Device Retrieving External IP Address Detected\",\"en_name\":\"Device Retrieving External IP Address Detected\"},{\"zh_name\":\"被感染域\",\"name\":\"Domain Observed Used for C2 Detected\",\"en_name\":\"Domain Observed Used for C2 Detected\"},{\"zh_name\":\"无用程序\",\"name\":\"Possibly Unwanted Program Detected\",\"en_name\":\"Possibly Unwanted Program Detected\"},{\"zh_name\":\"凭证窃取\",\"name\":\"Successful Credential Theft Detected\",\"en_name\":\"Successful Credential Theft Detected\"},{\"zh_name\":\"社会工程\",\"name\":\"Possible Social Engineering Attempted\",\"en_name\":\"Possible Social Engineering Attempted\"},{\"zh_name\":\"挖矿病毒\",\"name\":\"Crypto Currency Mining Activity Detected\",\"en_name\":\"Crypto Currency Mining Activity Detected\"}]";
    }

}
