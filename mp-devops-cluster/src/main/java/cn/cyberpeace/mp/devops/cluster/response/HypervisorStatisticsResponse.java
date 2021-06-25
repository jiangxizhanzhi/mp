package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "资源统计返回信息")
public class HypervisorStatisticsResponse extends BaseOpenStackResponse{

    /**
     * CPU总核数
     */
    private Integer vcpus;

    /**
     * 内存总大小
     */
    private Long memoryMb;

    /**
     * 本地磁盘总大小
     */
    private Long localGb;

    /**
     * 已使用CPU核数
     */
    private Long vcpusUsed;

    /**
     * 已使用内存大小
     */
    private Long memoryMbUsed;

    /**
     * 已使用磁盘大小
     */
    private Long localGbUsed;

    /**
     * 空闲内存大小
     */
    private Long freeRamMb;

    /**
     * 空闲磁盘大小
     */
    private Long freeDiskGb;

    
}
