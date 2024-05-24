package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementFileCountModel;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementFileListModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ElementFileCountModel.class)
public interface ElementFileCountMapper
        extends BaseModelMapper<ElementFileCountModel> {
}
