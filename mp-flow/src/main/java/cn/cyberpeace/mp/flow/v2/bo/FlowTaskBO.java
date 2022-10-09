package cn.cyberpeace.mp.flow.v2.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class FlowTaskBO extends ModelBO<FlowTaskModel, FlowTaskBO> {
    private String name;
    private String description;
    private Integer category;
    private Integer bandwidth;
    private String loadMode;
    private List<String> filterProtocols;
    private List<TaskFlow> flows;

    @Override
    public FlowTaskBO fromModel(FlowTaskModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowTaskModel::getFilterProtocols),
                BeanUtils.getBeanGetterFieldName(FlowTaskModel::getFlows)));
        filterProtocols = StringUtils.isEmpty(obj.getFilterProtocols()) ? null
                : JsonCommon.parse(obj.getFilterProtocols(), new TypeReference<>(){});
        flows = StringUtils.isEmpty(obj.getFlows()) ? null
                : JsonCommon.parse(obj.getFlows(), new TypeReference<>(){});
        return this;
    }

    @Override
    public FlowTaskModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowTaskModel::getFilterProtocols),
                BeanUtils.getBeanGetterFieldName(FlowTaskModel::getFlows)));
        if (filterProtocols != null) {
            obj.setFilterProtocols(JsonCommon.stringify(filterProtocols));
        }
        if (flows != null) {
            obj.setFlows(JsonCommon.stringify(flows));
        }
        return obj;
    }


    @Override
    public FlowTaskModel createModel() {
        return new FlowTaskModel();
    }


    @Data
    public static class TaskFlow {
        private String flowId;
        private Integer delay;
        private Integer weight;
    }

}
