package cn.cyberpeace.mp.situationaware.bo;

import cn.cyberpeace.mp.situationaware.constant.*;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigModel;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName WarningRuleConfigBO
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 15:00
 **/
@Data
@Accessors(chain = true)
public class WarningRuleConfigBO {

    private String id;

    private String warningName;


    private WarningTargetEnum warningTargetEnum;

    private WarningClassEnum warningClassEnum;

    private List<WarningMovement> warningMovementList;

    private SystemTypeEnum systemTypeEnum;

    private Set<String> processNameSet;

    private Set<String> processDetailFeatureSet;

    private List<ProcessStatusEnum> processStatusList;

    private Set<String> filePathSet;

    private Set<String> fileSuffixSet;

    private BigDecimal fileMinSize;

    private Set<String> registerPathSet;

    public static WarningRuleConfigBO fromModel(WarningRuleConfigModel warningRuleConfigModel) {

        WarningTargetEnum warningTargetEnum = BaseKeyAndName.getByKey(warningRuleConfigModel.getWarningTarget(), WarningTargetEnum.class);
        WarningClassEnum warningClassEnum = BaseKeyAndName.getByKey(warningRuleConfigModel.getWarningClass(), WarningClassEnum.class);

        List<WarningMovement> warningMovements = Optional.of(warningRuleConfigModel.getWarningMovements()).map(str -> Arrays.asList(str.split(SuricataConstant.COMMA)))
                .orElse(Collections.emptyList()).stream().filter(StringUtils::isNotBlank).map(Integer::parseInt)
                .map(element -> BaseKeyAndName.getByKey(element, WarningMovement.class)).collect(Collectors.toList());

        SystemTypeEnum systemTypeEnum = BaseKeyAndName.getByKey(warningRuleConfigModel.getSystemType(), SystemTypeEnum.class);
        Set<String> processNameSet = getSetFromString(warningRuleConfigModel.getProcessNames());

        List<ProcessStatusEnum> processStatusEnumList = Optional.of(warningRuleConfigModel.getProcessStatusList()).map(str -> Arrays.asList(str.split(SuricataConstant.COMMA)))
                .orElse(Collections.emptyList()).stream().filter(StringUtils::isNotBlank).map(Integer::parseInt)
                .map(element -> BaseKeyAndName.getByKey(element, ProcessStatusEnum.class)).collect(Collectors.toList());

        Set<String> filePathSet = getSetFromString(warningRuleConfigModel.getFilePaths());
        Set<String> fileSuffixSet = getSetFromString(warningRuleConfigModel.getFileSuffixes());

        BigDecimal fileMinSize = StringUtils.isBlank(warningRuleConfigModel.getFileSizeMin()) ? BigDecimal.ZERO : new BigDecimal(warningRuleConfigModel.getFileSizeMin());
        Set<String> registerPathSet = getSetFromString(warningRuleConfigModel.getRegisterPaths());

        WarningRuleConfigBO warningRuleConfigBO = new WarningRuleConfigBO()
                .setId(warningRuleConfigModel.getId())
                .setWarningName(warningRuleConfigModel.getWarningName())
                .setWarningTargetEnum(warningTargetEnum)
                .setWarningClassEnum(warningClassEnum)
                .setWarningMovementList(warningMovements)
                .setSystemTypeEnum(systemTypeEnum)
                .setProcessNameSet(processNameSet)
                .setProcessStatusList(processStatusEnumList)
                .setFilePathSet(filePathSet)
                .setFileSuffixSet(fileSuffixSet)
                .setFileMinSize(fileMinSize)
                .setRegisterPathSet(registerPathSet);

        return warningRuleConfigBO;

    }

    public static WarningRuleConfigModel toModel(WarningRuleConfigBO warningRuleConfigBO) {
        WarningRuleConfigModel warningRuleConfigModel = new WarningRuleConfigModel();

        Integer warningTarget = Optional.ofNullable(warningRuleConfigBO.getWarningTargetEnum()).map(WarningTargetEnum::getKey).orElse(SuricataConstant.ZERO);
        Integer warningClass = Optional.ofNullable(warningRuleConfigBO.getWarningClassEnum()).map(WarningClassEnum::getKey).orElse(SuricataConstant.ZERO);
        String warningMovements = Optional.ofNullable(warningRuleConfigBO.getWarningMovementList()).orElse(new ArrayList<>())
                .stream().map(WarningMovement::getKey).map(String::valueOf).collect(Collectors.joining(SuricataConstant.COMMA));
        Integer systemType = Optional.ofNullable(warningRuleConfigBO.getSystemTypeEnum()).map(SystemTypeEnum::getKey).orElse(SuricataConstant.ZERO);
        String processNames = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getProcessNameSet()).orElse(Collections.emptySet()));
        String processStatusList = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getProcessStatusList()).orElse(Collections.emptyList())
                .stream().map(ProcessStatusEnum::getKey).map(String::valueOf).collect(Collectors.joining(SuricataConstant.COMMA)));
        String filePaths = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getFilePathSet()).orElse(Collections.emptySet()));
        String fileSuffixes = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getFileSuffixSet()).orElse(Collections.emptySet()));
        String fileMinSize = Optional.ofNullable(warningRuleConfigBO.getFileMinSize()).map(BigDecimal::toString).orElse(SuricataConstant.EMPTY_STR);
        String registerPaths = String.join(SuricataConstant.COMMA, Optional.ofNullable(warningRuleConfigBO.getRegisterPathSet()).orElse(Collections.emptySet()));

        warningRuleConfigModel.setId(warningRuleConfigBO.getId());
        warningRuleConfigModel.setWarningName(warningRuleConfigBO.getWarningName())
                .setWarningTarget(warningTarget)
                .setWarningClass(warningClass)
                .setWarningMovements(warningMovements)
                .setSystemType(systemType)
                .setProcessNames(processNames)
                .setProcessStatusList(processStatusList)
                .setFilePaths(filePaths)
                .setFileSuffixes(fileSuffixes)
                .setFileSizeMin(fileMinSize)
                .setRegisterPaths(registerPaths);
        return warningRuleConfigModel;
    }

    private static Set<String> getSetFromString(String input) {
        return Optional.of(input).map(str -> Arrays.asList(str.split(SuricataConstant.COMMA)))
                .orElse(Collections.emptyList()).stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }


}
