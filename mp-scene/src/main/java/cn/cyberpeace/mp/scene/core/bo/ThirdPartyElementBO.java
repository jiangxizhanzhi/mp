package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
public class ThirdPartyElementBO {
    @ExcelProperty("节点类型（节点类型请选择终端、路由器、交换机、防火墙、其他）")
    private String nodeType;

    @ExcelProperty("节点名称（节点名称需要唯一）")
    private String nodeName;

    @ExcelProperty("所属靶场")
    private String range;

    private String nodeId = TextUtils.uuid();

    private List<ThirdPartyElementPortBO> ports = new ArrayList<>();



    public static class NodeType {
        private NodeType() {}
        public static final String END_DEVICE = "终端";
        public static final String ROUTER = "路由器";
        public static final String SWITCH = "交换机";
        public static final String FIREWALL = "防火墙";
        public static final String OTHER = "其他";
    }

    public static class DefaultIcon {
        private DefaultIcon() {}

        public static final String END_DEVICE = "/api/mp/file/504/805/726bd707e8ebacd6d1147584f221a5e2.png";
        public static final String SWITCH_ICON = "/api/mp/file/639/162/85ce8f4bcad3948c390f774d2916fee2.png";
        public static final String ROUTER_ICON = "/api/mp/file/067/985/d89cb7563a86d509593fa17d01c87fe2.png";
        public static final String FIREWALL_ICON = "/api/mp/file/839/714/051a3634081da8858e9af15012b9d9f2.png";
        public static final String OTHER_ICON = "/api/mp/file/813/184/0353b631c26ab7b0a0f9fd2b8235eaae.png";
    }


    public String getDefaultIcon() {
        if (NodeType.END_DEVICE.equals(this.nodeType)) {
            return DefaultIcon.END_DEVICE;
        } else if (NodeType.SWITCH.equals(this.nodeType)) {
            return DefaultIcon.SWITCH_ICON;
        } else if (NodeType.ROUTER.equals(this.nodeType)) {
            return DefaultIcon.ROUTER_ICON;
        } else if (NodeType.FIREWALL.equals(this.nodeType)) {
            return DefaultIcon.FIREWALL_ICON;
        } else {
            return DefaultIcon.OTHER_ICON;
        }
    }

    public int getDefaultType() {
        if (NodeType.END_DEVICE.equals(this.nodeType)) {
            return BaseSceneElementModel.Type.TERMINAL;
        } else if (NodeType.SWITCH.equals(this.nodeType)) {
            return BaseSceneElementModel.Type.SWITCH;
        } else if (NodeType.ROUTER.equals(this.nodeType)) {
            return BaseSceneElementModel.Type.ROUTER;
        } else if (NodeType.FIREWALL.equals(this.nodeType)) {
            return BaseSceneElementModel.Type.FIREWALL;
        } else {
            return BaseSceneElementModel.Type.OTHER;
        }
    }
}
