package cn.cyberpeace.mp.situationaware.constant;

import com.google.common.cache.LoadingCache;

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
    public static LoadingCache<String, String> wazuhToken;

    /**
     * openstack network查询
     */
    public static String ArkimeNetworkId;
}
