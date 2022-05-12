package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamRequest;
import cn.cyberpeace.mp.situationaware.model.enums.ParamTypeEnum;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/10/21 11:12 上午
**/
@Service
@Slf4j
public class ProbeParamService {


    /**
     * 将请求json字符串形式的参数内容，处理成ParamRequest
     * @param parameterStr
     * @return
     */
    public List<ProbeParamRequest> paramStr2paramsRequest(String parameterStr) {
        if (StringUtils.isNotBlank(parameterStr)) {
            List<ProbeParamRequest> probeParamReqs = new ArrayList<>();
            JSONObject paramObj = JSONObject.parseObject(parameterStr);
            paramObj.keySet().forEach(paramKey -> {
                ProbeParamRequest paramReq = new ProbeParamRequest();
                paramReq.setName(paramKey);

                JSONArray paramArray = paramObj.getJSONArray(paramKey);
                // 参数类型
                Integer type;
                if (ParamTypeEnum.STRING.toString().equals(paramArray.getString(0))) {
                    type = ParamTypeEnum.STRING.toValue();
                } else if (ParamTypeEnum.IP.toString().equals(paramArray.getString(0))) {
                    type = ParamTypeEnum.IP.toValue();
                } else if (ParamTypeEnum.NUMBER.toString().equals(paramArray.getString(0))) {
                    type = ParamTypeEnum.NUMBER.toValue();
                } else {
                    type = ParamTypeEnum.BOOL.toValue();
                }
                paramReq.setType(type);

                // 参数值
                paramReq.setDefaultValue(paramArray.getString(1));
                // 参数描述
                paramReq.setDescription(paramArray.getString(2));

                // 参数校验
                if (paramKey.length() > 40 || paramReq.getDefaultValue().length() > 50 ||
                        paramReq.getDescription().length() > 2048) {
                    log.error("param length is invalid");
                    throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
                }

                probeParamReqs.add(paramReq);
            });

            return probeParamReqs;
        }
        return Collections.emptyList();
    }


    /**
     * 将probeParamModels转化成parameter json字符串
     * @return
     */
    public String paramModels2ParamStr(List<ProbeParamModel> probeParamModels) {
        if (CollectionUtils.isNotEmpty(probeParamModels)) {
            JSONObject paramObj = new JSONObject();
            probeParamModels.forEach(param -> {
                JSONArray paramArray = new JSONArray();
                String type;
                if (param.getType() == ParamTypeEnum.STRING.toValue()) {
                    type = "String";
                } else if (param.getType() == ParamTypeEnum.IP.toValue()) {
                    type = "IP";
                } else if (param.getType() == ParamTypeEnum.NUMBER.toValue()) {
                    type = "Number";
                } else {
                    type = "Bool";
                }
                // 类型
                paramArray.add(type);
                // 默认值
                paramArray.add(param.getDefaultValue());
                // 参数说明
                paramArray.add(param.getDescription());
                paramObj.put(param.getName(), paramArray);
            });
            String paramStr = paramObj.toJSONString();
            return paramStr;
        }
        return null;
    }

}
