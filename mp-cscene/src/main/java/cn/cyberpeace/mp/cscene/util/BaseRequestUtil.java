package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


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


    
    public static void checkBaseRequestWithSceneIdAndElementIds(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> elementIds = baseRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }


    public static void checkBaseRequestWithSceneIdAndElementIds(BaseSceneElementPageRequest pageRequest) {
        if (StringUtils.isBlank(pageRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> elementIds = pageRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }


    
    public static void checkBaseRequestWithSceneId(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }
    }

    public static void checkBaseRequestWithSceneId(BaseSceneElementPageRequest pageRequest) {
        if (StringUtils.isBlank(pageRequest.getSceneId())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }
    }

    public static void processDefaultTimeRange(BaseTimeRangeRequest timeRangeRequest) {
        LocalDateTime now = LocalDateTime.now();

        if (Objects.isNull(timeRangeRequest.getEndTime())) {
            timeRangeRequest.setEndTime(now);
        }
        
        if (Objects.isNull(timeRangeRequest.getStartTime())) {
            timeRangeRequest.setStartTime(now.minusHours(hrsRange));
        }

        if (timeRangeRequest.getStartTime().isAfter(timeRangeRequest.getEndTime())) {
            log.error("start time should not after than end time {}", timeRangeRequest);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
    }


}
