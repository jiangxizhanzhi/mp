package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.frontbusiness.dto.UrlAccessCtlDto;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class UrlACWrapperService {

    @Autowired
    private UrlACService urlACService;



    public Map<String, List<String>> getUrlTargetsMap() {
        List<UrlAccessCtlDto> dtos = urlACService.listAllUrlAccessCtlDtos();
        Map<String, List<String>> urlTargetsMap = new HashMap<>();

        dtos.forEach(d -> {
            List<String> targetIds = urlTargetsMap.get(d.getUrl());
            if (CollectionUtil.isEmpty(targetIds)) {
                targetIds = new ArrayList<>();
                urlTargetsMap.put(d.getUrl(), targetIds);
            }

            if (CollectionUtil.isNotEmpty(d.getTargetIds())) {
                urlTargetsMap.get(d.getUrl()).addAll(d.getTargetIds());
            } else {
                log.warn("No targets url detected! {}", d.getUrl());
            }
        });

        return urlTargetsMap;
    }


    public Map<String, List<String>> getUrlPatternTargetsMap() {
        List<UrlAccessCtlDto> dtos = urlACService.listAllUrlACPatternDtos();
        Map<String, List<String>> urlTargetsMap = new HashMap<>();

        dtos.forEach(d -> {
            List<String> targetIds = urlTargetsMap.get(d.getUrl());
            if (CollectionUtil.isEmpty(targetIds)) {
                targetIds = new ArrayList<>();
                urlTargetsMap.put(d.getUrl(), targetIds);
            }

            urlTargetsMap.get(d.getUrl()).addAll(d.getTargetIds());
        });

        return urlTargetsMap;
    }



    public Map<String, List<String>> getUrlMethodsMap() {
        List<UrlAccessCtlDto> dtos = urlACService.listAllUrlAccessCtlDtos();
        Map<String, List<String>> urlMethodsMap = new HashMap<>();

        dtos.forEach(d -> {
            List<String> methods = urlMethodsMap.get(d.getUrl());
            if (CollectionUtil.isEmpty(methods)) {
                methods = new ArrayList<>();
                urlMethodsMap.put(d.getUrl(), methods);
            }

            urlMethodsMap.get(d.getUrl()).addAll(d.getMethods());
        });

        return urlMethodsMap;
    }

    public Map<String, List<String>> getUrlPatternMethodsMap() {
        List<UrlAccessCtlDto> dtos = urlACService.listAllUrlACPatternDtos();
        Map<String, List<String>> urlMethodsMap = new HashMap<>();

        dtos.forEach(d -> {
            List<String> methods = urlMethodsMap.get(d.getUrl());
            if (CollectionUtil.isEmpty(methods)) {
                methods = new ArrayList<>();
                urlMethodsMap.put(d.getUrl(), methods);
            }

            urlMethodsMap.get(d.getUrl()).addAll(d.getMethods());
        });

        return urlMethodsMap;
    }

}
