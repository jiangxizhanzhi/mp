package cn.cyberpeace.mp.front.business.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.front.business.dto.ImportUrlAuth;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.front.business.mapper.UrlACMapper;
import cn.cyberpeace.mp.front.business.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.front.business.model.UrlACModel;
import cn.cyberpeace.mp.front.business.model.UrlACTargetModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:52
 */
@Slf4j
@Service
public class UrlACService {

    public static final String CACHE_VAL_ALL_URLAC = "ALL_URLAC_DTOS_MAP";

    @Autowired
    private UrlACMapper urlACMapper;

    @Autowired
    private UrlACTargetMapper urlACTargetMapper;

    /**
     * 查询所有的
     * @return
     */
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_10M, value = CACHE_VAL_ALL_URLAC)
    public List<UrlAccessCtlDto> listAllUrlAccessCtlDtos() {
        List<UrlAccessCtlDto> dtos = urlACMapper.listAllUrlAccessCtlDtos();
        //Map<String, List<UrlAccessCtlDto>> urlAcDtoMap = new HashMap<>();

        dtos.forEach(d -> {
            if (StringUtils.isNotBlank(d.getTargetsStr())) {
                d.setTargetIds(Arrays.asList(d.getTargetsStr().split(",")));
            }

            if (StringUtils.isNotBlank(d.getMethodStr())) {
                d.setMethods(Arrays.asList(d.getMethodStr().split(",")));
            }
        });

        return dtos;
    }


    /**
     * 查询所有的
     * @return
     */
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_10M, value = CACHE_VAL_ALL_URLAC)
    public List<UrlAccessCtlDto> pageUrlAccessCtlDtos() {
        List<UrlAccessCtlDto> dtos = urlACMapper.listAllUrlAccessCtlDtos();
        //Map<String, List<UrlAccessCtlDto>> urlAcDtoMap = new HashMap<>();

        dtos.forEach(d -> {
            if (StringUtils.isNotBlank(d.getTargetsStr())) {
                d.setTargetIds(Arrays.asList(d.getTargetsStr().split(",")));
            }

            if (StringUtils.isNotBlank(d.getMethodStr())) {
                d.setMethods(Arrays.asList(d.getMethodStr().split(",")));
            }
        });

        return dtos;
    }





    @Transactional(rollbackFor = Exception.class)
    public void saveUrlAccessCtl(List<ImportUrlAuth> importUrlAuths) {
        if (CollectionUtil.isNotEmpty(importUrlAuths)) {
            List<UrlACModel> urlACModels = new ArrayList<>();
            List<UrlACTargetModel> urlACTargetModels = new ArrayList<>();
            for (ImportUrlAuth importUrlAuth : importUrlAuths) {

                String urlacId = TextUtils.uuid();
                UrlACModel urlACModel = UrlACModel.builder()
                        .url(importUrlAuth.getUrl())
                        .methods(importUrlAuth.getMethod().toLowerCase())
                        .desc(importUrlAuth.getMenuDesc())
                        .build();
                urlACModel.setId(urlacId);

                UrlACTargetModel urlACTargetModel = UrlACTargetModel.builder()
                        .urlacId(urlacId)
                        .target(importUrlAuth.getMenuId())
                        .build();


                urlACModels.add(urlACModel);
                urlACTargetModels.add(urlACTargetModel);
            }

            urlACMapper.bulkCreate(urlACModels);
            urlACTargetMapper.bulkCreate(urlACTargetModels);
        }

    }




}
