package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RequestProcessor {

    public List<NameValuePair> processRequestParams(BasePageRequest pageRequest) {

        List<NameValuePair> params = new ArrayList<>();
        if (Objects.isNull(pageRequest)) {
            params = defaultParams();
            return params;
        }

        if (Objects.isNull(pageRequest.getPageSize()) || pageRequest.getPageSize() <= 0) {
            pageRequest.setPageSize(10);
        }

        if (Objects.isNull(pageRequest.getPage()) || pageRequest.getPage() < 1) {
            pageRequest.setPage(1);
        }

        if (Objects.nonNull(pageRequest.getPageSize())) {
            params.add(new BasicNameValuePair("limit", String.valueOf(pageRequest.getPageSize())));
            Integer offset = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
            params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        }

        if (ArrayUtil.isNotEmpty(pageRequest.getSelect())) {
            params.add(new BasicNameValuePair("select", Joiner.on(",").join(pageRequest.getSelect())));
        }

        if (ArrayUtil.isNotEmpty(pageRequest.getSort())) {
            params.add(new BasicNameValuePair("sort", Joiner.on(",").join(pageRequest.getSelect())));
        }

        return params;
    }

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
