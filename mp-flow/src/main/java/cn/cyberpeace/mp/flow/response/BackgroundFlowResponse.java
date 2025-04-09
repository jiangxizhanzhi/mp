 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BackgroundFlowResponse.java.
 // File size 2245.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName BackgroundFlowResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FlowInfoResponse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "背景流量返回信息")
public class BackgroundFlowResponse extends FlowInfoResponse {

    /**
     * 流量文件名称
     */
    @ApiModelProperty(value = "流量文件名称")
    private String fileName;

    /**
     * 流量文件路径(不带ip)
     */
    @ApiModelProperty(value = "流量文件路径(不带ip)")
    private String pcapFile;

    /**
     * 流量文件地址(全地址)
     */
    @ApiModelProperty(value = "流量文件地址(全地址)")
    private String pcapFilePath;

    /**
     * pcap包含的协议
     */
    @ApiModelProperty(value = "pcap包含协议集合")
    private List<ProtocolBO> protocolList;

    /**
     * 时长(单位ms)
     */
    @ApiModelProperty(value = "时长(单位ms)")
    private Long duration;

    /**
     * 大小(单位Byte)
     */
    @ApiModelProperty(value = "大小(单位Byte)")
    private Long pcapSize;

    /**
     * 包数(报文数量)
     */
    @ApiModelProperty(value = "包数(报文数量)")
    private Long pktNum;

    public BackgroundFlowResponse(FlowInfoModel flowInfoModel, String prefixUrl) {
        setResourceId(flowInfoModel.getId());
        setResourceType(flowInfoModel.getResourceType());
        setTitle(flowInfoModel.getTitle());
        setIntroduction(flowInfoModel.getFlowDesc());
        setFlowDesc(flowInfoModel.getFlowDesc());
        setCategory(flowInfoModel.getCategory());
        if (Objects.nonNull(flowInfoModel.getCreateTime())) {
            setCreateTime(TimeUtils.localDateTimeToString(flowInfoModel.getCreateTime()));
        }
        setInteractiveType(flowInfoModel.getInteractiveType());

        setFileName(flowInfoModel.getFileName());
        if (StringUtils.isNotEmpty(flowInfoModel.getPcapFile())) {
            setPcapFile(flowInfoModel.getPcapFile());
            setPcapFilePath(prefixUrl + File.separator + flowInfoModel.getPcapFile());
        }

    }

}
