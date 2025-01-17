 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACWrapperService.java.
 // File size 3220.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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

 /**
  * urlacwrapper服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
public class UrlACWrapperService {

     /**
      * url acservice
      */
     @Autowired
    private UrlACService urlACService;

     /**
      * 获取url目标映射
      *
      * @return {@link Map }<{@link String }, {@link List }<{@link String }>>
      */
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

     /**
      * 获取url模式目标映射
      *
      * @return {@link Map }<{@link String }, {@link List }<{@link String }>>
      */
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

     /**
      * 获取url方法映射
      *
      * @return {@link Map }<{@link String }, {@link List }<{@link String }>>
      */
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

     /**
      * 获取url模式方法映射
      *
      * @return {@link Map }<{@link String }, {@link List }<{@link String }>>
      */
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
