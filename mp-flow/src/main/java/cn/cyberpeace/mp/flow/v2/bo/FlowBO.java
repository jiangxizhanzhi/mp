package cn.cyberpeace.mp.flow.v2.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class FlowBO extends ModelBO<FlowModel, FlowBO> {
    @NotEmpty(message = "name can not be empty")
    private String name;
    @NotEmpty(message = "description can not be empty")
    private String description;
    @NotNull(message = "category can not be empty")
    private Integer category;
    @NotNull(message = "interactiveType can not be empty")
    private Integer interactiveType;
    @NotNull(message = "resourceType can not be empty")
    private Integer resourceType;
    private List<String> protocols;
    private String pcapFile;
    private String fileName;
    private List<ProtocolConfig> protocolConfigs;
    private List<ProtocolFlowUnit> protocolFlowUnits;
    private String scriptType;
    private String scriptFile;
    private List<ScriptParam> scriptParams;
    private String ixiaFlowName;
    private LocalDateTime createTime;

    @Override
    public FlowBO fromModel(FlowModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocols),
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocolConfigs),
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocolFlowUnits),
                BeanUtils.getBeanGetterFieldName(FlowModel::getScriptParams)));
        protocols = StringUtils.isEmpty(obj.getProtocols()) ? null
                : JsonCommon.parse(obj.getProtocols(), new TypeReference<>(){});
        protocolConfigs = StringUtils.isEmpty(obj.getProtocolConfigs()) ? null
                : JsonCommon.parse(obj.getProtocolConfigs(), new TypeReference<>(){});
        protocolFlowUnits = StringUtils.isEmpty(obj.getProtocolFlowUnits()) ? null
                : JsonCommon.parse(obj.getProtocolFlowUnits(), new TypeReference<>(){});
        scriptParams = StringUtils.isEmpty(obj.getScriptParams()) ? null
                : JsonCommon.parse(obj.getScriptParams(), new TypeReference<>(){});
        return this;
    }

    @Override
    public FlowModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocols),
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocolConfigs),
                BeanUtils.getBeanGetterFieldName(FlowModel::getProtocolFlowUnits),
                BeanUtils.getBeanGetterFieldName(FlowModel::getScriptParams)));
        if (protocols != null) {
            obj.setProtocols(JsonCommon.stringify(protocols));
        }
        if (protocolConfigs != null) {
            obj.setProtocolConfigs(JsonCommon.stringify(protocolConfigs));
        }
        if (protocolFlowUnits != null) {
            obj.setProtocolFlowUnits(JsonCommon.stringify(protocolFlowUnits));
        }
        if (scriptParams != null) {
            obj.setScriptParams(JsonCommon.stringify(scriptParams));
        }
        return obj;
    }


    @Override
    public FlowModel createModel() {
        return new FlowModel();
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
    }

    public static class ScriptParamValueType {
        public static final String STRING = "string";
        public static final String NUMBER = "number";

        private ScriptParamValueType() {}
    }

    /**
     * 参数校验
     */
    public void paramCheck(boolean checkId) {
        if (checkId) {
            if (StringUtils.isBlank(this.getId())) {
                throw new MpRuntimeException(FlowErrorCode.PARAMS_SHOULD_NOT_EMPTY.format("id"));
            }
        }

        if (Objects.equals(resourceType, FlowModel.ResourceType.PCAP)) {
            if (StringUtils.isBlank(pcapFile)) {
                throw new MpRuntimeException(FlowErrorCode.PARAMS_SHOULD_NOT_EMPTY.format("pcapFile"));
            }
        }
    }

}
