package cn.cyberpeace.mp.flow.v2.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowGeneratorModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class FlowGeneratorBO extends ModelBO<FlowGeneratorModel, FlowGeneratorBO> {
    private String name;
    private String type;
    private String resourceId;
    private String ip;
    private String server;
    private String username;
    private String password;
    private String token;
    private List<Nic> nics;

    @Override
    public FlowGeneratorModel createModel() {
        return new FlowGeneratorModel();
    }

    @Data
    public static class Nic {
        private String id;
        private String name;
        private String ip;
        private String mac;
        private String resourceId;
        private String cpCloudNetworkId;
    }

    @Override
    public FlowGeneratorBO fromModel(FlowGeneratorModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowGeneratorModel::getNics)));
        nics = StringUtils.isEmpty(obj.getNics()) ? null
                : JsonCommon.parse(obj.getNics(), new TypeReference<>() {
        });
        return this;
    }
}
