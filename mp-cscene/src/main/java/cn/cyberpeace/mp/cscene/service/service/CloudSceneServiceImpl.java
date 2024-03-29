package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.cscene.repository.mapper.CloudSceneMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CloudSceneServiceImpl {

    @Autowired
    private CloudSceneMapper cloudSceneMapper;

}

