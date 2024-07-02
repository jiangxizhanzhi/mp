package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.TrafficGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TrafficGroupModelMapper {
    /**
     * deleteByPrimaryKey
     *
     * @param pk
     * @return int
     */
    int deleteByPrimaryKey(Integer pk);

    /**
     * deleteByTrafficGroupId
     *
     * @param trafficGroupId
     * @return int
     */
    int deleteByTrafficGroupId(@Param("trafficGroupId") String trafficGroupId);

    /**
     * insert
     *
     * @param record
     * @return int
     */
    int insert(TrafficGroupModel record);

    /**
     * insertSelective
     *
     * @param record
     * @return int
     */
    int insertSelective(TrafficGroupModel record);

    /**
     * selectByPrimaryKey
     *
     * @param pk
     * @return
     */
    TrafficGroupModel selectByPrimaryKey(Integer pk);

    /**
     * updateByPrimaryKeySelective
     *
     * @param record
     * @return int
     */
    int updateByPrimaryKeySelective(TrafficGroupModel record);

    /**
     * updateByPrimaryKey
     *
     * @param record
     * @return int
     */
    int updateByPrimaryKey(TrafficGroupModel record);
}