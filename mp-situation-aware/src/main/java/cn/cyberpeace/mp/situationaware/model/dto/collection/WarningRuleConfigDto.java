package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.bo.WarningRuleConfigBO;
import cn.cyberpeace.mp.situationaware.constant.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ApiModel("告警规则配置")
@Data
@Accessors(chain = true)
public class WarningRuleConfigDto {

    @ApiModelProperty("告警规则id")
    private String id;

    @ApiModelProperty("告警规则名称")
    private String warningName;

    @ApiModelProperty("告警类别")
    private String warningTarget;

    @ApiModelProperty("告警等级")
    private String warningClass;

    @ApiModelProperty("告警动作")
    private String warningMovements;

    @ApiModelProperty("系统类型")
    private String systemType;

    @ApiModelProperty("进程名称,多个,隔开")
    private String processNames;

    @ApiModelProperty("进程详细信息，多个,隔开")
    private String processDetailFeatures;

    @ApiModelProperty("进程状态")
    private List<String> processStatusList;

    @ApiModelProperty("文件路径，多个,隔开")
    private String filePaths;

    @ApiModelProperty("文件后缀，多个,隔开")
    private String fileSuffixes;

    @ApiModelProperty("文件最小值")
    private String fileMinSize;

    @ApiModelProperty("注册表路径，多个,隔开")
    private String registerPaths;

    public static WarningRuleConfigBO toBo(WarningRuleConfigDto warningRuleConfigDto) {
        WarningRuleConfigBO warningRuleConfigBO = new WarningRuleConfigBO()
                .setId(warningRuleConfigDto.getId())
                .setWarningName(warningRuleConfigDto.getWarningName())
                .setWarningTargetEnum(Optional.ofNullable(warningRuleConfigDto.getWarningTarget()).map(target -> BaseKeyAndName.getByName(target, WarningTargetEnum.class)).orElse(null))
                .setWarningClassEnum(Optional.ofNullable(warningRuleConfigDto.getWarningClass()).map(className -> BaseKeyAndName.getByName(className, WarningClassEnum.class)).orElse(null))
                .setWarningMovementList(Optional.ofNullable(warningRuleConfigDto.getWarningMovements()).map(moves -> Arrays.asList(moves.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).map(str -> BaseKeyAndName.getByName(str, WarningMovement.class)).filter(Objects::nonNull).collect(Collectors.toList()))
                .setSystemTypeEnum(Optional.ofNullable(warningRuleConfigDto.getSystemType()).map(system -> BaseKeyAndName.getByName(system, SystemTypeEnum.class)).orElse(null))
                .setProcessNameSet(Optional.ofNullable(warningRuleConfigDto.getProcessNames()).map(processNames -> Arrays.asList(processNames.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setProcessDetailFeatureSet(Optional.ofNullable(warningRuleConfigDto.getProcessDetailFeatures()).map(features -> Arrays.asList(features.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setProcessStatusList(Optional.ofNullable(warningRuleConfigDto.getProcessStatusList()).orElse(Collections.emptyList()).stream().map(status -> BaseKeyAndName.getByName(status, ProcessStatusEnum.class))
                        .collect(Collectors.toList()))
                .setFilePathSet(Optional.ofNullable(warningRuleConfigDto.getFilePaths()).map(filePaths -> Arrays.asList(filePaths.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setFileSuffixSet(Optional.ofNullable(warningRuleConfigDto.getFileSuffixes()).map(suffixes -> Arrays.asList(suffixes.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .setFileMinSize(StringUtils.isBlank(warningRuleConfigDto.getFileMinSize()) ? BigDecimal.ZERO : new BigDecimal(warningRuleConfigDto.getFileMinSize()))
                .setRegisterPathSet(Optional.ofNullable(warningRuleConfigDto.getRegisterPaths()).map(registerPaths -> Arrays.asList(registerPaths.split(SuricataConstant.COMMA))).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
        log.info("warningB0 = {}", warningRuleConfigBO);
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
        String registerPaths = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getRegisterPathSet()).orElse(Collections.emptySet()));

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
                .setFileMinSize(fileMinSize)
                .setRegisterPaths(registerPaths);
        log.info("warning dto = {}", warningRuleConfigDto);
        return warningRuleConfigDto;
    }
}
