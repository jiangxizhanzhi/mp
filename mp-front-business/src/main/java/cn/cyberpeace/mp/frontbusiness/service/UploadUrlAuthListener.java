 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UploadUrlAuthListener.java.
 // File size 3194.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontbusiness.dto.ImportUrlAuth;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

 /**
  * 上传url验证监听器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class UploadUrlAuthListener extends AnalysisEventListener<ImportUrlAuth> {

     /**
      * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
      */
     private static final int BATCH_COUNT = 10000;

     /**
      * 最大一次性导入100条数据
      */
     private static final int MAX_COUNT = 10000;

     /**
      * 列表
      */
     List<ImportUrlAuth> list = new ArrayList<>();

     /**
      * url acservice
      */
     private UrlACService urlACService;

     /**
      * 上传url验证监听器
      *
      * @param urlACService url acservice
      */
     public UploadUrlAuthListener(UrlACService urlACService) {
        this.urlACService = urlACService;
    }

     /**
      * 这个每一条数据解析都会来调用
      *
      * @param data    one row value. Is is same as
      * @param context 上下文
      */
     @Override
    public void invoke(ImportUrlAuth data, AnalysisContext context) {
        list.add(data);

        if (list.size() > MAX_COUNT) {
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format("100"));
        }
    }

     /**
      * 所有数据解析完成了 都会来调用
      *
      * @param context 上下文
      */
     @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

     /**
      * 加上存储数据库
      * http://10.10.10.10/biz/bug-view-4861.html
      * 当前导入模板里面说明的必填项是用户名、姓名、密码， 其他的组织、角色、电话号码没有在导入模板中说必填，
      * 组织不填的话导入后要有默认值。
      * 组织：可以不填， 不填的话默认根组织
      * 角色：必填， 属于已有角色可以导入，不属于已有角色导入失败。
      */
     private void saveData() {
        //List<TargetModel> allTargets = targetService.listAll(AuthConstant.DEFAULT_TENANT, null);
        //List<String> targetIds = allTargets.stream().map(TargetModel::getId).collect(Collectors.toList());
        //List<ImportUrlAuth> list1 = list.stream().filter(u -> !targetIds.contains(u.getMenuId())).collect(Collectors.toList());
        // 校验字段是否合法
        //checkImportUsers(list);
        //
        //userService.importExcel(list);
        urlACService.importByExcel(list);


        log.info("save success！");
    }



}
