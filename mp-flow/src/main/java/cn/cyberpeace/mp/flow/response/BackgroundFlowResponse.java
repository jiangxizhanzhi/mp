package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * @Description: 背景流量返回信息
 * @Author qiaokang
 * @Date：2021/8/10 16:57
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

    public BackgroundFlowResponse(FlowInfoModel flowInfoModel, String prefixUrl) {
        setResourceId(flowInfoModel.getId());
        setTitle(flowInfoModel.getTitle());
        setIntroduction(flowInfoModel.getFlowDesc());
        setCategory(flowInfoModel.getCategory());
        if (Objects.nonNull(flowInfoModel.getCreateTime())) {
            setCreateTime(DateUtil.format(flowInfoModel.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        setInteractiveType(flowInfoModel.getInteractiveType());

        setFileName(flowInfoModel.getFileName());
        if (StringUtils.isNotEmpty(flowInfoModel.getPcapFile())) {
            setPcapFile(flowInfoModel.getPcapFile());
            setPcapFilePath(prefixUrl + File.separator + flowInfoModel.getPcapFile());
        }

    }

}
