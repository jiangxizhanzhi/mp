package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.CollectionTemplateProbeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/27 11:54
 */
//@Service
@Slf4j
@Deprecated
public class CollectionTemplateProbeService {

    @Autowired
    private CollectionTemplateProbeMapper templateProbeMapper;

    public List<CollectionTemplateProbeDto> listCollectionTemplateProbeDtods(String templateId, ProbeSearchCondition probeCondition) {
        List<CollectionTemplateProbeDto> list = templateProbeMapper.listCollectionTemplateProbeDtos(templateId, probeCondition);
        return list;
    }


}
