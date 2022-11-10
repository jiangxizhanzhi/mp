package cn.cyberpeace.mp.flow.v2.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FlowTaskRecordBO extends ModelBO<FlowTaskRecordModel, FlowTaskRecordBO> {
    private String flowGeneratorId;
    private String flowGeneratorNicId;
    private String targetIp;
    private String targetMac;
    private String targetNetworkId;
    private String targetResourceId;
    private Integer status;

    private String name;
    private String desc;
    private Integer category;
    private Integer bandwidth;
    private String loadMode;
    private Integer ixiaRunId;
    private String ixiaTestName;
    /**
     * 负载矩阵集合
     */
    private List<LoadBandwidth> loadLists;
    private List<String> filterProtocols;
    private List<TaskFlow> flows;

    /**
     * 流量运行时间
     */
    private Integer duration;

    /**
     * 平台当前时间 yyyy-MM-dd HH:mm:ss
     */
    private String time;

    @Override
    public FlowTaskRecordBO fromModel(FlowTaskRecordModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowTaskRecordModel::getFilterProtocols),
                BeanUtils.getBeanGetterFieldName(FlowTaskRecordModel::getFlows)));
        filterProtocols = StringUtils.isEmpty(obj.getFilterProtocols()) ? null
                : JsonCommon.parse(obj.getFilterProtocols(), new TypeReference<>(){});
        flows = StringUtils.isEmpty(obj.getFlows()) ? null
                : JsonCommon.parse(obj.getFlows(), new TypeReference<>(){});
        return this;
    }

    @Override
    public FlowTaskRecordModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowTaskRecordModel::getFilterProtocols),
                BeanUtils.getBeanGetterFieldName(FlowTaskRecordModel::getFlows)));
        if (filterProtocols != null) {
            obj.setFilterProtocols(JsonCommon.stringify(filterProtocols));
        }
        if (flows != null) {
            obj.setFlows(JsonCommon.stringify(flows));
        }
        return obj;
    }


    @Override
    public FlowTaskRecordModel createModel() {
        return new FlowTaskRecordModel();
    }

    @Data
    public static class TaskFlow {
        private String flowGeneratorId;
        private String flowGeneratorNicId;
        private Integer status;
        private String flowId;

        private String name;
        private String description;
        private Integer category;
        private Integer interactiveType;
        private Integer resourceType;
        private List<String> protocols;
        private String pcapFile;
        private List<ProtocolConfig> protocolConfigs;
        private List<ProtocolFlowUnit> protocolFlowUnits;
        private String scriptType;
        private String scriptFile;
        private List<ScriptParam> scriptParams;
        private String ixiaFlowName;

        private Integer delay;
        private Integer weight;
        private Boolean supportEvasion;

        /**
         * 转换为行拼接的脚本参数
         */
        public String lineScriptParams() {
            if (scriptParams == null) {
                return StringUtils.EMPTY;
            }
            List<ScriptParam> validScriptParams = new ArrayList<>();
            var firstValidSingle = false;
            for (var i = scriptParams.size() - 1; i >= 0; i--) {
                var scriptParam = scriptParams.get(i);
                if (firstValidSingle) {
                    validScriptParams.add(scriptParam);
                    continue;
                }

                var value = scriptParam.getValue();
                if (StringUtils.isEmpty(value)) {
                    value = scriptParam.getDefaultValue();
                }
                if (!StringUtils.isEmpty(value)) {
                    validScriptParams.add(scriptParam);

                    if (StringUtils.isEmpty(scriptParam.getArgName())
                            && StringUtils.isEmpty(scriptParam.getArgShortName())) {
                        firstValidSingle = true;
                    }
                }
            }

            List<String> paramStrs = new ArrayList<>();
            for (var i = validScriptParams.size() - 1; i >= 0; i--) {
                var scriptParam = validScriptParams.get(i);
                var value = scriptParam.getValue();
                if (StringUtils.isEmpty(value)) {
                    value = scriptParam.getDefaultValue();
                }
                if (StringUtils.isEmpty(value)) {
                    value = "\"\"";
                }

                String paramStr;
                if (!StringUtils.isEmpty(scriptParam.getArgName())) {
                    paramStr = String.format("--%s=%s", scriptParam.getArgName(), value);
                } else if (!StringUtils.isEmpty(scriptParam.getArgShortName())) {
                    paramStr = String.format("-%s=%s", scriptParam.getArgName(), value);
                } else {
                    paramStr = value;
                }
                paramStrs.add(paramStr);
            }

            return StringUtils.joinWith(" ", paramStrs.toArray());
        }
    }

    @Data
    public static class ProtocolConfig {
        private String name;
        private Map<String, String> config;
    }

    @Data
    public static class ProtocolFlowUnit {
        private String name;
        private Integer seq;
        private String protocolName;
        private String source;
        private Map<String, String> params;
    }

    @Data
    public static class ScriptParam {
        private Boolean required;
        private String name;
        private String desc;
        private String argName;
        private String argShortName;
        private String valueType;
        private String defaultValue;
        private String value;
    }

    @Data
    public static class LoadBandwidth {
        /**
         * 时间：秒
         */
        private Integer time;

        /**
         * 带宽：mbps
         */
        private Integer bandwidth;
    }

}
