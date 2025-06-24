 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACService.java.
 // File size 12609.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.service.aggregation.GroupConcat;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.frontbusiness.dto.ImportUrlAuth;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAcCtlGroupBo;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.frontbusiness.mapper.UrlACMapper;
import cn.cyberpeace.mp.frontbusiness.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.frontbusiness.model.UrlACModel;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;
import cn.cyberpeace.mp.frontbusiness.request.urlac.UrlAccessCtlRequest;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

 /**
  * url acservice
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_LONG)
public class UrlACService {

     /**
      * fa
      */
     public static final String CACHE_VAL_ALL_URLAC = "ALL_URLAC_DTOS";

     /**
      * fa
      */
     public static final String CACHE_VAL_ALL_URLAC_PATTERN = "CACHE_VAL_ALL_URLAC_PATTERN";

     /**
      * 匹配器
      */
     private static final PathMatcher MATCHER = new AntPathMatcher();

     /**
      * url acmapper
      */
     @Autowired
    private UrlACMapper urlACMapper;

     /**
      * Url目标映射器
      */
     @Autowired
    private UrlACTargetMapper urlACTargetMapper;

     /**
      * urlactarget服务
      */
     @Autowired
    private UrlACTargetService urlACTargetService;

     /**
      * 根据targetId批量查询
      *
      * @param targetIds
      * @return
      */
     public List<UrlAccessCtlDto> listByTargetIds(List<String> targetIds) {
        if (CollUtil.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        List<UrlAccessCtlDto> dtos = urlACMapper.listByTargetIds(targetIds);
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
      * 根据url模糊查询
      *
      * @param url url
      * @return
      */
     public List<UrlAccessCtlDto> listByUrlFuzzyQuery(String url) {
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        return listByUrl(url);
    }

     /**
      * 根据 group 查询
      *
      * @param group 集团
      * @return
      */
     public List<UrlAcCtlGroupBo> listByGroupQuery(String group) {
        if (StringUtils.isBlank(group)) {
            return Collections.emptyList();
        }
        return listByGroup(group);
    }

     /**
      * 添加
      *
      * @param addReq 添加请求
      * @return {@link String }
      */
     @Transactional(rollbackFor = Exception.class)
    public String add(UrlAccessCtlRequest addReq) {
        String urlacId = TextUtils.uuid();

        UrlACModel urlACModel = UrlACModel.builder()
                .url(addReq.getUrl())
                .build();
        urlACModel.setId(urlacId);

        if (MATCHER.isPattern(addReq.getUrl())) {
            urlACModel.setIsPattern(1);
        }

        if (CollUtil.isNotEmpty(addReq.getMethods())) {
            urlACModel.setMethods(addReq.getMethods().stream().collect(Collectors.joining(",")));
        }

        boolean success = urlACMapper.create(urlACModel);
        if (!success) {
            throw new MpRuntimeException("save urlac failed!");
        }

        if (CollUtil.isNotEmpty(addReq.getTargetIds())) {
            List<UrlACTargetModel> acTargetModels =
                addReq.getTargetIds().stream().map(tid ->
                    UrlACTargetModel.builder()
                            .urlacId(urlacId)
                            .target(tid)
                            .build()
                ).collect(Collectors.toList());

            urlACTargetMapper.bulkCreate(acTargetModels);
        }

        return urlacId;
    }

     /**
      * 修改
      *
      * @param updateReq 更新请求
      * @return {@link String }
      */
     @Transactional(rollbackFor = Exception.class)
    public String modify(UrlAccessCtlRequest updateReq) {
        String urlacId = updateReq.getId();
        //UrlAccessCtlDto urlAccessCtlDto = urlACMapper.getById(urlacId);
        //if (Objects.isNull(urlAccessCtlDto)) {
        //    throw new MpRuntimeException("Not exist");
        //}

        UrlACModel urlACModel = UrlACModel.builder()
                .url(updateReq.getUrl())
                .build();
        urlACModel.setId(urlacId);

        if (MATCHER.isPattern(updateReq.getUrl())) {
            urlACModel.setIsPattern(1);
        }

        if (CollUtil.isNotEmpty(updateReq.getMethods())) {
            urlACModel.setMethods(updateReq.getMethods().stream().collect(Collectors.joining(",")));
        }

        boolean success = urlACMapper.update(urlACModel);
        if (!success) {
            throw new MpRuntimeException("update urlac failed!");
        }

         final int i = urlACTargetService.deleteByUrlACId(ArrayUtils.toArray(urlacId));
         if (CollUtil.isNotEmpty(updateReq.getTargetIds())) {
            List<UrlACTargetModel> acTargetModels =
                    updateReq.getTargetIds().stream().map(tid ->
                        UrlACTargetModel.builder()
                                .urlacId(urlacId)
                                .target(tid)
                                .build()
                    ).collect(Collectors.toList());

            urlACTargetMapper.bulkCreate(acTargetModels);
        }

        return urlacId;
    }

     /**
      * 删除
      *
      * @param ids id
      * @return int
      */
     @Transactional(rollbackFor = Exception.class)
    public int delete(String[] ids) {
        int cnt = urlACMapper.bulkDelete(ids).getSuccessCount();
        urlACTargetService.deleteByUrlACId(ArrayUtils.toArray(ids));
        return cnt;
    }

     /**
      * 查询所有的urlac不包括pattern
      *
      * @return
      */
     @Cacheable(value = CACHE_VAL_ALL_URLAC)
    public List<UrlAccessCtlDto> listAllUrlAccessCtlDtos() {
        List<UrlAccessCtlDto> dtos = urlACMapper.listAllUrlAccessCtlDtos();
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
      * 查询所有的pattern
      *
      * @return
      */
     @Cacheable(value = CACHE_VAL_ALL_URLAC_PATTERN)
    public List<UrlAccessCtlDto> listAllUrlACPatternDtos() {
        List<UrlAccessCtlDto> dtos = urlACMapper.listAllUrlAccessCtlPatternDtos();
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
      *
      * @return
      */
     @Cacheable(value = CACHE_VAL_ALL_URLAC)
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

     /**
      * 驱逐urlaccache
      */
     @CacheEvict(value = {CACHE_VAL_ALL_URLAC, CACHE_VAL_ALL_URLAC_PATTERN}, allEntries = true)
    public void evictURLACCache() {
        log.info("evict url ac cache...");
    }

     /**
      * excel导入
      *
      * @param importUrlAuths 导入url授权
      */
     @Transactional(rollbackFor = Exception.class)
    public void importByExcel(List<ImportUrlAuth> importUrlAuths) {
        if (CollUtil.isNotEmpty(importUrlAuths)) {
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
                if (MATCHER.isPattern(importUrlAuth.getUrl())) {
                    urlACModel.setIsPattern(1);
                }

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

     /**
      * @param url url
      * @return {@link List }<{@link UrlAccessCtlDto }>
      */
     private List<UrlAccessCtlDto> listByUrl(String url) {
        HumanCondition cond = new HumanCondition()
                .link(UrlACModel.class, UrlACTargetModel.class)
                .filterLink(UrlACModel.class, UrlACModel::getGroup, UrlACTargetModel.class, UrlACTargetModel::getGroup)
                .currentLink(UrlACModel.class)
                .filterStartsWith(UrlACModel::getUrl, url);
        cond.currentLink(UrlACModel.class)
                .select(UrlACModel::getUrl, UrlACModel::getIsPattern)
                .selectAs(UrlACModel::getMethods, UrlAccessCtlDto.QUERY_FIELD_METHODS)
                .selectAs(GroupConcat.f(UrlACModel::getGroup).distinct(), UrlAccessCtlDto.QUERY_FIELD_GROUPS)
                .currentLink(UrlACTargetModel.class)
                .selectAs(GroupConcat.f(UrlACTargetModel::getTarget).distinct(), UrlAccessCtlDto.QUERY_FIELD_TARGET_IDS)
                .currentLink(UrlACModel.class)
                .groupBy(UrlACModel::getUrl, UrlACModel::getIsPattern, UrlACModel::getMethods);
        return urlACMapper.list(cond, UrlAccessCtlDto.class);
    }

     /**
      * @param group 集团
      * @return {@link List }<{@link UrlAcCtlGroupBo }>
      */
     private List<UrlAcCtlGroupBo> listByGroup(String group) {
        HumanCondition cond = new HumanCondition()
                .link(UrlACModel.class, UrlACTargetModel.class)
                .filterLink(UrlACModel.class, UrlACModel::getGroup, UrlACTargetModel.class, UrlACTargetModel::getGroup)
                .currentLink(UrlACModel.class)
                .filterEqual(UrlACModel::getGroup, group)
                .selectAs(UrlACModel::getGroup, UrlAcCtlGroupBo.QUERY_FIELD_GROUP)
                .selectAs(UrlACModel::getUrl, UrlAcCtlGroupBo.QUERY_FIELD_URL)
                .selectAs(UrlACModel::getIsPattern, UrlAcCtlGroupBo.QUERY_FIELD_IS_PATTERN)
                .selectAs(UrlACModel::getMethods, UrlAcCtlGroupBo.QUERY_FIELD_METHODS)
                .currentLink(UrlACTargetModel.class)
                .selectAs(UrlACTargetModel::getTarget, UrlAcCtlGroupBo.QUERY_FIELD_TARGETS)
                .distinct();
        return urlACMapper.list(cond, UrlAcCtlGroupBo.class);
    }

     /**
      * @param args arg游戏
      */
     public static void main(String[] args) {
        String pattern = "/api/mp/agg/default-img/get-default-img/?defaultImgDirEnum=TARGET";
        String url = "/api/mp/agg/default-img/get-default-img/?defaultImgDirEnum=TARGET";
        boolean match = MATCHER.match(pattern, url);
        System.out.println(match);
    }




}
