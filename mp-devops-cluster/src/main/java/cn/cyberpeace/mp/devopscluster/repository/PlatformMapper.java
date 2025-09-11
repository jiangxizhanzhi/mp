package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
@Mapper
@Repository
@ModelClass(PlatformModel.class)
public interface PlatformMapper extends BaseModelMapper<PlatformModel> {
}
