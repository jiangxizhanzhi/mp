package cn.cyberpeace.mp.situationaware.model.wazuh.agent;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 09:50
 */
@Data
public class OS {

    private String build;

    private String arch;

    private String major;

    private String minor;

    private String name;

    private String patform;

    private String uname;

    private String version;


}
