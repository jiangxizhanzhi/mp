package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MultiSceneClient {

    private final HttpClient httpClient;

    private final Integer TIMEOUT_TIME = 10000;

    private final static String REGISTER_HEARTBEAT = "/api/mp/cscene/multi-scene/receive/heartbeat";

    public MultiSceneClient() {
        httpClient = HttpClient.builder()
                .configTimeout(TIMEOUT_TIME).configMaxConnTotal(20).configMaxConnPerRoute(20)
                .build();
        httpClient.setLogRequest(true);
    }

    public void registerAndHeartbeat(String server, MultiSceneRegisterBo multiSceneRegisterBo) {
        try {
            ServiceResult<Boolean> result = httpClient.post(StringUtils.join(server, REGISTER_HEARTBEAT), multiSceneRegisterBo, null, null, TIMEOUT_TIME)
                    .snakeGetResultBean(new TypeReference<ServiceResult<Boolean>>() {
                    });
            if (!Boolean.TRUE.equals(result.getData())) {
                throw new MpRuntimeException("分靶场心跳失败");
            }
        } catch (Exception e) {
            log.error(String.format("分靶场心跳失败, 错误: %s", e.getMessage()), e);
            throw new MpRuntimeException("分靶场心跳失败");
        }
    }

    public <T extends SnakeHttpData, R, P extends SnakeHttpParam> R commonSnakePost(String url, T httpData, P httpParam, TypeReference<R> typeReference) {
        return httpClient.post(url, httpData, httpParam, null, TIMEOUT_TIME)
                    .snakeGetResultBean(typeReference);

    }

    public <T extends SnakeHttpData, R, P extends SnakeHttpParam> R commonSnakeDelete(String url, T httpData, P httpParam, TypeReference<R> typeReference) {
        return httpClient.delete(url, httpData, httpParam, null, TIMEOUT_TIME)
                .snakeGetResultBean(typeReference);

    }




}
