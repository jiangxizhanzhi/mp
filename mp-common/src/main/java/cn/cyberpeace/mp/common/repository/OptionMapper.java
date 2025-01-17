 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionMapper.java.
 // File size 902.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.common.model.OptionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Mapper
@Repository
@ModelClass(OptionModel.class)
public interface OptionMapper extends BaseModelMapper<OptionModel> {

    /**
     * 获取指定类型最大编码值（只有编码为数字的可以用）
     * @param type
     * @return
     */
    Integer getMaxCode(@Param("type") String type);

    /**
     * 获取指定类型最大排序值
     * @param type
     * @return
     */
    Integer getMaxSort(@Param("type") String type);

}
