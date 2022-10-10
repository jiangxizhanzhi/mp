package cn.cyberpeace.mp.flow.v2.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_generator")
public class FlowGeneratorModel extends BaseModel {
    private String name;
    @DefaultStr(Type.CPCLOUD_TREX)
    private String type;
    @DefaultStr
    private String resourceId;
    @DefaultStr
    private String ip;
    @DefaultStr
    private String server;
    @DefaultStr
    private String username;
    @DefaultStr
    private String password;
    @DefaultStr
    private String token;
    @DefaultStr
    private String nics;

    public static class Type {
        public static final String CPCLOUD_TREX = "cpcloud_trex";
        public static final String CPCLOUD_IXIA = "cpcloud_ixia";
        public static final String DRAGON_TEST = "dragon_test";

        private Type() {}
    }

}
