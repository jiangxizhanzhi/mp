package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.SysDeployLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/05/31 16:13
 */
@Mapper
@Repository
@ModelClass(SysDeployLogModel.class)
public interface SysDeployLOgMapper extends BaseModelMapper<SysDeployLogModel> {

}
