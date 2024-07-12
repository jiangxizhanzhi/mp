 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name POLogoUtils.java.
 // File size 1571.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName POLogoUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class POLogoUtils {

    /**
     * 对象path批量转fdfs地址
     * @param targetList
     * @param <T>
     */
    public static <T> void changeLogoPathToUrl(List<T> targetList) {
        if (CollectionUtils.isNotEmpty(targetList)) {
            List<LocalFileMappingModel> mappingModelList = DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.TARGET.getGroup()));
            for (T target : targetList) {
                try {
                    Class<?> curClass = target.getClass();
                    Field logoField = curClass.getDeclaredField("logo");
                    logoField.setAccessible(true);
                    String logo = logoField.get(target).toString();
                    String url = DefaultImgUtil.pickPathToFdfsUrl(logo, mappingModelList);
                    logoField.set(target, url);
                } catch (Exception e) {
                    log.error("changePathToUrl error for {}:", target, e);
                }
            }
        }
    }
    
}
