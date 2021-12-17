package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.admin.model.ActivKeyMenuModel;
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
@ModelClass(ActivKeyMenuModel.class)
public interface ActivKeyMenuMapper extends BaseModelMapper<ActivKeyMenuModel> {

    List<ActivKeyMenuModel> selectAll();

    int deleteAll();
}
