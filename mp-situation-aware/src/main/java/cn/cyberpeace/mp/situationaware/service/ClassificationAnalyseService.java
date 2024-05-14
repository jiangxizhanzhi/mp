package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.situationaware.bo.AttackClassificationBO;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationFromJsonModel;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassificationAnalyseService {

    private static final String CLASSIFICATION_FILE_NAME = "suricata/classification.json";


    @Cacheable(value = "#root.targetClass.simpleName + '.' + #root.methodName")
    public FlowClassificationFromJsonModel getFlowClassificationInfoFromJson() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + CLASSIFICATION_FILE_NAME);
            String json = FileUtils.readLine(resources);
            return JsonUtils.parse(json, FlowClassificationFromJsonModel.class);
        } catch (IOException error) {
            throw new MpRuntimeException(CommonErrorCode.FILE_DOWNLOAD_FAILED.format(CLASSIFICATION_FILE_NAME));
        }
    }

    @Cacheable(value = "#root.targetClass.simpleName + '.' + #root.methodName")
    public Map<String, String> getFlowEventEnToCnMap() {
        FlowClassificationFromJsonModel flowClassificationFromJsonModel = getFlowClassificationInfoFromJson();
        return flowClassificationFromJsonModel.getClassifications().stream().flatMap(flowTypes -> flowTypes.getFlow_events().stream())
                .collect(Collectors.toMap(AttackClassificationBO.Classification::getName, AttackClassificationBO.Classification::getZh_desc, (a, b) -> a));

    }


}
