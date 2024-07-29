package cn.cyberpeace.mp.devopscluster.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 共性平台方法
 */
@Mapper
@Repository
public interface GXPTTargetMapper {

    List<String> findIdsByUuids(@Param("param") List<String> imageIds);

}
