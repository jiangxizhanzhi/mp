package cn.cyberpeace.mp.scene.core.bo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ThirdPartyElementConnectBO {
    @ExcelProperty("连线起始节点名称（请选择）")
    private String fromNodeName;
    @ExcelProperty("连线起始节点ip（非终端节点可以为空）")
    private String fromNodeIp;
    @ExcelProperty("连线终止节点名称（请选择）")
    private String toNodeName;
    @ExcelProperty("连线终止节点ip（非终端节点可以为空）")
    private String toNodeIp;
    private String fromPortId;
    private String toPortId;


}
