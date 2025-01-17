 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileMappingMapper.java.
 // File size 429.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(LocalFileMappingModel.class)
public interface LocalFileMappingMapper extends BaseObjMappingMapper<LocalFileMappingModel> {
}
