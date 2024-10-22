package cn.cyberpeace.mp.devopscluster.repository;


import cn.cyberpeace.mp.auth.model.PersonnelManagementModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(AssetManagementModel.class)
public interface AssetManagementMapper extends BaseModelMapper<AssetManagementModel> {


}
