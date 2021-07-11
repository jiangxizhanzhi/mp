package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(MenuExtraInfoModel.class)
public interface MenuExtraInfoMapper extends BaseModelMapper<MenuExtraInfoModel> {

    List<MenuExtraInfoModel> listAll();

    int deleteAll();
}
