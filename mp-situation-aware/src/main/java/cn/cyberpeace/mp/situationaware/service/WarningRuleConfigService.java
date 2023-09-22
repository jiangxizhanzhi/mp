package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.bo.WarningRuleConfigBO;
import cn.cyberpeace.mp.situationaware.constant.WarningMovement;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigRequest;
import cn.cyberpeace.mp.situationaware.repository.WarningRuleConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName WarningRuleConfigService
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 9:18
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class WarningRuleConfigService {

    private final WarningRuleConfigMapper warningRuleConfigMapper;

    public WarningRuleConfigDto getWarningRuleConfigById(String id) {
        ParamCheck.notBlank(id, "id");
        WarningRuleConfigModel warningRuleConfigModel = warningRuleConfigMapper.get(id);
        if (Objects.nonNull(warningRuleConfigModel)) {
            WarningRuleConfigBO warningRuleConfigBO = WarningRuleConfigBO.fromModel(warningRuleConfigModel);
            WarningRuleConfigDto warningRuleConfigDto = WarningRuleConfigDto.fromBO(warningRuleConfigBO);
            return warningRuleConfigDto;
        } else {
            return new WarningRuleConfigDto();
        }
    }

    public void saveOrUpdateWarningRuleConfig(WarningRuleConfigDto warningRuleConfigDto) {
        if (Objects.nonNull(warningRuleConfigDto)) {
            WarningRuleConfigBO warningRuleConfigBO = WarningRuleConfigDto.toBo(warningRuleConfigDto);
            WarningRuleConfigModel warningRuleConfigModel = WarningRuleConfigBO.toModel(warningRuleConfigBO);
            warningRuleConfigMapper.save(warningRuleConfigModel);
        }
    }


}
