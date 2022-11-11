package cn.cyberpeace.mp.flow.v2.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/11
 */
@Slf4j
@Service
public class FlowLoadService {
    private static final double standardInterval = 6;
    private static final String RANDOM = "random";
    private static final String BINOMIAL = "binomial";
    private static final String UNIFORMITY = "uniformity";
    private static final String INDEX = "index";
    private static final String GAUSS = "gauss";

    public List<LoadInfo> calculateLoad(int time, double maxLoad, String loadMode) {
        List<LoadInfo> loadInfos = new ArrayList<>();
        if (StringUtils.isNotEmpty(loadMode)) {
            if (loadMode.equals(BINOMIAL)) {
                binomialDistributionLoad(loadInfos, time, 0.5, maxLoad);
            } else if (loadMode.equals(GAUSS)) {
                normalDistributionLoad(loadInfos, time, maxLoad);
            } else if (loadMode.equals(RANDOM)) {
                randomLoad(loadInfos, time, maxLoad);
            } else if (loadMode.equals(UNIFORMITY)) {
                uniformityLoad(loadInfos, time, maxLoad);
            } else if (loadMode.equals(INDEX)) {
                indexLoad(loadInfos, time, maxLoad);
            }
        } else {
            log.info("LoadMode can not be empty!");
        }
        return loadInfos;
    }

    private void binomialDistributionLoad(List<LoadInfo> loadInfos, int time, double p, double maxLoad) {
        BinomialDistribution binomialDistribution = new BinomialDistribution(time, p);
        double proportion = maxLoad / binomialDistribution.probability(time / 2);
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setBandwidth((int) (binomialDistribution.probability(i) * proportion));
            loadInfo.setTime(i);
            checkBandWidth(loadInfo);
            loadInfos.add(loadInfo);
        }
    }

    private void normalDistributionLoad(List<LoadInfo> loadInfos, int time, double maxLoad) {
        NormalDistribution normalDistribution = new NormalDistribution();
        double interval = standardInterval / time;
        double index = -(standardInterval / 2);
        double proportion = maxLoad / normalDistribution.density(0);
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setTime(i);
            loadInfo.setBandwidth((int) (normalDistribution.density(index) * proportion));
            checkBandWidth(loadInfo);
            loadInfos.add(loadInfo);
            index += interval;
        }
    }

    private void randomLoad(List<LoadInfo> loadInfos, int time, double maxLoad) {
        double load = maxLoad;
        if (maxLoad <= 0) {
            load = 100;
        }
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setTime(i);
            loadInfo.setBandwidth((int) (Math.random() * load + 1));
            checkBandWidth(loadInfo);
            loadInfos.add(loadInfo);
        }
    }

    private void uniformityLoad(List<LoadInfo> loadInfos, int time, double maxLoad) {
        double load = maxLoad;
        if (maxLoad <= 0) {
            load = 100;
        }
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setTime(i);
            loadInfo.setBandwidth((int) (load));
            checkBandWidth(loadInfo);
            loadInfos.add(loadInfo);
        }
    }

    private void indexLoad(List<LoadInfo> loadInfos, int time, double maxLoad) {
        double load = maxLoad;
        if (maxLoad <= 1) {
            load = 100;
        }
        double border = Math.log(load);
        double interval = border / time;
        double index = 0;
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setTime(i);
            loadInfo.setBandwidth((int) (Math.exp(index)));
            checkBandWidth(loadInfo);
            loadInfos.add(loadInfo);
            index += interval;
        }
    }

    private void checkBandWidth(LoadInfo loadInfo) {
        if (loadInfo.getBandwidth() <= 0) {
            loadInfo.setBandwidth(1);
        }
    }

    @Getter
    @Setter
    public static class LoadInfo {
        private Integer time;
        private Integer bandwidth;
    }
}
