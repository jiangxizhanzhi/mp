package cn.cyberpeace.mp.flow.v2.bo;

import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import lombok.Data;

@Data
public class FlowTaskCountBO extends ModelBO<FlowTaskCountModel, FlowTaskCountBO> {

    private String flowGeneratorId;
    private String flowTaskRecordId;
    private String flowId;
    private String bandwidth;
    private Long tcpConnectTry;
    private Long packets;
    private Long tcp_connect_success;

    @Override
    public FlowTaskCountBO fromModel(FlowTaskCountModel obj, String... ignoreFields) {
        return super.fromModel(obj, ignoreFields);
    }

    @Override
    public FlowTaskCountModel toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        return obj;
    }

    @Override
    public FlowTaskCountModel createModel() {
        return new FlowTaskCountModel();
    }

}
