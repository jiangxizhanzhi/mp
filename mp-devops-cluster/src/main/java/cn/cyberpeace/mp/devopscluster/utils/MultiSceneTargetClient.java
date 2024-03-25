package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.MultiSceneCommonTargetsQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MultiSceneTargetClient {

    private final HttpClient httpClient;

    private final Integer TIMEOUT_TIME = 10000;

    private final static String LIST_COMMON_TARGETS = "/api/mp/devops/cluster/target/common-targets";
    private final static String LIST_TARGETS_BY_FILTER = "/api/mp/devops/cluster/target/listAll";

    public MultiSceneTargetClient() {
        httpClient = HttpClient.builder()
                .configTimeout(TIMEOUT_TIME).configMaxConnTotal(20).configMaxConnPerRoute(20)
                .build();
    }

    public List<DeviceVO> listCommonTargets(String server, MultiSceneCommonTargetsQueryParams multiSceneCommonTargetsQueryParams) {
        try {
            multiSceneCommonTargetsQueryParams.setMultiSceneType(BaseSceneModel.MultiSceneType.COMMON_SCENE);
            return httpClient.get(StringUtils.join(server, LIST_COMMON_TARGETS), multiSceneCommonTargetsQueryParams, null, TIMEOUT_TIME)
                    .snakeGetResultBean(new TypeReference<ServiceResult<ListResult<DeviceVO>>>() {
                    }).getData().getList();
        } catch (Exception e) {
            log.error(String.format("查询分靶场公共靶标异常: %s", e.getMessage()), e);
            return Collections.emptyList();
        }
    }

    public List<Device> listNotCommonTargets(String server, TargetSceneRequest targetSceneRequest) {
        try {
            targetSceneRequest.setMultiSceneType(BaseSceneModel.MultiSceneType.COMMON_SCENE);
            return httpClient.get(StringUtils.join(server, LIST_TARGETS_BY_FILTER), targetSceneRequest, null, TIMEOUT_TIME)
                    .snakeGetResultBean(new TypeReference<ServiceResult<ListResult<Device>>>() {
                    }).getData().getList();
        } catch (Exception e) {
            log.error(String.format("查询分靶场非公共靶标异常:%s", e.getMessage()), e);
            return Collections.emptyList();
        }
    }

}
