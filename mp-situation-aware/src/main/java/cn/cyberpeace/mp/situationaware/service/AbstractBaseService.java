package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.utils.LocalDateUtils;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础Service
 * @author wanggang
 */
public abstract class AbstractBaseService {

    private static final String SESSION3 = "sessions3";

    /**
     * 索引
     * @param startDate
     * @param endDate
     * @return
     */
    protected String[] indices(List<String> groupList,LocalDateTime startDate, LocalDateTime endDate){
        List<String> indexList = indexListBuilder(groupList,startDate,endDate);
        var indexArray = new String[indexList.size()];
        return indexList.toArray(indexArray);
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    protected List<String> indexListBuilder(List<String> groupList, LocalDateTime startTime, LocalDateTime endTime) {
        if (CollectionUtils.isEmpty(groupList)) {
            return Lists.newArrayList();
        }
        List<String> dateStringList = LocalDateUtils.fetchFormatterTime(startTime,endTime);
        if (CollectionUtils.isEmpty(dateStringList)) {
            return Lists.newArrayList();
        }
        return groupList
                .stream().map(groupId -> dateStringList.stream().map(dateString -> sessionIndexFormatter(groupId, dateString))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    /**
     * 构造流量索引
     * @param groupId
     * @param dateString dateString
     * @return indexString
     */
    protected String sessionIndexFormatter(String groupId,String dateString) {
        StringBuilder sessionIndexBuilder = new StringBuilder();
        sessionIndexBuilder.append(groupId).append(SESSION3).append("-")
                .append(dateString);
        return sessionIndexBuilder.toString();
    }
}
