package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(ScreenshotModel.class)
public interface ScreenshotMapper extends BaseModelMapper<ScreenshotModel> {

    
    Long queryUsedSpace();

    
    List<ScreenshotModel> queryByCreateTime(String createTime);

    
    List<ScreenshotModel> selectByIds(@Param("ids") List<String> ids);
	
}
