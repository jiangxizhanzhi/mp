package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.service.constant.ProbeOperateConstant;
import cn.cyberpeace.mp.cscene.service.param.ProbeOperateQueryParams;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class RangeService {
    @Autowired
    private CsceneInstanceService instanceService ;

    @Autowired
    private ProbeOperateLogService probeOperateLogService ;

    public List<Range> getRange(String sceneId) {
        var start = instanceService.get(sceneId).getSource().getObj().getCreateTime();
        ProbeOperateQueryParams params = new ProbeOperateQueryParams();
        params.setScene(sceneId);
        params.setType(ProbeOperateConstant.PROBEOPERATE_TYPE.COLLECT_PERIOD.getValue());
        var periods = probeOperateLogService.queryOperateProbe(params);
        if (CollectionUtils.isEmpty(periods)) {
            return new ArrayList<>();
        } else {
            List<Range> ranges = new ArrayList<>();

            Range first = new Range();
            first.setPeriod(-1);
            first.setStart(start);
            first.setEnd(periods.get(0).getCreateTime());
            ranges.add(first);

            for (int i = 0; i < periods.size(); i++) {
                Range range = new Range();
                range.setStart(periods.get(i).getCreateTime());
                range.setPeriod(periods.get(i).getCollectPeriod());
                if (i == periods.size() - 1) {
                    // 计算距当前时间最近的周期点
                    var periodNums = (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - range.getStart().toEpochSecond(ZoneOffset.UTC)) / range.getPeriod();
                    var timePoint = range.getStart().plusSeconds(periodNums * range.getPeriod());
                    range.setEnd(timePoint);
                } else {
                    range.setEnd(periods.get(i + 1).getCreateTime());
                }
                ranges.add(range);
            }
            return ranges;
        }
    }

    public LocalDateTime getTime(LocalDateTime time, List<Range> ranges) {
        for (int i = ranges.size() - 1; i >= 0; i--) {
            var range = ranges.get(i);
            if (time.isAfter(range.getStart()) && (time.isBefore(range.getEnd()) || time.isEqual(range.getEnd()))) {
                if (range.getPeriod() == -1) {
                    return time;
                } else {
                    int periodNums = BigDecimal.valueOf((time.toEpochSecond(ZoneOffset.UTC) - range.getStart().toEpochSecond(ZoneOffset.UTC)))
                            .divide(BigDecimal.valueOf(range.getPeriod()), 5, RoundingMode.DOWN)
                            .intValue();
                    return range.getStart().plusSeconds((periodNums + 1) * range.getPeriod());
                }
            }
        }
        return ranges.get(0).getEnd();
    }

    @Data
    public static class Range {
        private Integer period;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
