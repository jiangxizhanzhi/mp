package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeParamRequest;
import cn.cyberpeace.mp.situationaware.model.enums.ParamTypeEnum;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ProbeParamService {

    @Autowired
    private ProbeParamMapper probeParamMapper;

    
    public String getProbeParamAsJsonStr(String probeId, String templateId) {
        if (StringUtils.isNotBlank(probeId)) {
            List<ProbeParamModel> probeParamModels = probeParamMapper.selectByProbeIdAndTemplateId(probeId, templateId);
            String paramStr = paramModels2ParamStr(probeParamModels);
            return paramStr;
        }

        return null;
    }

    
    public List<ProbeParamRequest> paramStr2paramsRequest(String parameterStr) {
        if (StringUtils.isNotBlank(parameterStr)) {
            List<ProbeParamRequest> probeParamReqs = new ArrayList<>();
            
            JSONObject paramObj = JSONObject.parseObject(parameterStr, Feature.OrderedField);
            paramObj.keySet().forEach(paramKey -> {
                ProbeParamRequest paramReq = new ProbeParamRequest();
                paramReq.setName(paramKey);

                JSONArray paramArray = paramObj.getJSONArray(paramKey);
                
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

                
                paramReq.setDefaultValue(paramArray.getString(1));
                
                paramReq.setDescription(paramArray.getString(2));

                
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


    
    public String paramModels2ParamStr(List<ProbeParamModel> probeParamModels) {
        if (CollectionUtils.isNotEmpty(probeParamModels)) {
            
            JSONObject paramObj = new JSONObject(true);
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
                
                paramArray.add(type);
                
                paramArray.add(param.getDefaultValue());
                
                paramArray.add(param.getDescription());
                paramObj.put(param.getName(), paramArray);
            });
            String paramStr = paramObj.toJSONString();
            return paramStr;
        }
        return null;
    }

}
