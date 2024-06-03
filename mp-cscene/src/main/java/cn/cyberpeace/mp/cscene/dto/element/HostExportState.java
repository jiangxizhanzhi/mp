package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点状态信息(cpu，内存，磁盘等")
public class HostExportState {

    @ApiModelProperty("CPU")
    private List<HostState> hostState;

    @ApiModelProperty("网络")
    private ListObjServiceResult<Netiface> netiface;

    @ApiModelProperty("端口列表")
    private ListObjServiceResult<Port> ports;

    @ApiModelProperty("进程")
    private ListObjServiceResult<Process> process;
}
