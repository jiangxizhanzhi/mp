package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:17
 */
public class RequestProcessor {

    public List<NameValuePair> processRequestParams(BaseRequest baseRequest) {
        List<NameValuePair> params = defaultParams();

        if (Objects.isNull(baseRequest)) {
            return params;
        }

        if (Objects.nonNull(baseRequest.getLimit())) {
            params.add(new BasicNameValuePair("limit", String.valueOf(baseRequest.getLimit())));
        }

        if (Objects.nonNull(baseRequest.getOffset())) {
            params.add(new BasicNameValuePair("offset", String.valueOf(baseRequest.getOffset())));
        }

        if (StringUtils.isNotBlank(baseRequest.getSelect())) {
            params.add(new BasicNameValuePair("select", String.valueOf(baseRequest.getSelect())));
        }

        return params;
    }

    private List<NameValuePair> defaultParams() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("limit", "10"));
        return params;
    }

}
