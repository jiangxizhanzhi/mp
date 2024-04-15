package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/8/23 15:33
 */
@Mapper
@Repository
@ModelClass(SysModelVersionModel.class)
public interface SysModelVersionMapper extends BaseModelMapper<SysModelVersionModel> {
}
