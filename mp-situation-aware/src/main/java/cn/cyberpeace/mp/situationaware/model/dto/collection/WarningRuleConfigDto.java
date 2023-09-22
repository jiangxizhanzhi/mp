package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.bo.WarningRuleConfigBO;
import cn.cyberpeace.mp.situationaware.constant.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName WarningRuleConfigDto
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 17:46
 **/
@Data
@Accessors(chain = true)
public class WarningRuleConfigDto {
    private String id;

    private String warningName;

    private String warningTarget;

    private String warningClass;

    private String warningMovements;

    private String systemType;

    private String processNames;

    private String processDetailFeatures;

    private List<String> processStatusList;

    private String filePaths;

    private String fileSuffixes;

    private String fileMinSize;

    public static WarningRuleConfigBO toBo(WarningRuleConfigDto warningRuleConfigDto) {
        WarningRuleConfigBO warningRuleConfigBO = new WarningRuleConfigBO()
                .setId(warningRuleConfigDto.getId())
                .setWarningName(warningRuleConfigDto.getWarningName())
                .setWarningTargetEnum(Optional.of(warningRuleConfigDto.getWarningTarget()).map(target -> BaseKeyAndName.getByName(target, WarningTargetEnum.class)).orElse(null))
                .setWarningClassEnum(Optional.of(warningRuleConfigDto.getWarningClass()).map(className -> BaseKeyAndName.getByName(className, WarningClassEnum.class)).orElse(null))
                .setWarningMovementList(Optional.of(warningRuleConfigDto.getWarningMovements()).map(moves -> Arrays.asList(moves.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).map(str -> BaseKeyAndName.getByName(str, WarningMovement.class)).collect(Collectors.toList()))
                .setSystemTypeEnum(Optional.of(warningRuleConfigDto.getSystemType()).map(system -> BaseKeyAndName.getByName(system, SystemTypeEnum.class)).orElse(null))
                .setProcessNameSet(Optional.of(warningRuleConfigDto.getProcessNames()).map(processNames -> Arrays.asList(processNames.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setProcessDetailFeatureSet(Optional.of(warningRuleConfigDto.getProcessDetailFeatures()).map(features -> Arrays.asList(features.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setProcessStatusList(Optional.of(warningRuleConfigDto.getProcessStatusList()).orElse(Collections.emptyList()).stream().map(status -> BaseKeyAndName.getByName(status, ProcessStatusEnum.class))
                        .collect(Collectors.toList()))
                .setFilePathSet(Optional.of(warningRuleConfigDto.getFilePaths()).map(filePaths -> Arrays.asList(filePaths.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setFileSuffixSet(Optional.of(warningRuleConfigDto.getFileSuffixes()).map(suffixes -> Arrays.asList(suffixes.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setFileMinSize(StringUtils.isBlank(warningRuleConfigDto.getFileMinSize()) ? BigDecimal.ZERO : new BigDecimal(warningRuleConfigDto.getFileMinSize()));
        return warningRuleConfigBO;
    }

    public static WarningRuleConfigDto fromBO(WarningRuleConfigBO warningRuleConfigBO) {

        String warningTarget = Optional.ofNullable(warningRuleConfigBO.getWarningTargetEnum()).map(WarningTargetEnum::getName).orElse(SuricataConstant.EMPTY_STR);
        String warningClass = Optional.ofNullable(warningRuleConfigBO.getWarningClassEnum()).map(WarningClassEnum::getName).orElse(SuricataConstant.EMPTY_STR);
        String warningMovements = Optional.ofNullable(warningRuleConfigBO.getWarningMovementList()).orElse(new ArrayList<>())
                .stream().map(WarningMovement::getName).collect(Collectors.joining(SuricataConstant.COMMA));
        String systemType = Optional.ofNullable(warningRuleConfigBO.getSystemTypeEnum()).map(SystemTypeEnum::getName).orElse(SuricataConstant.EMPTY_STR);
        String processNames = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getProcessNameSet()).orElse(Collections.emptySet()));
        List<String> processStatusList = Optional.ofNullable(warningRuleConfigBO.getProcessStatusList()).orElse(Collections.emptyList())
                .stream().map(ProcessStatusEnum::getName).collect(Collectors.toList());
        String filePaths = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getFilePathSet()).orElse(Collections.emptySet()));
        String fileSuffixes = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getFileSuffixSet()).orElse(Collections.emptySet()));
        String fileMinSize = Optional.ofNullable(warningRuleConfigBO.getFileMinSize()).map(BigDecimal::toString).orElse(SuricataConstant.EMPTY_STR);

        WarningRuleConfigDto warningRuleConfigDto = new WarningRuleConfigDto();
        warningRuleConfigDto.setId(warningRuleConfigBO.getId())
                .setWarningName(warningRuleConfigBO.getWarningName())
                .setWarningTarget(warningTarget)
                .setWarningClass(warningClass)
                .setWarningMovements(warningMovements)
                .setSystemType(systemType)
                .setProcessNames(processNames)
                .setProcessStatusList(processStatusList)
                .setFilePaths(filePaths)
                .setFileSuffixes(fileSuffixes)
                .setFileMinSize(fileMinSize);
        return warningRuleConfigDto;
    }
}
