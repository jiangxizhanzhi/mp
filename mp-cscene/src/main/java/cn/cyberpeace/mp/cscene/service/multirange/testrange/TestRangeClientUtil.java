package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.http.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.scene.model.RangeModel;
import cn.cyberpeace.mp.scene.repository.RangeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TestRangeClientUtil {

    private static Map<String, HttpClient> httpClientCache = new ConcurrentHashMap<>();
    private static RangeMapper rangeMapper = BeanCommon.getBean(RangeMapper.class);
    static {
        List<RangeModel> rangeModelList = rangeMapper.list(new HumanCondition());
        for (RangeModel rangeModel : rangeModelList) {
            httpClientCache.put(rangeModel.getRangeName(), initiateHttpClient(rangeModel.getRangeIp()));
        }
    }

    public static HttpResult get(String range, String url, HttpParam param, Map<String, String> headers, Integer timeout) {

        var result = getHttpClient(range).request(HttpGet.class, url, null, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult post(String range, String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = getHttpClient(range).request(HttpPost.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult put(String range, String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = getHttpClient(range).request(HttpPut.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult patch(String range, String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = getHttpClient(range).request(HttpPatch.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult delete(String range, String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = getHttpClient(range).request(HttpDeleteWithBody.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    private static HttpClient getHttpClient(String rangeName) {
        HttpClient httpClient = httpClientCache.get(rangeName);
        if (Objects.nonNull(httpClient)) {
            TaskCommon.executeTask(() -> {
                HttpClient httpClientFromRange = getHttpClientFromRange(rangeName);
                httpClientCache.put(rangeName, httpClientFromRange);
            });
            return httpClient;
        } else {
            httpClient = getHttpClientFromRange(rangeName);
            httpClientCache.put(rangeName, httpClient);
            return httpClient;
        }
    }


    private static HttpClient getHttpClientFromRange(String rangeName) {
        List<RangeModel> rangeModelList = rangeMapper.list(new HumanCondition().filterEqual(RangeModel::getRangeName, rangeName));
        if (CollectionUtils.isEmpty(rangeModelList)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("靶场" + rangeName + "不存在"));
        } else {
            RangeModel rangeModel = rangeModelList.get(0);
            HttpClient httpClient = initiateHttpClient(rangeModel.getRangeIp());
            httpClientCache.put(rangeName, httpClient);
            return httpClient;
        }
    }

    private static HttpClient initiateHttpClient(String ip) {
        var httpClientBuilder = HttpClient.builder().configServer("http://" + ip + ":80");
        httpClientBuilder.configTimeout(10000);
        httpClientBuilder.configMaxConnPerRoute(10);
        httpClientBuilder.configMaxConnTotal(10);
        return httpClientBuilder.build();
    }



}
