package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.admin.model.ActivEditionMenuModel;
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
@ModelClass(ActivEditionMenuModel.class)
public interface ActivEditionMenuMapper extends BaseModelMapper<ActivEditionMenuModel> {

    List<ActivEditionMenuModel> selectAll();

    int deleteAll();
}
