package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import cn.cyberpeace.mp.scene.repository.AlertRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlertRuleService {
    @Autowired
    private AlertRuleMapper alertRuleMapper;

    public void saveAlertRule(List<AlertRuleModel> alertRuleModels) {
        alertRuleMapper.bulkCreate(alertRuleModels);
    }
}
