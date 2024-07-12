 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EsCollectionDataStorageMapper.java.
 // File size 897.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.EsCollectionDataStorageModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @ClassName EsCollectionDataStorageMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(EsCollectionDataStorageModel.class)
public interface EsCollectionDataStorageMapper extends BaseModelMapper<EsCollectionDataStorageModel> {

    List<EsCollectionDataStorageModel> listByDate(@Param("date")String date);
}
