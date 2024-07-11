package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.util.LocaleUtil;
import cn.cyberpeace.mp.situationaware.bo.SuricataData;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.event.SuricataRuleUpdateEventPublisher;
import cn.cyberpeace.mp.situationaware.model.SuricataRuleModel;
import cn.cyberpeace.mp.situationaware.repository.SuricataRuleMapper;
import cn.cyberpeace.mp.situationaware.request.SuricataQueryRequest;
import cn.cyberpeace.mp.situationaware.request.SuricataRuleRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.situationaware.constant.SuricataConstant.STARTWITH_STR;
import static cn.cyberpeace.mp.situationaware.constant.SuricataConstant.SURICATA_RULE_FIELD_VALUE_PK_FIRST;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.service
 * @Author: hhuang
 * @CreateTime: 2023/04/21  09:40
 * @Description: Suricata处理service
 * @Version: 1.0
 */
@Slf4j
@Service
public class SuricataService {


    @Resource
    private SuricataRuleMapper suricataRuleMapper;

    @Resource
    private ClassificationAnalyseService classificationAnalyseService;
    @Resource
    private SuricataRuleUpdateEventPublisher suricataRuleUpdateEventPublisher;

    
    /**
     * @description: 处理规则文件
     *
     * @param file
     * @return boolean
     */
    public boolean handlerRuleFile(MultipartFile file) {
        // 校验文件格式
        String originalFileName = file.getOriginalFilename();
        String fileType = originalFileName.substring(Objects.requireNonNull(originalFileName).lastIndexOf("."));
        if (!".rules".equals(fileType)) {
            log.error("Suricata rules type error! ");
            return false;
        }

        List<String> ruleStrList = queryRulesString(file);
        List<SuricataRuleModel> convertRuleList = parse(ruleStrList);
        //入库
        return importData(convertRuleList);
    }

    /**
     * analyseRule
     *
     * @param metadata
     * @return
     */
    public SuricataData analyseRule(String metadata) {
        try {
            SuricataRuleModel suricataRuleModel = SuricataRuleModel.parseRuleStringToModel(metadata, classificationAnalyseService);
            return convertModelToData(suricataRuleModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(SituationalAwareErrorCode.SURICATA_FORMAT_ERROR);
        }
    }

    /**
     * saveRule
     *
     * @param suricataRuleRequest
     */
    public void saveRule(SuricataRuleRequest suricataRuleRequest) {
        try {
            SuricataRuleModel suricataRuleModel = SuricataRuleModel.parseRuleStringToModel(suricataRuleRequest.getMetadata(), classificationAnalyseService);
            suricataRuleModel.setPk(suricataRuleRequest.getPk());
            suricataRuleModel.setId(suricataRuleRequest.getId());
            suricataRuleModel.setRuleGenType(1);

            if (StringUtils.isEmpty(suricataRuleModel.getId())) {
                suricataRuleMapper.create(suricataRuleModel);
            } else {
                suricataRuleMapper.update(suricataRuleModel);
            }

            //通知更新suricata文件
            suricataRuleUpdateEventPublisher.publishEvent(this.getClass().getName());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(SituationalAwareErrorCode.SURICATA_FORMAT_ERROR);
        }
    }

    /**
     * importData
     *
     * @param convertRuleList
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean importData(List<SuricataRuleModel> convertRuleList){
        int num = suricataRuleMapper.filterDelete(new HumanCondition()
            .filterGreaterThan(SuricataRuleModel::getPk, SURICATA_RULE_FIELD_VALUE_PK_FIRST));
        log.info("delete suricataRule result num is {}",num);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //批量新增
        BaseModelMapper.BulkResult<SuricataRuleModel> bulkResult =
            suricataRuleMapper.bulkCreate(convertRuleList,1000,true);
        stopWatch.stop();
        log.info("import spend time is {}",stopWatch.getTotalTimeMillis());

        //通知更新suricata文件
        suricataRuleUpdateEventPublisher.publishEvent(this.getClass().getName());

        return bulkResult.getSuccessCount() > 0;
    }

    /**
     * convertAllRuleDataToRuleFile
     *
     * @param filePath
     */
    public void convertAllRuleDataToRuleFile(String filePath) {
        try {
            Path targetPath = Path.of(filePath);
            Files.deleteIfExists(targetPath);
            scanRuleDataByPkAndWriteRuleDataToFile(targetPath);
        } catch (IOException e) {
            log.error("初始化suricata规则文件失败");
            log.error(e.getMessage(), e);
        }
    }


    /**
     * scanRuleDataByPkAndInitClassType
     */
    @Transactional(rollbackFor = Exception.class)
    public void scanRuleDataByPkAndInitClassType() {
        scanRuleDataByPkAndDoSomething(this::updateSuricataRuleClassTypeWhenEmpty);
    }

    /**
     * updateSuricataRuleClassTypeWhenEmpty
     *
     * @param suricataRuleModelList
     */
    private void updateSuricataRuleClassTypeWhenEmpty(List<SuricataRuleModel> suricataRuleModelList) {
        List<SuricataRuleModel> emptyClassTypeRules = suricataRuleModelList.stream()
                .filter(ruleModel -> StringUtils.isEmpty(ruleModel.getClassTypeCn()) || StringUtils.isEmpty(ruleModel.getClassTypeEn()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(emptyClassTypeRules)) {
            emptyClassTypeRules.forEach(ruleModel -> {
                String cnClassType = ruleModel.extractCnClassType(classificationAnalyseService);
                String enClassType = ruleModel.extractOriginClassTypeFromRuleOptions();
                ruleModel.setClassTypeCn(cnClassType);
                ruleModel.setClassTypeEn(enClassType);
            });
            suricataRuleMapper.bulkUpdate(emptyClassTypeRules);
        }
    }


    /**
     * scanRuleDataByPkAndWriteRuleDataToFile
     *
     * @param ruleDataPath
     */
    private void scanRuleDataByPkAndWriteRuleDataToFile(Path ruleDataPath) {
        scanRuleDataByPkAndDoSomething((ruleModelList) -> {
            try {
                Files.write(ruleDataPath, convertRuleDataToLines(ruleModelList), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                log.error("扫描数据库表mp_situation_suricatarule生成suricata规则文件失败");
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * scanRuleDataByPkAndDoSomething
     *
     * @param consumer
     */
    private void scanRuleDataByPkAndDoSomething(Consumer<List<SuricataRuleModel>> consumer) {
        int initPk = 0;
        do {
            HumanCondition humanCondition = new HumanCondition().orderBy(SuricataRuleModel::getPk);
            humanCondition.setLimit(1000);
            humanCondition.filterGreaterThan(SuricataRuleModel::getPk, initPk);
            List<SuricataRuleModel> suricataRuleModelList = suricataRuleMapper.list(humanCondition);
            if (CollectionUtils.isEmpty(suricataRuleModelList)) {
                break;
            }

            initPk = suricataRuleModelList.get(suricataRuleModelList.size() - 1).getPk();

            consumer.accept(suricataRuleModelList);

            if (CollectionUtils.isEmpty(suricataRuleModelList) && suricataRuleModelList.size() < 1000) {
                break;
            }
        } while (true);
    }

    /**
     * convertRuleDataToLines
     *
     * @param suricataRuleModelList
     * @return
     */
    private List<String> convertRuleDataToLines(List<SuricataRuleModel> suricataRuleModelList) {
        if (CollectionUtils.isEmpty(suricataRuleModelList)) {
            return Collections.emptyList();
        } else {
            return suricataRuleModelList.stream().map(this::convertRuleDataToFormatRule)
                    .collect(Collectors.toList());
        }
    }

    /**
     * The action, that determines what happens when the signature matches
     * The header, defining the protocol, IP addresses, ports and direction of the rule.
     * The rule options, defining the specifics of the rule.
     *
     *
     *
     * @param ruleData
     * @return
     */
    private String convertRuleDataToFormatRule(SuricataRuleModel ruleData) {
        return ruleData.constructMetadata();
    }


    /**
     * @description: 解析规则数据为规则数据对象
     *
     * @param ruleStrList
     * @return List<SuricataData>
     */
    public List<SuricataRuleModel> parse(List<String> ruleStrList){
        if(CollectionUtils.isEmpty(ruleStrList)){
            return Collections.emptyList();
        }
        ruleStrList = filterCommentRuleData(ruleStrList);

        return ruleStrList.stream().map(rule -> SuricataRuleModel.parseRuleStringToModel(rule, classificationAnalyseService)).collect(Collectors.toList());
    }

    private List<String> filterCommentRuleData(List<String> ruleStrList) {
        return ruleStrList.stream().filter(ruleStr -> !ruleStr.startsWith(STARTWITH_STR)).collect(Collectors.toList());
    }




    /**
     * @description: 从上传文件中获取所有规则
     *
     * @param file
     * @return List<String>
     */
    public List<String> queryRulesString(MultipartFile file){

        List<String> rules = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8))){
            String rule;
            while ((rule = bufferedReader.readLine()) != null) {
                rules.add(rule);
            }
            return rules;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @description: 分页查询流量规则信息
     *
     * @param suricataQueryRequest
     * @return ServiceResult<List<SuricataData>>
     */
    public ServiceResult<List<SuricataData>> queryPage(SuricataQueryRequest suricataQueryRequest) {
        suricataQueryRequest.ensure();
        //构建查询参数
        HumanCondition condition = new HumanCondition();
        condition.orderBy(SuricataRuleModel::getPk, true);

        if(StringUtils.isNotEmpty(suricataQueryRequest.getProtocol())){
            condition.filterEqual(SuricataRuleModel::getProtocol, suricataQueryRequest.getProtocol());
        }
        if(StringUtils.isNotEmpty(suricataQueryRequest.getRuleName())){
            condition.filterContains(SuricataRuleModel::getRuleName, suricataQueryRequest.getRuleName());
        }
        if(StringUtils.isNotEmpty(suricataQueryRequest.getAttck())){
            condition.filterContains(SuricataRuleModel::getAttck, suricataQueryRequest.getAttck());
        }
        if (StringUtils.isNotEmpty(suricataQueryRequest.getClassType())) {
            condition.filterOrStart().orContains(SuricataRuleModel::getClassTypeCn, suricataQueryRequest.getClassType())
                    .orContains(SuricataRuleModel::getClassTypeEn, suricataQueryRequest.getClassType())
                    .filterOrEnd();
        }

        //查询结果总数
        int total = suricataRuleMapper.total(condition);
        if(total == 0){
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), total)).asResult();
        }
        //添加分页信息
        condition.pagination(suricataQueryRequest.getPage(), suricataQueryRequest.getPageSize());
        //执行查询
        List<SuricataRuleModel> suricataRuleModelList = suricataRuleMapper.list(condition);
        //转换响应为业务数据
        List<SuricataData> result = new ArrayList<>();
        for (SuricataRuleModel rule : suricataRuleModelList) {
            SuricataData build = convertModelToData(rule);
            result.add(build);
        }
        return new ListObjServiceResult(new ListObjResult(result, total)).asResult();
    }


    /**
     * convertModelToData
     *
     * @param rule
     * @return
     */
    public static SuricataData convertModelToData(SuricataRuleModel rule) {
        SuricataData build = SuricataData.builder().action(rule.getAction()).protocol(rule.getProtocol()).source(rule.getSource())
                .sourcePorts(rule.getSourcePorts()).direction(rule.getDirection()).destination(rule.getDestination())
                .destinationPorts(rule.getDestinationPorts()).ruleMsg(rule.getRuleMsg()).ruleName(rule.getRuleName())
                .sid(rule.getSid()).attck(rule.getAttck()).pk(rule.getPk()).id(rule.getId()).build();

        if (LocaleUtil.chineseLocalFlag()) {
            build.setClassType(rule.getClassTypeCn());
        } else {
            build.setClassType(rule.getClassTypeEn());
        }

        build.setMetadata(rule.constructMetadata());
        build.setThreshold(rule.extractThreshold());
        build.setReference(rule.extractReferenceUrl());
        build.setRuleGenType(rule.getRuleGenType());
        return build;
    }


    /**
     * @description: 查询规则所有的协议类型
     *
     * @return ServiceResult<List<String>>
     */
    public ServiceResult<List<String>> queryAllProtocols() {
        //构建查询条件
        HumanCondition condition = new HumanCondition();
        //查询协议类型
        condition.select(SuricataRuleModel::getProtocol).distinct();
        List<SuricataRuleModel> ruleModels = suricataRuleMapper.list(condition);
        List<String> result = new ArrayList<>();
        for (SuricataRuleModel rule : ruleModels) {
            if (StringUtils.isNotEmpty(rule.getProtocol())) {
                String protocol = rule.getProtocol();
                result.add(protocol);
            }
        }
        return new ServiceResult<>(result).asResult();
    }

    /**
     * queryAllClassType
     *
     * @return
     */
    public List<String> queryAllClassType() {
        if (LocaleUtil.chineseLocalFlag()) {
            return new ArrayList<>(classificationAnalyseService.getFlowEventEnToCnMap().values());
        } else {
            return new ArrayList<>(classificationAnalyseService.getFlowEventEnToCnMap().keySet());
        }
    }
}
