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
import cn.cyberpeace.mp.flow.service.FlowPortService;
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
import java.util.stream.Collectors;

/**
 * @Description: 流量网口service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowPortServiceImpl extends BaseModelServiceImpl<FlowPortModel, FlowPortMapper, FlowPortFields, QueryParams> implements FlowPortService {

    @Autowired
    private FlowConfig flowConfig;

    @Value("${mp.devopscluster.cp-ip:}")
    private String cpIp;

    public FlowPortServiceImpl(FlowPortMapper stuMapper, FlowPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}
