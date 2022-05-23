package cn.cyberpeace.mp.front.business.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.front.business.controller.UrlACController;
import cn.cyberpeace.mp.front.business.dto.ImportUrlAuth;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlRequest;
import cn.cyberpeace.mp.front.business.mapper.UrlACMapper;
import cn.cyberpeace.mp.front.business.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.front.business.model.UrlACModel;
import cn.cyberpeace.mp.front.business.model.UrlACTargetModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.yaml.snakeyaml.nodes.CollectionNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:52
 */
@Slf4j
@Service
public class UrlACService {

    public static final String CACHE_VAL_ALL_URLAC = "ALL_URLAC_DTOS";

    public static final String CACHE_VAL_ALL_URLAC_PATTERN = "CACHE_VAL_ALL_URLAC_PATTERN";

    private static final PathMatcher MATCHER = new AntPathMatcher();

    @Autowired
    private UrlACMapper urlACMapper;

    @Autowired
    private UrlACTargetMapper urlACTargetMapper;


    /**
     * 根据targetId批量查询
     * @param targetIds
     * @return
     */
    public List<UrlAccessCtlDto> listByTargetIds(List<String> targetIds) {
        log.info("targetIds:{}", targetIds);
        if (CollectionUtil.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        return urlACMapper.listByTargetIds(targetIds);
    }

    /**
     * 根绝url批量模糊查询
     * @return
     */
    public List<UrlAccessCtlDto> listByUrlFuzzyQuery(List<String> urls) {
        log.info("urls:{}", urls);
        if (CollectionUtil.isEmpty(urls)) {
            return Collections.emptyList();
        }
        return urlACMapper.listByUrls(urls);
    }


    @Transactional(rollbackFor = Exception.class)
    public String add(UrlAccessCtlRequest addReq) {
        String urlacId = TextUtils.uuid();

        UrlACModel urlACModel = UrlACModel.builder()
                .url(addReq.getUrl())
                .build();
        urlACModel.setId(urlacId);

        if (CollectionUtil.isNotEmpty(addReq.getMethods())) {
            urlACModel.setMethods(addReq.getMethods().stream().collect(Collectors.joining(",")));
        }

        urlACMapper.create(urlACModel);

        if (CollectionUtil.isNotEmpty(addReq.getTargetIds())) {
            List<UrlACTargetModel> acTargetModels =
                addReq.getTargetIds().stream().map(tid -> {
                    UrlACTargetModel acTargetModel = UrlACTargetModel.builder()
                            .urlacId(urlacId)
                            .target(tid)
                            .build();
                    return acTargetModel;
                }).collect(Collectors.toList());

            urlACTargetMapper.bulkCreate(acTargetModels);
        }

        return urlacId;
    }




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

        if (CollectionUtil.isNotEmpty(updateReq.getMethods())) {
            urlACModel.setMethods(updateReq.getMethods().stream().collect(Collectors.joining(",")));
        }

        urlACMapper.update(urlACModel);

        urlACTargetMapper.deleteByUrlACId(ArrayUtils.toArray(urlacId));
        if (CollectionUtil.isNotEmpty(updateReq.getTargetIds())) {
            List<UrlACTargetModel> acTargetModels =
                    updateReq.getTargetIds().stream().map(tid -> {
                        UrlACTargetModel acTargetModel = UrlACTargetModel.builder()
                                .urlacId(urlacId)
                                .target(tid)
                                .build();
                        return acTargetModel;
                    }).collect(Collectors.toList());

            urlACTargetMapper.bulkCreate(acTargetModels);
        }

        return urlacId;
    }


    @Transactional(rollbackFor = Exception.class)
    public int delete(String[] ids) {
        int cnt = urlACMapper.bulkDelete(ids);
        urlACTargetMapper.deleteByUrlACId(ArrayUtils.toArray(ids));
        return cnt;
    }

    /**
     * 查询所有的urlac不包括pattern
     * @return
     */
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_10M, value = CACHE_VAL_ALL_URLAC)
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
     * @return
     */
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_10M, value = CACHE_VAL_ALL_URLAC_PATTERN)
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
    public void importByExcel(List<ImportUrlAuth> importUrlAuths) {
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


    public static void main(String[] args) {
        String pattern = "/api/mp/agg/default-img/get-default-img/?defaultImgDirEnum=TARGET";
        String url = "/api/mp/agg/default-img/get-default-img/?defaultImgDirEnum=TARGET";
        boolean match = MATCHER.match(pattern, url);
        System.out.println(match);
    }




}
