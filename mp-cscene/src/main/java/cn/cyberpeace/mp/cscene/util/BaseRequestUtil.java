 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseRequestUtil.java.
 // File size 4372.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName BaseRequestUtil
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/1 09:34
 * @Version 1.0.0
 */
@Slf4j
@Component
public class BaseRequestUtil {


    @Value("${mp.cscene.default.hours-range:24}")
    private Integer hoursRange;

    private static Integer hrsRange;

    @PostConstruct
    public void init() {
        hrsRange = hoursRange;
    }


    /**
     * 校验BaseRequest，没有sceneId和elementIds则报错
     *
     * @param baseRequest
     */
    public static void checkBaseRequestWithSceneIdAndElementIds(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> elementIds = baseRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }


    /**
     * checkBaseRequestWithSceneIdAndElementIds
     *
     * @param pageRequest
     */
    public static void checkBaseRequestWithSceneIdAndElementIds(BaseSceneElementPageRequest pageRequest) {
        if (StringUtils.isBlank(pageRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> elementIds = pageRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }


    /**
     * 校验BaseRequest，没有sceneId则报错
     *
     * @param baseRequest
     */
    public static void checkBaseRequestWithSceneId(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }
    }

    /**
     * checkBaseRequestWithSceneId
     *
     * @param pageRequest
     */
    public static void checkBaseRequestWithSceneId(BaseSceneElementPageRequest pageRequest) {
        if (StringUtils.isBlank(pageRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }
    }

    /**
     * processDefaultTimeRange
     *
     * @param timeRangeRequest
     */
    public static void processDefaultTimeRange(BaseTimeRangeRequest timeRangeRequest) {
        LocalDateTime now = LocalDateTime.now();

        if (Objects.isNull(timeRangeRequest.getEndTime())) {
            timeRangeRequest.setEndTime(now);
        }
        // 时间范围默认24小时
        if (Objects.isNull(timeRangeRequest.getStartTime())) {
            timeRangeRequest.setStartTime(now.minusHours(hrsRange));
        }

        if (timeRangeRequest.getStartTime().isAfter(timeRangeRequest.getEndTime())) {
            log.error("start time should not after than end time {}", timeRangeRequest);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
    }


}
