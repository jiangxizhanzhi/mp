 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseObjMappingService.java.
 // File size 523.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;


public interface BaseObjMappingService<M extends BaseObjMappingModel> {

    ServiceResult<M> getMapping(String group, String name);

    ServiceResult<List<M>> getMappings(String group);

    ServiceResult<List<M>> syncMappings(String group, List<M> mappings);

    ServiceResult<Integer> deleteMappings(String group);

}
