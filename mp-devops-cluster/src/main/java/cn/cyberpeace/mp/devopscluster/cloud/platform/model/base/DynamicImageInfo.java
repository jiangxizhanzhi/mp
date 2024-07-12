 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamicImageInfo.java.
 // File size 4876.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName DynamicImageInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Getter
@Setter
public class DynamicImageInfo {
    private List<String> platforms;

    private List<PlatformInfo> details;

    @Getter
    @Setter
    public static class PlatformInfo {
        private String platform;
        private List<PlatformPortInfo> portInfos;
    }

    @Getter
    @Setter
    public static class PlatformPortInfo {
        private Integer portNum;
        private List<String> portNames;
    }


    public static DynamicImageInfo mock() {
        return JsonCommon.snakeParse("{\n" +
                "    \"platforms\": [\n" +
                "        \"c2600\",\n" +
                "        \"c3700\",\n" +
                "        \"c7200\"\n" +
                "    ],\n" +
                "    \"details\": [\n" +
                "        {\n" +
                "            \"platform\": \"c2600\",\n" +
                "            \"port_infos\": [\n" +
                "                {\n" +
                "                    \"port_num\": 2,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"port_num\": 4,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\",\n" +
                "                        \"Ethernet2/0\",\n" +
                "                        \"Ethernet2/1\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"platform\": \"c3700\",\n" +
                "            \"port_infos\": [\n" +
                "                {\n" +
                "                    \"port_num\": 3,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\",\n" +
                "                        \"Ethernet2/0\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"port_num\": 4,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\",\n" +
                "                        \"Ethernet2/0\",\n" +
                "                        \"Ethernet2/1\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "    \n" +
                "        {\n" +
                "            \"platform\": \"c7200\",\n" +
                "            \"port_infos\": [\n" +
                "                {\n" +
                "                    \"port_num\": 7,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\",\n" +
                "                        \"Ethernet2/0\",\n" +
                "                        \"Ethernet3/0\",\n" +
                "                        \"Ethernet4/0\",\n" +
                "                        \"Ethernet5/0\",\n" +
                "                        \"Ethernet6/0\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"port_num\": 4,\n" +
                "                    \"port_names\": [\n" +
                "                        \"Ethernet0/0\",\n" +
                "                        \"Ethernet1/0\",\n" +
                "                        \"Ethernet2/0\",\n" +
                "                        \"Ethernet2/1\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}", DynamicImageInfo.class);
    }
}
