package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.common.model.DropdownModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Mapper
@Repository
@ModelClass(DropdownModel.class)
public interface DropdownMapper extends BaseModelMapper<DropdownModel> {
}
