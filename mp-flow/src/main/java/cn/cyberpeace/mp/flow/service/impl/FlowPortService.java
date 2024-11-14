 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPortServiceImpl.java.
 // File size 7934.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

 import cn.cyberpeace.mp.base.exception.MpRuntimeException;
 import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
 import cn.cyberpeace.mp.base.service.param.QueryParams;
 import cn.cyberpeace.mp.base.util.HttpUtils;
 import cn.cyberpeace.mp.flow.config.FlowConfig;
 import cn.cyberpeace.mp.flow.constant.BaseConstants;
 import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
 import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
 import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
 import cn.cyberpeace.mp.flow.model.FlowPortModel;
 import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
 import cn.cyberpeace.mp.flow.service.field.FlowPortFields;
 import cn.cyberpeace.mp.flow.util.CommonUtil;
 import com.alibaba.fastjson.JSONObject;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.collections4.CollectionUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.exception.ExceptionUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.scheduling.annotation.Async;
 import org.springframework.stereotype.Service;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import java.util.TreeSet;

 /**
 * @ClassName FlowPortServiceImpl
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowPortService extends BaseModelServiceImpl<FlowPortModel, FlowPortMapper, FlowPortFields, QueryParams>   {

    @Autowired
    private FlowConfig flowConfig;

    @Value("${mp.devopscluster.cp-ip:}")
    private String cpIp;

    @Autowired
    public FlowPortService(FlowPortMapper stuMapper, FlowPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}
