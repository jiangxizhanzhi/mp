package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.enums.ParamTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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
     * 攻击脚本运行参数(JSON)
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private String parameter;

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
            setFile(prefixUrl + File.separator + filePath);
            setSuffix(filePath.endsWith(".py") ? 0 : 1);
        }
        setRunCmd(flowInfoModel.getRunCmd());

        if (CollectionUtils.isNotEmpty(scriptParamModels)) {
            JSONObject paramObj = new JSONObject();
            scriptParamModels.forEach(param -> {
                JSONArray paramArray = new JSONArray();
                String type;
                if (param.getParamType() == ParamTypeEnum.STRING.toValue()) {
                    type = "String";
                } else if (param.getParamType() == ParamTypeEnum.IP.toValue()) {
                    type = "IP";
                } else if (param.getParamType() == ParamTypeEnum.NUMBER.toValue()) {
                    type = "Number";
                } else {
                    type = "Bool";
                }
                // 类型
                paramArray.add(type);
                // 默认值
                paramArray.add(param.getParamValue());
                // 参数说明
                paramArray.add(param.getParamDesc());
                paramObj.put(param.getParamKey(), paramArray);
            });
            setParameter(paramObj.toJSONString());
        }
    }

}
