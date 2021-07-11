package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.decoder.Decoder;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DecoderService extends RequestProcessor {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private AgentService agentService;

    
    public ListObjServiceResult<Decoder> pageDecoders(BasePageRequest pageRequest) {
        var uri = "/decoders";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(pageRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Decoder> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        List<Decoder> decoders = result.getData().getAffectedItems();

        return new ListObjServiceResult(new ListObjResult<>(decoders, result.getData().getTotalAffectedItems()).asResult()).asResult();
    }

}
