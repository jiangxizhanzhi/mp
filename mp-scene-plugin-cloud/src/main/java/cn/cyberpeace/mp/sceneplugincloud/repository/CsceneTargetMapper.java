package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.bo.CsceneTargetBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(CsceneTargetBO.class)
public interface CsceneTargetMapper extends BaseModelMapper<CsceneTargetBO> {
    CsceneTargetBO  findByEid(@Param("ids") String ids);
}
