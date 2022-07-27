package cn.cyberpeace.mp.devops.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
import cn.cyberpeace.mp.devops.log.model.OpTypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Mapper
@Repository
@ModelClass(OpTypeModel.class)
public interface OpTypeMapper extends BaseModelMapper<OpTypeModel> {
}
