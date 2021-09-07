package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 新增pcap管理请求
 * @Author qiaokang
 * @Date：2021/8/10 15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增pcap管理请求")
public class AddPcapRequest {

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String fileName;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String tag;

    /**
     * 类型：strike 武器库，normal 普通流量
     */
    @ApiModelProperty("类型：strike 武器库，normal 普通流量")
    private String type = "normal";

    /**
     * 文件类别：DDOS、cve、fuzze
     */
    @ApiModelProperty("文件类别：DDOS、cve、fuzze")
    private String categoryId;

}
