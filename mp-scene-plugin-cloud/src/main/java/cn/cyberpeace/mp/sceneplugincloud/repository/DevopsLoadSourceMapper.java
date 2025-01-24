package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(DevopsLoadSourceModel.class)
public interface DevopsLoadSourceMapper extends BaseModelMapper<DevopsLoadSourceModel> {
    DevopsLoadSourceModel getSynchronousDataSource();

    String getLoadId(@Param("loadId") String loadId);
}
