package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.bo.SuricataData;
import cn.cyberpeace.mp.sceneplugincollection.model.SuricataRuleModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.SuricataRuleMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.param.SuricataQueryParams;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.sceneplugincollection.constant.SuricataConstant.*;


/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincollection.service
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

    
    /**
     * @description: 处理规则文件
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
        //解析处理
        List<SuricataData> ruleList = parse(ruleStrList);
        //转换
        List<SuricataRuleModel> convertRuleList = convertRuleList(ruleList);
        //入库
        return importData(convertRuleList);
    }

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
        return bulkResult.getSuccessCount() > 0;
    }

    /**
     * @description: 对象SuricataData list转换
     * @param ruleList
     * @throws
     * @return List<SuricataRuleModel>
     */
    private List<SuricataRuleModel> convertRuleList(List<SuricataData> ruleList){
        return ruleList.stream().map(rule -> {
            return SuricataRuleModel.builder().action(rule.getAction()).protocol(rule.getProtocol())
                .source(rule.getSource()).sourcePorts(rule.getSourcePorts()).direction(rule.getDirection())
                .destination(rule.getDestination()).destinationPorts(rule.getDestinationPorts())
                .ruleMsg(rule.getRuleMsg()).ruleName(rule.getRuleName()).sid(rule.getSid()).attck(rule.getAttck())
                .build();
        }).collect(Collectors.toList());

    }


    /**
     * @description: 解析规则数据为规则数据对象
     * @param ruleStrList
     * @return List<SuricataData>
     */
    public List<SuricataData> parse(List<String> ruleStrList){
        System.out.println(ruleStrList.size());
        if(CollectionUtils.isEmpty(ruleStrList)){
            return Collections.emptyList();
        }
        //过滤开头是#的数据
        ruleStrList = ruleStrList.stream().filter(rule -> !rule.startsWith(STARTWITH_STR)).collect(Collectors.toList());
        List<SuricataData> ruleList = new ArrayList<>();
        ruleStrList.stream().forEach(rule->{
            int msgIndex = rule.indexOf(MSG_FLAG_STR);
            //主要信息
            String aStr = rule.substring(0, msgIndex);
            String[] split = aStr.split(SPACE_REGEX);
            //msg结构体信息
            String ruleMsg = rule.substring(msgIndex);
            //msgFlagStr前面的部分处理
            SuricataData suricataData = SuricataData.builder().action(split[SURICATA_FIELD.ACTION.ordinal()])
                .protocol(split[SURICATA_FIELD.PROTOCOL.ordinal()]).source(split[SURICATA_FIELD.SOURCE.ordinal()])
                .sourcePorts(split[SURICATA_FIELD.SOURCE_PORTS.ordinal()])
                .direction(split[SURICATA_FIELD.DIRECTION.ordinal()])
                .destination(split[SURICATA_FIELD.DESTINATION.ordinal()])
                .destinationPorts(split[SURICATA_FIELD.DESTINATION_PORTS.ordinal()]).ruleMsg(ruleMsg).build();
            //解析处理规则结构体
            parseMsg(suricataData,ruleMsg);
            ruleList.add(suricataData);
        });
        return ruleList;
    }

    /**
     * @description: 解析处理规则的msg结构体
     * @param suricataData
     * @param ruleMsg
     * @return void
     */
    public void parseMsg(SuricataData suricataData,String ruleMsg){
        if(StringUtils.isEmpty(ruleMsg)){
            return;
        }
        String[] msgSplit = ruleMsg.split(MSG_SEPERATOR);
        //获取先获取rulename，sid
        Arrays.stream(msgSplit).forEach(msg -> {
            if(msg.startsWith(MSG_FLAG_STR)){
                String name = StringUtils.stripStart(msg, MSG_FLAG_STR);
                //去除引号
                suricataData.setRuleName(name.replace("\"",""));
            } else if (msg.startsWith(SID_PREFIX)) {
                String sid = StringUtils.stripStart(msg, SID_PREFIX);
                suricataData.setSid(sid);
            } else if (msg.startsWith(METADATA_PREFIX)) {
                //元数据信息处理获取ATT&ck
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


    /**
     * @description: 从上传文件中获取所有规则
     * @param file
     * @return List<String>
     */
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


    /**
     * @description: 分页查询流量规则信息
     * @param suricataQueryParams
     * @return ServiceResult<List<SuricataData>>
     */
    public ServiceResult<List<SuricataData>> queryPage(SuricataQueryParams suricataQueryParams) {
        suricataQueryParams.ensure();
        //构建查询参数
        HumanCondition condition = new HumanCondition();
        if(StringUtils.isNotEmpty(suricataQueryParams.getProtocol())){
            condition.filterEqual(SuricataRuleModel::getProtocol,suricataQueryParams.getProtocol());
        }
        if(StringUtils.isNotEmpty(suricataQueryParams.getRuleName())){
            condition.filterContains(SuricataRuleModel::getRuleName,suricataQueryParams.getRuleName());
        }
        if(StringUtils.isNotEmpty(suricataQueryParams.getAttck())){
            condition.filterContains(SuricataRuleModel::getAttck,suricataQueryParams.getAttck());
        }
        //查询结果总数
        int total = suricataRuleMapper.total(condition);
        if(total == 0){
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), total)).asResult();
        }
        //添加分页信息
        condition.pagination(suricataQueryParams.getPage(),suricataQueryParams.getPageSize());
        //执行查询
        List<SuricataRuleModel> suricataRuleModelList = suricataRuleMapper.list(condition);
        //转换响应为业务数据
        List<SuricataData> result = suricataRuleModelList.stream().map(rule -> {
            return SuricataData.builder().action(rule.getAction()).protocol(rule.getProtocol()).source(rule.getSource())
                .sourcePorts(rule.getSourcePorts()).direction(rule.getDirection()).destination(rule.getDestination())
                .destinationPorts(rule.getDestinationPorts()).ruleMsg(rule.getRuleMsg()).ruleName(rule.getRuleName())
                .sid(rule.getSid()).attck(rule.getAttck()).build();
        }).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(result, total)).asResult();
    }
}
