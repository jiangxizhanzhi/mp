package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devopscluster.vo.TypeAllVO;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/11/9 15:02
 */
@Slf4j
@Service
public class JsonMockService {

    private static final String IMAGES_JSON = "config/images.json";

    private static final String TYPE_JSON = "config/type.json";

    private static final String FLAVOR_JSON = "config/flavor.json";

    /**
     * 查询所有镜像
     * @param ids
     * @return
     */
    public List<Device> filterDevicesByIds(List<Integer> ids) {
        String content = getContent(IMAGES_JSON);
        List<Device> allDevices = new ArrayList<>();

        if (StringUtils.isNotBlank(content)) {
            allDevices = JSONObject.parseArray(content, Device.class);
            if (CollectionUtil.isNotEmpty(ids)) {
                List<Device> filteredDevices = allDevices.stream().filter(d -> ids.contains(d.getId())).collect(Collectors.toList());
                return filteredDevices;
            }
        }

        return allDevices;
    }


    /**
     * 查询所有类型
     * @return
     */
    public TypeAllVO getTypeAllVO() {
        TypeAllVO typeAllVO = new TypeAllVO();
        String content = getContent(TYPE_JSON);

        if (StringUtils.isNotBlank(content)) {
            typeAllVO = JSONObject.parseObject(content, TypeAllVO.class);
        }

        return typeAllVO;
    }


    /**
     * 查询flavor列表
     * @return
     */
    public List<Flavor> getFlavors() {
        List<Flavor> flavors = new ArrayList<>();
        String content = getContent(FLAVOR_JSON);

        if (StringUtils.isNotBlank(content)) {
            flavors = JSONObject.parseArray(content, Flavor.class);
        }

        return flavors;
    }


    private String getContent(String jsonFile) {
        Resource resource = new ClassPathResource(jsonFile);
        String content = null;
        try {
            content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
        } catch (IOException e) {
            log.error("json 解析异常:{}", jsonFile);
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        return content;
    }

}
