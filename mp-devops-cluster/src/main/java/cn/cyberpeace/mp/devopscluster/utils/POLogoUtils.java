package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Slf4j
public class POLogoUtils {

    
    public static <T> void changeLogoPathToUrl(List<T> targetList) {
        if (CollectionUtils.isNotEmpty(targetList)) {
            List<LocalFileMappingModel> mappingModelList = DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.TARGET.getGroup()));
            targetList.forEach(target -> {
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
            });
        }
    }
    
}
