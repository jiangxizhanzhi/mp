package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.SceneSwitchInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description 截屏文件管理mapper
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Mapper
@Repository
@ModelClass(SceneSwitchInfoModel.class)
public interface SceneSwitchInfoMapper extends BaseModelMapper<SceneSwitchInfoModel> {


	
}
