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
