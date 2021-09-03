package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(AvatarModel.class)
public interface AvatarMapper extends BaseModelMapper<AvatarModel> {

    List<AvatarModel> listAll();

    void deleteAll();
}
