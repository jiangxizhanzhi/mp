package cn.cyberpeace.mp.front.business.service;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.front.business.dto.ImportUrlAuth;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.front.business.mapper.UrlACMapper;
import cn.cyberpeace.mp.front.business.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.front.business.model.UrlACModel;
import cn.cyberpeace.mp.front.business.model.UrlACTargetModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:52
 */
@Slf4j
@Service
public class UrlACService {

    @Autowired
    private UrlACMapper urlACMapper;

    @Autowired
    private UrlACTargetMapper urlACTargetMapper;

    public List<UrlAccessCtlDto> listAllUrlAccessCtlDtos() {

        return null;
    }


    public void saveUrlAccessCtl(List<ImportUrlAuth> importUrlAuths) {
        if (CollectionUtil.isNotEmpty(importUrlAuths)) {
            List<UrlACModel> urlACModels = new ArrayList<>();
            List<UrlACTargetModel> urlACTargetModels = new ArrayList<>();
            for (ImportUrlAuth importUrlAuth : importUrlAuths) {

                String urlACId = TextUtils.uuid();
                UrlACModel urlACModel = UrlACModel.builder()
                        .url(importUrlAuth.getUrl())
                        .methods(importUrlAuth.getMethod())
                        .desc(importUrlAuth.getMenuDesc())
                        .build();
                urlACModel.setId(urlACId);

                UrlACTargetModel urlACTargetModel = UrlACTargetModel.builder()
                        .urlACId(urlACId)
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
