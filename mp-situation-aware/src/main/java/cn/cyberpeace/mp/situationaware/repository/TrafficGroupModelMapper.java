 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TrafficGroupModelMapper.java.
 // File size 1393.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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