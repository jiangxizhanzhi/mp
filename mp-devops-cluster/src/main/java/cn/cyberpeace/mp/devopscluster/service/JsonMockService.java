package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.vo.TypeAllVO;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
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
@Service
public class JsonMockService {

    private static final String IMAGES_JSON = "config/images.json";

    private static final String TYPE_JSON = "config/type.json";

    /**
     * 查询所有镜像
     * @param ids
     * @return
     */
    public List<Device> filterDevicesByIds(List<Integer> ids) {
        Resource resource = new ClassPathResource(IMAGES_JSON);
        List<Device> allDevices = new ArrayList<>();
        String content = null;
        try {
            content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        Resource resource = new ClassPathResource(TYPE_JSON);
        TypeAllVO typeAllVO = new TypeAllVO();
        String content = null;
        try {
            content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (StringUtils.isNotBlank(content)) {
            typeAllVO = JsonCommon.parse(content, TypeAllVO.class);
        }

        return typeAllVO;
    }

}
