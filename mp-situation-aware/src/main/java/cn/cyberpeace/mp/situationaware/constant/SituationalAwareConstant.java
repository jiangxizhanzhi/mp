package cn.cyberpeace.mp.situationaware.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.situationaware.service.wazuh.SecurityService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 态势感知全局缓存
 */
public class SituationalAwareConstant {
    /**
     * 分组id url 分组
     */
    public static LoadingCache<String, String> groupIdSessionUrlCache;

    /**
     * 分组id
     */
    public static LoadingCache<String, String> wazuhToken =
            CacheBuilder.newBuilder()
                    .maximumSize(1)
                    .expireAfterWrite(14, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            String token = BeanCommon.getBean(SecurityService.class).authenticate();
                            return token;
                        }
                    });

    /**
     * openstack network查询
     */
    public static String ArkimeNetworkId;

    public static Integer ES_DOC_MAXCOUNT = 10000;

    public static String ERROR_CODE_PREFIX = "situationaware.error_code.";

    public static List<String> monitorRuleIds = Lists.newArrayList("5715", "5502");
}
