package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiSceneClient {

    private final HttpClient httpClient;

    private final Integer TIMEOUT_TIME = 10000;

    private final static String REGISTER_HEARTBEAT = "/api/mp/cscene/multi-scene/receive/heartbeat";

    public MultiSceneClient(String server) {
        httpClient = HttpClient.builder().configServer(server)
                .configTimeout(TIMEOUT_TIME).configMaxConnTotal(20).configMaxConnPerRoute(20)
                .build();
    }

    public void registerAndHeartbeat(MultiSceneRegisterBo multiSceneRegisterBo) {
        try {
            ServiceResult<Boolean> result = httpClient.post(REGISTER_HEARTBEAT, multiSceneRegisterBo, null, null, TIMEOUT_TIME)
                    .snakeGetResultBean(new TypeReference<ServiceResult<Boolean>>() {
                    });
            if (!Boolean.TRUE.equals(result.getData())) {
                throw new MpRuntimeException("分靶场心跳失败");
            }
        } catch (Exception e) {
            log.error(String.format("分靶场心跳失败, 错误: %s", e.getMessage()), e);
        }
    }

}
