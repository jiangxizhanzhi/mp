package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.TrafficGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TrafficGroupModelMapper {
    int deleteByPrimaryKey(Integer pk);

    int deleteByTrafficGroupId(@Param("trafficGroupId") String trafficGroupId);

    int insert(TrafficGroupModel record);

    int insertSelective(TrafficGroupModel record);

    TrafficGroupModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(TrafficGroupModel record);

    int updateByPrimaryKey(TrafficGroupModel record);
}