package cn.cyberpeace.mp.situationaware.model.wazuh.rule;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/4/18 19:58
 */
@Data
public class Rule {

    private int id;

    private int level;

    private String filename;

    @JSONField(name = "relative_dirname")
    private String relativeDirname;

    private String status;

    @JSONField(name = "pci_dss")
    private List<String> pciDss;

    private List<String> gpg13;

    private List<String> gdpr;

    private List<String> hipaa;

    //@JSONField(name = "nist_800_53")
    //private List<String> nist80053;

    private List<String> tsc;

    private List<String> mitre;

    private List<String> groups;

    private String description;

    private Details details;

    @Data
    public static class Details {

        private String noalert;

        private String category;
    }

}
