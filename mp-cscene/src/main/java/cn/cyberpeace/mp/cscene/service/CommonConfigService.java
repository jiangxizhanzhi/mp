package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.CsceneCommonConfigModel;
import cn.cyberpeace.mp.cscene.repository.CsceneCommonConfigMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data
public class CommonConfigService {

    private final static String SYSTEM_TYPE_TO_AGENT_SUPPORT = "systemTypeToAgentSupport";

    @Resource
    private CsceneCommonConfigMapper csceneCommonConfigMapper;

    public ServiceResult<Map<String, Object>> getSystemTypeToAgentSupport() {
        var result = csceneCommonConfigMapper.first(new HumanCondition().filterEqual(CsceneCommonConfigModel::getKey, SYSTEM_TYPE_TO_AGENT_SUPPORT));
        if (result != null) {
            var map = JsonCommon.parseMap(result.getContent());
            return new ServiceResult<>(map);
        }
        return new ServiceResult<>(new HashMap<>());
    }
}
