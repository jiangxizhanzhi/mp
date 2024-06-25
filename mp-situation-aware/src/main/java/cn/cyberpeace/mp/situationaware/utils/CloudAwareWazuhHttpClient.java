package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import org.apache.http.NameValuePair;

import java.util.List;

public class CloudAwareWazuhHttpClient {


    /**
     * 根据云类型获取请求的基础URL。
     *
     * @param cloudType  云类型枚举值
     * @param requestUri 请求的URI部分
     * @return 完整的请求URL
     */
    public static String getRequestUrl(SituationAwareConfig situationAwareConfig, Integer cloudType, String requestUri) {
        String requestUrl;
        if (null != cloudType) {
            switch (cloudType) {
                case BaseSceneModel.CloudType.PUBLIC:
                    requestUrl = situationAwareConfig.getPublicWazuhBaseUrl();
                    break;
                case BaseSceneModel.CloudType.PRIVATE:
                    requestUrl = situationAwareConfig.getPrivateWazuhBaseUrl();
                    break;
                case BaseSceneModel.CloudType.MIX:
                    requestUrl = situationAwareConfig.getMixWazuhBaseUrl();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid cloudType: " + cloudType);
            }
        } else {
            requestUrl = situationAwareConfig.getWazuhBaseUrl();
        }

        return requestUrl + requestUri;
    }

    /**
     * 根据云环境类型执行HTTP GET请求并返回响应内容。
     * <p>
     * 此方法会根据提供的{@code cloudType}选择不同的HTTPS实用工具类来发送带有认证令牌的GET请求。
     * 如果{@code cloudType}为null，则默认使用{@link WazuhHttpsUtils#sendGetWithAuthToken}方法发送请求。
     *
     * @param cloudType  云环境类型枚举值，对应{@link BaseSceneModel.CloudType}中的PUBLIC、PRIVATE、MIX之一，或为null以使用默认处理方式。
     * @param requestUrl 完整的请求URL字符串。
     * @param params     GET请求的参数列表，类型为{@link NameValuePair}的列表。
     * @return 从服务器返回的响应内容字符串。
     * @throws IllegalArgumentException 如果提供了无效的{@code cloudType}值，且不为null。
     */
    public static String getRes(Integer cloudType, String requestUrl, List<NameValuePair> params) {
        String res;

        if (cloudType != null) {
            switch (cloudType) {
                case BaseSceneModel.CloudType.PUBLIC:
                    res = PublicWazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
                    break;
                case BaseSceneModel.CloudType.PRIVATE:
                    res = PrivateWazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
                    break;
                case BaseSceneModel.CloudType.MIX:
                    res = MixWazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid cloudType: " + cloudType);
            }
        } else {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        }

        return res;
    }

}
