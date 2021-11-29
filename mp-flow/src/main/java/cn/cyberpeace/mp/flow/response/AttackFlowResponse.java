package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 攻击流量返回信息
 * @Author qiaokang
 * @Date：2021/8/10 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "攻击流量返回信息")
public class AttackFlowResponse extends FlowInfoResponse {

    /**
     * 攻击脚本文件地址
     */
    @ApiModelProperty(value = "攻击脚本文件地址")
    private String file;

    /**
     * 攻击脚本代码
     */
    @ApiModelProperty(value = "攻击脚本代码")
    private String code;

    /**
     * 攻击脚本语言 0:python, 1:shell
     */
    @ApiModelProperty(value = "攻击脚本语言 0:python, 1:shell")
    private Integer suffix;

    /**
     * 攻击脚本运行参数
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private String runCmd;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

    public AttackFlowResponse(FlowInfoModel flowInfoModel, String prefixUrl, List<FlowScriptParamModel> scriptParamModels) {
        setResourceId(flowInfoModel.getId());
        setTitle(flowInfoModel.getTitle());
        setIntroduction(flowInfoModel.getFlowDesc());
        setCategory(flowInfoModel.getCategory());
        if (Objects.nonNull(flowInfoModel.getCreateTime())) {
            setCreateTime(DateUtil.format(flowInfoModel.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        setInteractiveType(flowInfoModel.getInteractiveType());

        String filePath = flowInfoModel.getScriptFile();
        if (StringUtils.isNotBlank(filePath)) {
            setFile(prefixUrl + filePath);
            setSuffix(filePath.endsWith(".py") ? 0 : 1);
        }
        setRunCmd(flowInfoModel.getRunCmd());

        if (CollectionUtils.isNotEmpty(scriptParamModels)) {
            setScriptParamModels(scriptParamModels);
        }
    }

}
