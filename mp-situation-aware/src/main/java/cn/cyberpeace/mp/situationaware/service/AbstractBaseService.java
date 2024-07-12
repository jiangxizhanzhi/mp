 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AbstractBaseService.java.
 // File size 5204.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.utils.LocalDateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基础Service
 * @author wanggang
 */
@Slf4j
/**
 * 抽象基础服务类，提供通用的服务功能。
 */
public abstract class AbstractBaseService {

    /**
     * 会话索引常量，用于构建索引字符串。
     */
    protected static final String SESSION3 = "sessions3";

    /**
     * 默认语言标签，用于当没有指定语言时的默认选择。
     */
    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    /**
     * 消息源，用于获取本地化的消息。
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * 根据组列表和时间范围构建索引数组。
     *
     * @param groupList 组列表，用于生成索引。
     * @param startDate 开始时间，用于确定索引的时间范围。
     * @param endDate 结束时间，用于确定索引的时间范围。
     * @return 索引字符串数组。
     */
    /**
     * 索引
     *
     * @param startDate
     * @param endDate
     * @return
     */
    protected String[] indices(List<String> groupList,LocalDateTime startDate, LocalDateTime endDate){
        if (CollectionUtils.isEmpty(groupList)){
            return new String[]{};
        }
        List<String> indexList = indexListBuilder(groupList,startDate,endDate);
        String[] indexArray = new String[indexList.size()];
        return indexList.toArray(indexArray);
    }

    /**
     * 构建索引列表。
     *
     * @param groupList 组列表，用于生成索引。
     * @param startTime 开始时间，用于确定索引的时间范围。
     * @param endTime 结束时间，用于确定索引的时间范围。
     * @return 索引字符串列表。
     */
    /**
     * 构造查询需要的索引
     *
     * @param groupList
     * @param startTime
     * @param endTime
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
                .stream().map(groupId -> {
                    List<String> list = new ArrayList<>();
                    for (String dateString : dateStringList) {
                        String s = sessionIndexFormatter(groupId, dateString);
                        list.add(s);
                    }
                    return list;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 根据组ID和日期构建会话索引字符串。
     *
     * @param groupId 组ID，用于构建索引。
     * @param dateString 日期字符串，用于构建索引。
     * @return 会话索引字符串。
     */
    /**
     * 构造流量索引
     *
     * @param groupId
     * @param dateString dateString
     * @return indexString
     */
    protected String sessionIndexFormatter(String groupId,String dateString) {
        StringBuilder sessionIndexBuilder = new StringBuilder();
        sessionIndexBuilder.append(groupId).append("_").append(SESSION3).append("-")
                .append(dateString);
        return sessionIndexBuilder.toString();
    }

    /**
     * 根据地域、消息键和参数获取本地化消息。
     *
     * @param locale 地域，用于获取相应语言的消息。
     * @param msgKey 消息键，用于查找消息。
     * @param params 参数，用于消息格式化。
     * @return 本地化消息字符串。
     */
    /**
     * getLocalMessage
     *
     * @param locale
     * @param msgKey
     * @param params
     * @return
     */
    protected String getLocalMessage(Locale locale, String msgKey, String... params) {
        if (Objects.isNull(locale) || locale.toLanguageTag().equals("und")) {
            locale = Locale.forLanguageTag(DEFAULT_LANGUAGE_TAG);
        }
        try {
            return messageSource.getMessage(msgKey, params, locale);
        } catch (Exception e) {
            log.error("Get message by key `{}` params `{}` on locale {} failed: {}", msgKey, params,
                    locale, ExceptionUtils.getStackTrace(e));
            return msgKey;
        }
    }
}
