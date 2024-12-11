package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.VersionLibraryModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author liyahao
* @description 针对表【mp_version_library(软件/固件版本库)】的数据库操作Mapper
* @createDate 2024-12-10 17:08:50
* @Entity cn.cyberpeace.mp.devopscluster.model.MpVersionLibrary
*/
@Mapper
@Repository
@ModelClass(VersionLibraryModel.class)
public interface MpVersionLibraryMapper extends BaseModelMapper<VersionLibraryModel> {

}




