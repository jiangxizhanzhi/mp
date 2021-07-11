package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.bo.SuricataData;
import cn.cyberpeace.mp.situationaware.model.SuricataRuleModel;
import cn.cyberpeace.mp.situationaware.repository.SuricataRuleMapper;
import cn.cyberpeace.mp.situationaware.request.SuricataQueryRequest;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.situationaware.constant.SuricataConstant.*;


@Slf4j
@Service
public class SuricataService {


    @Resource
    private SuricataRuleMapper suricataRuleMapper;

    
    
    public boolean handlerRuleFile(MultipartFile file) {
        
        String originalFileName = file.getOriginalFilename();
        String fileType = originalFileName.substring(Objects.requireNonNull(originalFileName).lastIndexOf("."));
        if (!".rules".equals(fileType)) {
            log.error("Suricata rules type error! ");
            return false;
        }
        List<String> ruleStrList = queryRulesString(file);
        
        List<SuricataData> ruleList = parse(ruleStrList);
        
        List<SuricataRuleModel> convertRuleList = convertRuleList(ruleList);
        
        return importData(convertRuleList);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean importData(List<SuricataRuleModel> convertRuleList){
        int num = suricataRuleMapper.filterDelete(new HumanCondition()
            .filterGreaterThan(SuricataRuleModel::getPk, SURICATA_RULE_FIELD_VALUE_PK_FIRST));
        log.info("delete suricataRule result num is {}",num);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        BaseModelMapper.BulkResult<SuricataRuleModel> bulkResult =
            suricataRuleMapper.bulkCreate(convertRuleList,1000,true);
        stopWatch.stop();
        log.info("import spend time is {}",stopWatch.getTotalTimeMillis());
        return bulkResult.getSuccessCount() > 0;
    }

    
    private List<SuricataRuleModel> convertRuleList(List<SuricataData> ruleList){
        return ruleList.stream().map(rule -> {
            return SuricataRuleModel.builder().action(rule.getAction()).protocol(rule.getProtocol())
                .source(rule.getSource()).sourcePorts(rule.getSourcePorts()).direction(rule.getDirection())
                .destination(rule.getDestination()).destinationPorts(rule.getDestinationPorts())
                .ruleMsg(rule.getRuleMsg()).ruleName(rule.getRuleName()).sid(rule.getSid()).attck(rule.getAttck())
                .build();
        }).collect(Collectors.toList());

    }


    
    public List<SuricataData> parse(List<String> ruleStrList){
        System.out.println(ruleStrList.size());
        if(CollectionUtils.isEmpty(ruleStrList)){
            return Collections.emptyList();
        }
        
        ruleStrList = ruleStrList.stream().filter(rule -> !rule.startsWith(STARTWITH_STR)).collect(Collectors.toList());
        List<SuricataData> ruleList = new ArrayList<>();
        ruleStrList.stream().forEach(rule->{
            int msgIndex = rule.indexOf(MSG_FLAG_STR);
            
            String aStr = rule.substring(0, msgIndex);
            String[] split = aStr.split(SPACE_REGEX);
            
            String ruleMsg = rule.substring(msgIndex);
            
            SuricataData suricataData = SuricataData.builder().action(split[SURICATA_FIELD.ACTION.ordinal()])
                .protocol(split[SURICATA_FIELD.PROTOCOL.ordinal()]).source(split[SURICATA_FIELD.SOURCE.ordinal()])
                .sourcePorts(split[SURICATA_FIELD.SOURCE_PORTS.ordinal()])
                .direction(split[SURICATA_FIELD.DIRECTION.ordinal()])
                .destination(split[SURICATA_FIELD.DESTINATION.ordinal()])
                .destinationPorts(split[SURICATA_FIELD.DESTINATION_PORTS.ordinal()]).ruleMsg(ruleMsg).build();
            
            parseMsg(suricataData,ruleMsg);
            ruleList.add(suricataData);
        });
        return ruleList;
    }

    
    public void parseMsg(SuricataData suricataData,String ruleMsg){
        if(StringUtils.isEmpty(ruleMsg)){
            return;
        }
        String[] msgSplit = ruleMsg.split(MSG_SEPERATOR);
        
        Arrays.stream(msgSplit).forEach(msg -> {
            if(msg.startsWith(MSG_FLAG_STR)){
                String name = StringUtils.stripStart(msg, MSG_FLAG_STR);
                
                suricataData.setRuleName(name.replace("\"",""));
            } else if (msg.startsWith(SID_PREFIX)) {
                String sid = StringUtils.stripStart(msg, SID_PREFIX);
                suricataData.setSid(sid);
            } else if (msg.startsWith(METADATA_PREFIX)) {
                
                String metadata = StringUtils.stripStart(msg, METADATA_PREFIX);
                String[] metainfos = metadata.split(METADATA_SEPERATOR);
                Arrays.stream(metainfos).forEach(meta->{
                    if(meta.startsWith(ATTCK_PREFIX)){
                        String mid = StringUtils.stripStart(meta,ATTCK_PREFIX).trim();
                        suricataData.setAttck(mid);
                    }
                });
            }
        });
    }


    
    public List<String> queryRulesString(MultipartFile file){
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8));
            String rule;
            List<String> rules = new ArrayList<>();
            while ((rule = bufferedReader.readLine()) != null) {
                rules.add(rule);
            }
            return rules;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(!Objects.isNull(bufferedReader)){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    
    public ServiceResult<List<SuricataData>> queryPage(SuricataQueryRequest suricataQueryRequest) {
        suricataQueryRequest.ensure();
        
        HumanCondition condition = new HumanCondition();
        if(StringUtils.isNotEmpty(suricataQueryRequest.getProtocol())){
            condition.filterEqual(SuricataRuleModel::getProtocol, suricataQueryRequest.getProtocol());
        }
        if(StringUtils.isNotEmpty(suricataQueryRequest.getRuleName())){
            condition.filterContains(SuricataRuleModel::getRuleName, suricataQueryRequest.getRuleName());
        }
        if(StringUtils.isNotEmpty(suricataQueryRequest.getAttck())){
            condition.filterContains(SuricataRuleModel::getAttck, suricataQueryRequest.getAttck());
        }
        
        int total = suricataRuleMapper.total(condition);
        if(total == 0){
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), total)).asResult();
        }
        
        condition.pagination(suricataQueryRequest.getPage(), suricataQueryRequest.getPageSize());
        
        List<SuricataRuleModel> suricataRuleModelList = suricataRuleMapper.list(condition);
        
        List<SuricataData> result = suricataRuleModelList.stream().map(rule -> {
            return SuricataData.builder().action(rule.getAction()).protocol(rule.getProtocol()).source(rule.getSource())
                .sourcePorts(rule.getSourcePorts()).direction(rule.getDirection()).destination(rule.getDestination())
                .destinationPorts(rule.getDestinationPorts()).ruleMsg(rule.getRuleMsg()).ruleName(rule.getRuleName())
                .sid(rule.getSid()).attck(rule.getAttck()).build();
        }).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(result, total)).asResult();
    }

    
    public ServiceResult<List<String>> queryAllProtocols() {
        
        HumanCondition condition = new HumanCondition();
        
        condition.select(SuricataRuleModel::getProtocol).distinct();
        List<SuricataRuleModel> ruleModels = suricataRuleMapper.list(condition);
        List<String> result = ruleModels.stream().filter(rule -> StringUtils.isNotEmpty(rule.getProtocol()))
            .map(SuricataRuleModel::getProtocol).collect(Collectors.toList());
        return new ServiceResult<>(result).asResult();
    }
}
