 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultImgUtil.java.
 // File size 7750.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.util;

 import cn.cyberpeace.mp.base.common.FsCommon;
 import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
 import cn.cyberpeace.mp.base.repository.LocalFileMappingMapper;
 import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
 import cn.cyberpeace.mp.common.DefaultImgDirEnum;
 import cn.cyberpeace.mp.common.constant.AuthConstant;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.collections4.CollectionUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.springframework.stereotype.Component;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;

 /**
  * @ClassName DefaultImgUtil
  * @Description 默认图片工具类
  * @Author qiaokang
  * @Date 2022/9/19 15:49
  * @Version 1.0.0
  */
 @Component
 @Slf4j
 public class DefaultImgUtil2 {

     private static LocalFileMappingMapper localFileMappingMapper;

     /**
      * DefaultImgUtil
      *
      * @param localFileMappingMapper
      */
     public DefaultImgUtil2(LocalFileMappingMapper localFileMappingMapper) {
         DefaultImgUtil2.localFileMappingMapper = localFileMappingMapper;
     }

     /**
      * 文件路径转fdfs的url
      * @param path
      * @return
      */
     public static String pathToFdfsUrl(String path) {
         if (StringUtils.isNotBlank(path) && !FsCommon.isFileServerOuterUrl(path)) {
 //            if (!path.startsWith(AuthConstant.DEFAULT_IMAGE_PREFIX)) {
 //                path = AuthConstant.DEFAULT_IMAGE_PREFIX.concat(path);
 //            }
             HumanCondition humanCondition = new HumanCondition();
             humanCondition.filterEqual(LocalFileMappingModel::getGroup, splitToGroup(path))
                     .filterEqual(LocalFileMappingModel::getName, splitToName(path));
             List<LocalFileMappingModel> fileMappingModels = localFileMappingMapper.list(humanCondition);
             if (CollectionUtils.isNotEmpty(fileMappingModels)) {
                 path = FsCommon.genHttpUrl(fileMappingModels.get(0).getRemoteFile());
             }
         }
         if (!FsCommon.isFileServerOuterUrl(path)) {
             path = FsCommon.genHttpUrl(path);
         }

         return path;
     }

     /**
      * getGroupNamePathMap
      *
      * @return
      */
     public static Map<String, String> getGroupNamePathMap() {
         Map<String, String> rtnMap = new HashMap<>();
         HumanCondition humanCondition = new HumanCondition();
         List<LocalFileMappingModel> fileMappingModels = localFileMappingMapper.list(humanCondition);
         if (CollectionUtils.isNotEmpty(fileMappingModels)) {
             for (LocalFileMappingModel localFileMappingModel : fileMappingModels) {
             }
         }
         return rtnMap;
     }

     /**
      * 批量映射文件中获取fdfs地址
      * @param path
      * @param mappingModelList
      * @return
      */
     public static String pickPathToFdfsUrl(String path, List<LocalFileMappingModel> mappingModelList) {
         if (StringUtils.isNotBlank(path) && !FsCommon.isFileServerOuterUrl(path) &&
                 CollectionUtils.isNotEmpty(mappingModelList)) {
             String mayDefaultPath = path ;
             String group = splitToGroup(mayDefaultPath);
             String name = splitToName(mayDefaultPath);
             Optional<LocalFileMappingModel> mappingModelOptional =
                     Optional.empty();
             for (LocalFileMappingModel model : mappingModelList) {
                 if (model.getGroup().equals(group) && model.getName().equals(name)) {
                     mappingModelOptional = Optional.of(model);
                     break;
                 }
             }

             if (mappingModelOptional.isPresent()) {
                 path = FsCommon.genHttpUrl(mappingModelOptional.get().getRemoteFile());
             }
         }
         if (!FsCommon.isFileServerOuterUrl(path)) {
             path = FsCommon.genHttpUrl(path);
         }

         return path;
     }


     /**
      * 根据分组集合查询映射模型
      * @param groupList
      * @return
      */
     public static List<LocalFileMappingModel> listByGroups(List<String> groupList) {
         List<LocalFileMappingModel> modelList = new ArrayList<>();
         if (CollectionUtils.isNotEmpty(groupList)) {
             if (DefaultImgDirEnum.TARGET.getGroup().equals(groupList.get(0))) {
                 groupList = Arrays.asList(DefaultImgDirEnum.APPLICATION_SYSTEM.getGroup(),
                         DefaultImgDirEnum.NETWORK_EQUIPMENT.getGroup(),DefaultImgDirEnum.OPERATION_SYSTEM.getGroup(),
                         DefaultImgDirEnum.SAFETY_EQUIPMENT.getGroup(), DefaultImgDirEnum.COMMON_TARGET.getGroup());
             }

             HumanCondition humanCondition = new HumanCondition()
             .filterIn(LocalFileMappingModel::getGroup, groupList.toArray());
             List<LocalFileMappingModel> fileMappingModels = localFileMappingMapper.list(humanCondition);
             if (CollectionUtils.isNotEmpty(fileMappingModels)) {
                 modelList = fileMappingModels;
             }
         }

         return modelList;
     }

     /**
      * 根据分组获取路径名
      * @param group
      * @return
      */
     public static String getPathByGroup(String group) {
         for (DefaultImgDirEnum imgDirEnum : DefaultImgDirEnum.values()) {
             if (imgDirEnum.getGroup().equals(group)) {
                 return AuthConstant.DEFAULT_IMAGE_PREFIX.concat(imgDirEnum.getEndPathName());
             }
         }

         return StringUtils.EMPTY;
     }

     /**
      * 根据路径获取分组
      * @param path 不带文件名
      * @return
      */
     public static String getGroupByPath(String path) {
         for (DefaultImgDirEnum imgDirEnum : DefaultImgDirEnum.values()) {
             if (imgDirEnum.getCurrentPath().equals(path) || imgDirEnum.getEndPathName().equals(path)) {
                 return imgDirEnum.getGroup();
             }
         }

         return StringUtils.EMPTY;
     }

     /**
      * path中拆分group
      * @param path
      * @return
      */
     public static String splitToGroup(String path) {
         path = path.substring(0, path.lastIndexOf(AuthConstant.ORG_PATH_SEPARATOR));
         return getGroupByPath(path);
     }

     /**
      * path中拆分文件名
      * @param path
      * @return
      */
     public static String splitToName(String path) {
         return path.substring(path.lastIndexOf(AuthConstant.ORG_PATH_SEPARATOR) + 1);
     }

 }
