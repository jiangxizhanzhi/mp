package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res.ExternalNetworkIdRes;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res.ExternalNetworkInfoRes;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.CUSTOM_ERROR;

@Slf4j
public class Network extends CloudClient {

    /**
     * get
     *
     * @param id
     * @return
     */
    public NetworkData get(String id) {
        String url = String.format(CloudUrlConstant.NETWORK_GET_URL, id);
        CloudResponse<NetworkData> result = cloudGet(url, null, new TypeReference<>() {}, true);
        return result.getData();
    }

    /**
     * getExternalNetworkId
     *
     * @return
     */
    public String getExternalNetworkId() {
        CloudResponse<ExternalNetworkIdRes> response = cloudPost(CloudUrlConstant.GET_EXTERNAL_NETWORK, null, null, new TypeReference<CloudResponse<ExternalNetworkIdRes>>() {
        }, true);
        return response.getData().getExternalNetId();
    }

    /**
     * getExternalNetworkInfo
     *
     * @return
     */
    public ExternalNetworkInfoRes getExternalNetworkInfo() {
        try {
            String externalId = getExternalNetworkId();
            NetworkData networkData = get(externalId);
            ExternalNetworkInfoRes externalNetworkInfoRes = new ExternalNetworkInfoRes()
                    .setId(externalId)
                    .setCidr(networkData.getSubnet().getCidr())
                    .setGatewayIp(networkData.getSubnet().getGatewayIp())
                    .setName(networkData.getName());
            return externalNetworkInfoRes;
        } catch (Exception e) {
            log.error("获取外部网络信息异常", e);
            throw new MpRuntimeException(CUSTOM_ERROR.format("获取外部网络信息异常, 平台接口异常或者外部网络SN_external未设置子网subnet信息"));
        }
    }


    /**
     * getByName
     *
     * @param name
     * @return
     */
    public NetworkData getByName(String name) {
        NetworkQueryParam queryParam = new NetworkQueryParam();
        queryParam.setName(name);
        CloudResponse<CloudListData<NetworkData>> result = cloudGet(CloudUrlConstant.NETWORK_QUERY_URL, queryParam,
                new TypeReference<>() {}, true);

        CloudListData<NetworkData> listData = result.getData();
        if (listData != null && listData.getList() != null && !listData.getList().isEmpty()) {
            return listData.getList().get(0);
        } else {
            return null;
        }
    }

    /**
     * getAll
     *
     * @param limit
     * @param offset
     * @return
     */
    public List<NetworkData> getAll(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<NetworkData>> result = cloudGet(CloudUrlConstant.NETWORK_QUERY_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }
}
