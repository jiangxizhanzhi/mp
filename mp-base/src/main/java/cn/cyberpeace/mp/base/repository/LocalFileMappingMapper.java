package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName LocalFileMappingMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseObjMappingMapper
 */
@Mapper
@Repository
@ModelClass(LocalFileMappingModel.class)
public interface LocalFileMappingMapper extends BaseObjMappingMapper<LocalFileMappingModel> {
}
