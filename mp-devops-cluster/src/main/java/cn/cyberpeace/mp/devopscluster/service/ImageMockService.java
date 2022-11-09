package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
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
public class ImageMockService {

    public List<Device> filterByIds(List<Integer> ids) {
        Resource resource = new ClassPathResource("config/images.json");
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

}
