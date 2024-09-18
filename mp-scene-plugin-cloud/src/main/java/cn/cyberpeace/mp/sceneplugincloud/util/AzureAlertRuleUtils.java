package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.model.AlarmConditionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.azure.core.management.Region;
import com.azure.resourcemanager.monitor.fluent.MonitorClient;
import com.azure.resourcemanager.monitor.fluent.ScheduledQueryRulesClient;
import com.azure.resourcemanager.monitor.fluent.models.ScheduledQueryRuleResourceInner;
import com.azure.resourcemanager.monitor.models.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AzureAlertRuleUtils {

    /**
     * 创建报警规则
     * @param resourceService
     * @param alertRuleBOList
     * @param groupName
     * @param resourceId
     * @param resourceName
     */
    public static void createAlertRules(IAzureResourceManager resourceService,List<AlertRuleBO> alertRuleBOList,
                                        String groupName, String resourceId, String resourceName) {
        if (alertRuleBOList!=null && alertRuleBOList.size() > 0) {
            for (AlertRuleBO alertRuleBO : alertRuleBOList) {
                List<AlarmConditionModel> alarmConditionList = alertRuleBO.getAlarmConditionList();
                AlarmConditionModel alarmConditionModel = alarmConditionList.get(0);
                if (alarmConditionModel != null && alarmConditionModel.getType().equals("Custom Log Search")) {
                    ArrayList<Condition> conditions = new ArrayList<>();
                    Condition condition = new Condition()
                            .withQuery(alarmConditionModel.getCondition())
//                默认计数
                            .withTimeAggregation(TimeAggregation.COUNT)
                            .withOperator(ConditionOperator.fromString(alarmConditionModel.getOperator()))
                            .withThreshold(alarmConditionModel.getThreshold());
                    conditions.add(condition);

                    ScheduledQueryRuleCriteria criteria = new ScheduledQueryRuleCriteria();
                    criteria.withAllOf(conditions);

//                      TODO 暂默认30分钟
                    long windowSize = 1800;
                    Duration frequency = Duration.parse(alarmConditionModel.getFrequency());
                    long frequencyMin = frequency.getSeconds();
                    if (frequencyMin > windowSize) {
                        windowSize = frequencyMin;
                    }

                    ScheduledQueryRuleResourceInner rule = new ScheduledQueryRuleResourceInner();
                    rule.withCriteria(criteria);
                    rule.withSeverity(AlertSeverity.fromLong(AlertRuleBO.Grade.getAzureCodeByCode(alertRuleBO.getAlarmLevel())));
//        聚合粒度
                    rule.withWindowSize(Duration.ofSeconds(windowSize));
//        评估频率
                    rule.withEvaluationFrequency(Duration.parse(alarmConditionModel.getFrequency()));
                    rule.withLocation(Region.ASIA_EAST.name());
                    rule.withScopes(Arrays.asList(resourceId));
                    MonitorClient monitorClient = resourceService.getMonitorManager().serviceClient();
                    ScheduledQueryRulesClient scheduledQueryRules = monitorClient.getScheduledQueryRules();
                    try {
                        scheduledQueryRules.createOrUpdate(groupName, alertRuleBO.getName() + "-" + resourceName, rule);
                    } catch (Exception e) {
                        log.error(" ScheduledQueryAlert create erro,ScheduledQueryAlertName: {}", alertRuleBO.getName());
                    }

                } else if (alarmConditionModel != null) {

                    // 创建监视警报
                    MetricAlert.DefinitionStages.WithCriteriaDefinition withCriteriaDefinition = resourceService.getManager().alertRules().metricAlerts()
//                预警规则名称
                            .define(alertRuleBO.getName() + "-" + resourceName)
//                资源组
                            .withExistingResourceGroup(groupName)
//                虚拟机id
                            .withTargetResource(resourceId)
//                回溯期 5分钟
                            .withPeriod(Duration.ofMinutes(15))
//                检查间隔
                            .withFrequency(Duration.ofMinutes(5))
//                严重性，预警规则说明
                            .withAlertDetails(AlertRuleBO.Grade.getAzureCodeByCode(alertRuleBO.getAlarmLevel()), alertRuleBO.getAlarmDescription())
//                选择操作组
//                .withActionGroups("/subscriptions/C87BA926-F7C2-4D53-8B8E-89245DC92E26/resourceGroups/6b987034b1f8490abc635ccdf9753c02/providers/microsoft.insights/actionGroups/RecommendedAlertRules-AG-1")
                            .withActionGroups();
                    MetricAlert.DefinitionStages.WithCreate attach = withCriteriaDefinition
//                定义预警条件
                            .defineAlertCriteria(alarmConditionModel.getType())
//                设置要监控的度量标准名称，即 "Available Memory Bytes"（可用内存字节）
                            .withMetricName(alarmConditionModel.getType())
//                设置预警条件：
//                - 使用的时间聚合类型为 "MINIMUM"（最小值）
//                - 条件是小于293,601,280字节（约280MB）的内存
                            .withCondition(MetricAlertRuleTimeAggregation.fromString(alarmConditionModel.getAggregationType()),
                                    MetricAlertRuleCondition.fromString(alarmConditionModel.getOperator()),
                                    alarmConditionModel.getThreshold())
                            .attach();
                    if (alarmConditionList.size() > 1) {
                        for (int i = 1; i < alarmConditionList.size(); i++) {
                            alarmConditionModel = alarmConditionList.get(i);
                            attach
                                    .defineAlertCriteria(alarmConditionModel.getType())
                                    .withMetricName(alarmConditionModel.getType())
                                    .withCondition(MetricAlertRuleTimeAggregation.fromString(alarmConditionModel.getAggregationType()),
                                            MetricAlertRuleCondition.fromString(alarmConditionModel.getOperator()),
                                            alarmConditionModel.getThreshold())
                                    .attach();
                        }
                    }
                    try {
                        attach.create();
                    } catch (Exception e) {
                        log.error(" MetricAlert create erro,MetricAlertName: {}", alertRuleBO.getName());
                    }
                }
            }
        }
    }
}
