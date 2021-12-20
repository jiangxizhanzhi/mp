package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.admin.model.MenuExtraInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/17 11:13
 */
@Mapper
@Repository
@ModelClass(MenuExtraInfoModel.class)
public interface MenuExtraInfoMapper extends BaseModelMapper<MenuExtraInfoModel> {

    List<MenuExtraInfoModel> selectAll();

    int deleteAll();
}
