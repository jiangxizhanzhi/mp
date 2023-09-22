package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.bo.WarningRuleConfigBO;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigFields;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigParams;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigQuery;
import cn.cyberpeace.mp.situationaware.repository.WarningRuleConfigMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName WarningRuleConfigService
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 9:18
 **/
@Slf4j
@Service
public class WarningRuleConfigService extends BaseModelServiceImpl<WarningRuleConfigModel, WarningRuleConfigMapper, WarningRuleConfigFields, WarningRuleConfigParams> {

    WarningRuleConfigService(WarningRuleConfigMapper warningRuleConfigMapper, WarningRuleConfigFields warningRuleConfigFields) {
        this.setModelMapper(warningRuleConfigMapper);
        this.setModelFields(warningRuleConfigFields);
    }



    public WarningRuleConfigDto getWarningRuleConfigById(String id) {
        ParamCheck.notBlank(id, "id");
        WarningRuleConfigModel warningRuleConfigModel = getModelMapper().get(id);
        if (Objects.nonNull(warningRuleConfigModel)) {
            WarningRuleConfigDto warningRuleConfigDto = WarningRuleConfigDto.fromModel(warningRuleConfigModel);
            return warningRuleConfigDto;
        } else {
            return new WarningRuleConfigDto();
        }
    }

    public void saveWarningRuleConfig(WarningRuleConfigDto warningRuleConfigDto) {
        if (Objects.nonNull(warningRuleConfigDto)) {
            WarningRuleConfigModel warningRuleConfigModel = WarningRuleConfigDto.toModel(warningRuleConfigDto);
            this.create(warningRuleConfigModel);
        }
    }

    public void updateWarningRuleConfig(WarningRuleConfigDto warningRuleConfigDto) {
        if (Objects.nonNull(warningRuleConfigDto)) {
            ParamCheck.notBlank(warningRuleConfigDto.getId(), "id");
            WarningRuleConfigModel warningRuleConfigModel = WarningRuleConfigDto.toModel(warningRuleConfigDto);
            this.update(warningRuleConfigModel);

        }
    }

    public ListObjServiceResult<WarningRuleConfigDto> pageWarningRuleConfig(WarningRuleConfigParams warningRuleConfigParams) {
        Integer warningTarget = warningRuleConfigParams.getWarningTarget();
        if (Objects.nonNull(warningTarget) && Objects.isNull(warningRuleConfigParams.getSearchFields())) {
            warningRuleConfigParams.setSearch(String.valueOf(warningTarget));
            warningRuleConfigParams.setSearchFields(new String[]{"warning_target"});
        }

        ListObjServiceResult<WarningRuleConfigModel> modelPageResult = this.list(warningRuleConfigParams);
        List<WarningRuleConfigModel> warningRuleConfigModels = Optional.ofNullable(modelPageResult.getSource()).map(ListObjResult::getObjs).orElse(Collections.emptyList());
        int total = modelPageResult.getSource().getTotal();

        ListObjServiceResult<WarningRuleConfigDto> dtoPageResult = new ListObjServiceResult<>();
        if (CollectionUtils.isNotEmpty(warningRuleConfigModels)) {
            List<WarningRuleConfigDto> warningRuleConfigDtoList = warningRuleConfigModels.stream()
                    .map(WarningRuleConfigDto::fromModel).collect(Collectors.toList());
            dtoPageResult.setSource(new ListObjResult<>(warningRuleConfigDtoList, total));
        } else {
            dtoPageResult.setSource(new ListObjResult<>(Collections.emptyList(), 0));
        }
        return dtoPageResult;
    }


}
