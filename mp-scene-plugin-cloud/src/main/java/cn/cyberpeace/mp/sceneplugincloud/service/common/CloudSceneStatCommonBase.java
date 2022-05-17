package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.StatCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class CloudSceneStatCommonBase<P1 extends BasePluginSceneElementMapper<?>,
        P2 extends BasePluginSceneElementPortMapper<?>> {

    /**
     * count element方法
     */
    public static final Map<String, Function<List<Object>, Object>> CALC_COUNT_ELEMENT_FIELD_CALC_MAP = Map.of(
            BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getFirewallRules),
            fieldValues -> {
                var count = 0;
                for (var rawFieldValue : fieldValues) {
                    var fieldValue = (String) rawFieldValue;
                    if (StringUtils.isEmpty(fieldValue)) {
                        continue;
                    }
                    List<FirewallRule> firewallRules = JsonCommon.parse(fieldValue, new TypeReference<List<FirewallRule>>() {});
                    count += firewallRules.size();
                }
                return count;
            }
    );

    /**
     * count port方法
     */
    public static final Map<String, Function<List<Object>, Object>> CALC_COUNT_PORT_FIELD_CALC_MAP = Map.of(
            BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getFirewallRules),
            fieldValues -> {
                var count = 0;
                for (var rawFieldValue : fieldValues) {
                    var fieldValue = (String) rawFieldValue;
                    if (StringUtils.isEmpty(fieldValue)) {
                        continue;
                    }
                    List<FirewallRule> firewallRules = JsonCommon.parse(fieldValue, new TypeReference<List<FirewallRule>>() {});
                    count += firewallRules.size();
                }
                return count;
            }
    );

    @Setter
    @Getter
    private P1 cloudElementMapper;
    @Setter
    @Getter
    private P2 cloudElementPortMapper;

    protected abstract HumanCondition genNodeCondition(List<String> sceneIds, List<String> nodeIds);

    protected abstract HumanCondition genNodePortCondition(List<String> sceneIds, List<String> nodeIds);

    /**
     * 分组查询指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param groupFieldName 分组聚合字段
     * @param countFieldNames 统计字段
     * @param distinct 是否去重
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合统计数量 Map<字段名, Map<分组字段值, 该字段值聚合结果>>
     */
    public <T> Map<String, Map<T, Long>> groupCountElement(List<String> sceneIds, List<String> nodeIds,
                                                           String groupFieldName, List<String> countFieldNames,
                                                           boolean distinct) {
        var condition = genNodeCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementMapper().getModelClass());

        Map<String, Map<T, Long>> result;
        try {
            result = StatCommon.groupCount(getCloudElementMapper(), condition, groupFieldName, countFieldNames,
                    distinct);
        } catch (Exception e) {
            log.error("Group count cloud element on field {} error: {}", groupFieldName, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return result;
    }

    /**
     * 分组查询节点指定字段的聚合统计数量, 需要自定义计算结果
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param groupFieldName 分组聚合字段
     * @param fieldNames 聚合统计字段 有效字段 CALC_COUNT_ELEMENT_FIELD_CALC_MAP
     * @return 字段的聚合统计数量 Map<字段名, Map<分组字段值, 该字段值聚合结果>>
     */
    public <T> Map<String, Map<T, Long>> groupCalcCountElement(List<String> sceneIds, List<String> nodeIds,
                                                               String groupFieldName, List<String> fieldNames) {
        var fieldCalcMap = fieldNames.stream()
                .collect(Collectors.toMap(fieldName -> fieldName,
                         fieldName -> CALC_COUNT_ELEMENT_FIELD_CALC_MAP.getOrDefault(fieldName, List::size)));
        var condition = genNodeCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementMapper().getModelClass());
        Map<String, Map<T, Object>> result;
        try {
            result = StatCommon.groupCalc(getCloudElementMapper(), condition, groupFieldName, fieldCalcMap);
        } catch (Exception e) {
            log.error("Group calc count cloud element on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        Map<String, Map<T, Long>> formatResult = new HashMap<>();
        result.forEach((fieldName, fieldGroupValues) -> {
            Map<T, Long> fieldGroupCalcValue = new HashMap<>();
            formatResult.put(fieldName, fieldGroupCalcValue);
            fieldGroupValues.forEach((groupValue, groupCalcValue) -> {
                fieldGroupCalcValue.put(groupValue, Long.valueOf(groupCalcValue.toString()));
            });
        });
        return formatResult;
    }

    /**
     * 查询网元指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段
     * @param distinct 是否去重
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public Map<String, Long> countElement(List<String> sceneIds, List<String> nodeIds, List<String> fieldNames,
                                          boolean distinct) {
        var condition = genNodeCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementMapper().getModelClass());
        Map<String, Long> result;
        try {
            result = StatCommon.count(getCloudElementMapper(), condition, fieldNames, distinct);
        } catch (Exception e) {
            log.error("Count cloud element on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return result;
    }

    /**
     * 查询网元指定字段的聚合统计数量, 需要自定义计算结果
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段 有效字段 CALC_COUNT_ELEMENT_FIELD_CALC_MAP
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public Map<String, Long> calcCountElement(List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        var fieldCalcMap = fieldNames.stream()
                .collect(Collectors.toMap(fieldName -> fieldName,
                        fieldName -> CALC_COUNT_ELEMENT_FIELD_CALC_MAP.getOrDefault(fieldName, List::size)));
        var condition = genNodeCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementMapper().getModelClass());
        Map<String, Object> result;
        try {
            result = StatCommon.calc(getCloudElementMapper(), condition, fieldCalcMap);
        } catch (Exception e) {
            log.error("Calc count cloud element on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        Map<String, Long> formatResult = new HashMap<>();
        result.forEach((fieldName, fieldValue) -> {
            formatResult.put(fieldName, Long.valueOf(fieldValue.toString()));
        });
        return formatResult;
    }

    /**
     * 查询端口指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段  有效字段CALC_COUNT_PORT_FIELDS
     * @param distinct 是否去重
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public Map<String, Long> countPort(List<String> sceneIds, List<String> nodeIds, List<String> fieldNames,
                                       boolean distinct) {
        var condition = genNodePortCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementPortMapper().getModelClass());
        Map<String, Long> result;
        try {
            result = StatCommon.count(getCloudElementPortMapper(), condition, fieldNames, distinct);
        } catch (Exception e) {
            log.error("Count cloud element port on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return result;
    }

    /**
     * 查询端口指定字段的聚合统计数量, 需要自定义计算结果
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段 有效字段CALC_COUNT_PORT_FIELDS
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public Map<String, Long> calcCountPort(List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        var fieldCalcMap = fieldNames.stream()
                .collect(Collectors.toMap(fieldName -> fieldName,
                        fieldName -> CALC_COUNT_PORT_FIELD_CALC_MAP.getOrDefault(fieldName, List::size)));
        var condition = genNodePortCondition(sceneIds, nodeIds);
        condition.currentLink(getCloudElementPortMapper().getModelClass());
        Map<String, Object> result;
        try {
            result = StatCommon.calc(getCloudElementPortMapper(), condition, fieldCalcMap);
        } catch (Exception e) {
            log.error("Calc count cloud element port on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        Map<String, Long> formatResult = new HashMap<>();
        result.forEach((fieldName, fieldValue) -> {
            formatResult.put(fieldName, Long.valueOf(fieldValue.toString()));
        });
        return formatResult;
    }

}
