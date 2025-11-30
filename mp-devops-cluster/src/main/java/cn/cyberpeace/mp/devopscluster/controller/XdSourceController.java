package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.utils.XdOneHttpClient;
import cn.cyberpeace.mp.devopscluster.vo.*;
import cn.cyberpeace.mp.devopscluster.vo.xd.one.Action;
import cn.cyberpeace.mp.devopscluster.vo.xd.one.Cluster;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(value = "超融合平台资源监控", tags = "超融合平台资源监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/xd_source")
public class XdSourceController {

    @Autowired
    private XdOneHttpClient xdOneHttpClient;

    @ApiOperation("物理资源列表")
    @GetMapping("/phy_list")
    public ServiceResult<List<XdPhyVO>> phyList() {
        List<XdPhyVO> result = new ArrayList<>();

        try {
            xdOneHttpClient.init();
//            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
            HttpResult clusterResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
            ObjectMapper objectMapper = new ObjectMapper();
            clusterResult.setResult("{\n" +
                    "    \"count\": 1,\n" +
                    "    \"next\": null,\n" +
                    "    \"previous\": null,\n" +
                    "    \"results\": [\n" +
                    "        {\n" +
                    "            \"uuid\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\",\n" +
                    "            \"created_at\": \"2025-11-19 17:14\",\n" +
                    "            \"updated_at\": \"2025-11-19 17:14\",\n" +
                    "            \"name\": \"root-cluster-382e\",\n" +
                    "            \"provider\": \"openstack\",\n" +
                    "            \"description\": null,\n" +
                    "            \"external\": false,\n" +
                    "            \"is_local\": true,\n" +
                    "            \"properties\": {},\n" +
                    "            \"default_cluster\": true,\n" +
                    "            \"disaster_recovery\": {},\n" +
                    "            \"services\": [\n" +
                    "                {\n" +
                    "                    \"uuid\": \"e9fc3317-7758-4837-82f2-f5c275692fe2\",\n" +
                    "                    \"name\": \"ops\",\n" +
                    "                    \"type\": \"ops\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10040,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"4a2080ff-d560-4e12-bf89-4e03679bc517\",\n" +
                    "                    \"name\": \"monitor\",\n" +
                    "                    \"type\": \"monitor\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10080,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"ce257510-d573-4b22-b1e3-bb0ff1811180\",\n" +
                    "                    \"name\": \"openstack\",\n" +
                    "                    \"type\": \"openstack\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 5000,\n" +
                    "                    \"auth\": {\n" +
                    "                        \"username\": \"admin\",\n" +
                    "                        \"domain_name\": \"Default\",\n" +
                    "                        \"auth_url\": \"http://192.168.140.4:5000\",\n" +
                    "                        \"project_name\": \"admin\",\n" +
                    "                        \"region_name\": \"RegionOne\",\n" +
                    "                        \"password\": \"FHgL3AfMOi9SXXzaOt9Nu8aJ3G5UJ4t0AVFKISQN\"\n" +
                    "                    },\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"d86fc778-c48d-412b-af51-6188cd727669\",\n" +
                    "                    \"name\": \"rabbitmq\",\n" +
                    "                    \"type\": \"rabbitmq\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 5672,\n" +
                    "                    \"auth\": {\n" +
                    "                        \"username\": \"openstack\",\n" +
                    "                        \"password\": \"sqV3CZUeCm76sGLjPKMOAfjR4h7NIr1RL9gavFyt\"\n" +
                    "                    },\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"98ff59f4-4713-4090-a2d4-c145ab91c41c\",\n" +
                    "                    \"name\": \"octopus\",\n" +
                    "                    \"type\": \"octopus\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10101,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
//                HttpResult phyStaResult = xdOneHttpClient.xdGet("/proxy/monitor/nodes?cluster=" + cluster.getResults().get(0).getUuid(), null);
                HttpResult phyStaResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
                phyStaResult.setResult("{\n" +
                        "    \"192.168.140.5\": {\n" +
                        "        \"cpu\": {\n" +
                        "            \"util\": {\n" +
                        "                \"value\": \"3.58\",\n" +
                        "                \"unit\": \"%\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"memory\": {\n" +
                        "            \"used\": {\n" +
                        "                \"value\": \"41\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            },\n" +
                        "            \"total\": {\n" +
                        "                \"value\": \"754\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"filesystems\": {\n" +
                        "            \"/dev/sda1\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"6782976\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"523968512\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sda2\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"196136960\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1020702720\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sda3\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"155982388565.33\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1769341595648\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"network\": {\n" +
                        "            \"enp125s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"1\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"397\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"461\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"192.168.140.6\": {\n" +
                        "        \"cpu\": {\n" +
                        "            \"util\": {\n" +
                        "                \"value\": \"2.8\",\n" +
                        "                \"unit\": \"%\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"memory\": {\n" +
                        "            \"used\": {\n" +
                        "                \"value\": \"36\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            },\n" +
                        "            \"total\": {\n" +
                        "                \"value\": \"754\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"filesystems\": {\n" +
                        "            \"/dev/sdd1\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"6782976\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"523968512\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sdd2\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"196136960\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1020702720\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sdd3\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"46340962304\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1769341595648\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"network\": {\n" +
                        "            \"enp125s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"1\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"335\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"313\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"vxlan_sys_4789\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"192.168.140.7\": {\n" +
                        "        \"cpu\": {\n" +
                        "            \"util\": {\n" +
                        "                \"value\": \"3.52\",\n" +
                        "                \"unit\": \"%\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"memory\": {\n" +
                        "            \"used\": {\n" +
                        "                \"value\": \"34\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            },\n" +
                        "            \"total\": {\n" +
                        "                \"value\": \"754\",\n" +
                        "                \"unit\": \"GB\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"filesystems\": {\n" +
                        "            \"/dev/sda1\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"6782976\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"523968512\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sda2\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"196136960\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1020702720\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"/dev/sda3\": {\n" +
                        "                \"used\": {\n" +
                        "                    \"value\": \"47302253226.67\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                },\n" +
                        "                \"total\": {\n" +
                        "                    \"value\": \"1769341595648\",\n" +
                        "                    \"unit\": \"Bytes\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"network\": {\n" +
                        "            \"enp125s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp125s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f0\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f1\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f2\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"424\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"427\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"enp189s0f3\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"1\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"vxlan_sys_4789\": {\n" +
                        "                \"receive_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                },\n" +
                        "                \"transmit_rate\": {\n" +
                        "                    \"value\": \"0\",\n" +
                        "                    \"unit\": \"KB/s\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
                JSONObject phyStaJson = JSON.parseObject(phyStaResult.getResult());

//                HttpResult phyResult = xdOneHttpClient.xdGet("/proxy/monitor/prometheus/measures?cluster=" + cluster.getResults().get(0).getUuid() + "&queries[]=max_over_time(node_dmi_info[1y])&queries[]=max_over_time(node_os_info[1y])&queries[]=max_over_time(node_uname_info[1y])&queries[]= up{job=\"node\"}", null);
                HttpResult phyResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
                phyResult.setResult("[\n" +
                        "    {\n" +
                        "        \"data\": [\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"bios_date\": \"01/04/2025\",\n" +
                        "                    \"bios_release\": \"7.38\",\n" +
                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
                        "                    \"bios_version\": \"7.38\",\n" +
                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
                        "                    \"board_vendor\": \"PowerLeader\",\n" +
                        "                    \"board_version\": \"V200R023C10\",\n" +
                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_name\": \"PR210K\",\n" +
                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"system_vendor\": \"PowerLeader\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.736,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"bios_date\": \"01/04/2025\",\n" +
                        "                    \"bios_release\": \"7.38\",\n" +
                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
                        "                    \"bios_version\": \"7.38\",\n" +
                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
                        "                    \"board_vendor\": \"PowerLeader\",\n" +
                        "                    \"board_version\": \"V200R023C10\",\n" +
                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_name\": \"PR210K\",\n" +
                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"system_vendor\": \"PowerLeader\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.736,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"bios_date\": \"01/04/2025\",\n" +
                        "                    \"bios_release\": \"7.38\",\n" +
                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
                        "                    \"bios_version\": \"7.38\",\n" +
                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
                        "                    \"board_vendor\": \"PowerLeader\",\n" +
                        "                    \"board_version\": \"V200R023C10\",\n" +
                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_name\": \"PR210K\",\n" +
                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
                        "                    \"system_vendor\": \"PowerLeader\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.736,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"unit\": null,\n" +
                        "        \"metric\": \"max_over_time(node_dmi_info[1y])\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": [\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"id\": \"openEuler\",\n" +
                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"name\": \"openEuler\",\n" +
                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
                        "                    \"version_id\": \"24.03\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.769,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"id\": \"openEuler\",\n" +
                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"name\": \"openEuler\",\n" +
                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
                        "                    \"version_id\": \"24.03\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.769,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"id\": \"openEuler\",\n" +
                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"name\": \"openEuler\",\n" +
                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
                        "                    \"version_id\": \"24.03\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.769,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"unit\": null,\n" +
                        "        \"metric\": \"max_over_time(node_os_info[1y])\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": [\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"domainname\": \"(none)\",\n" +
                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"machine\": \"aarch64\",\n" +
                        "                    \"nodename\": \"control-1\",\n" +
                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
                        "                    \"sysname\": \"Linux\",\n" +
                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.797,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"domainname\": \"(none)\",\n" +
                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"machine\": \"aarch64\",\n" +
                        "                    \"nodename\": \"control-2\",\n" +
                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
                        "                    \"sysname\": \"Linux\",\n" +
                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.797,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"domainname\": \"(none)\",\n" +
                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
                        "                    \"job\": \"node\",\n" +
                        "                    \"machine\": \"aarch64\",\n" +
                        "                    \"nodename\": \"control-3\",\n" +
                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
                        "                    \"sysname\": \"Linux\",\n" +
                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.797,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"unit\": null,\n" +
                        "        \"metric\": \"max_over_time(node_uname_info[1y])\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"data\": [\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"__name__\": \"up\",\n" +
                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
                        "                    \"job\": \"node\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.828,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"__name__\": \"up\",\n" +
                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
                        "                    \"job\": \"node\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.828,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"metric\": {\n" +
                        "                    \"__name__\": \"up\",\n" +
                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
                        "                    \"job\": \"node\"\n" +
                        "                },\n" +
                        "                \"value\": [\n" +
                        "                    1764476065.828,\n" +
                        "                    \"1\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"unit\": null,\n" +
                        "        \"metric\": \"up\"\n" +
                        "    }\n" +
                        "]");
                JSONArray jsonArray = JSON.parseArray(phyResult.getResult());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if ("max_over_time(node_uname_info[1y])".equals(jsonObject.getString("metric"))
                            && jsonObject.getJSONArray("data") != null
                            && jsonObject.getJSONArray("data").size() > 0) {
                        JSONArray jsonArray2 = jsonObject.getJSONArray("data");
                        for (int j = 0; j < jsonArray2.size(); j++) {
                            JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                            if (jsonObject2.getJSONObject("metric") != null) {
                                XdPhyVO xdPhyVO = new XdPhyVO();
                                xdPhyVO.setNodeName(jsonObject2.getJSONObject("metric").getString("nodename"));
                                xdPhyVO.setSysName(jsonObject2.getJSONObject("metric").getString("sysname"));
                                String instance = jsonObject2.getJSONObject("metric").getString("instance");
                                xdPhyVO.setInstance(instance);
                                if (phyStaJson.getJSONObject(instance.split(":")[0]) != null) {
                                    JSONObject instanceJson = phyStaJson.getJSONObject(instance.split(":")[0]);
                                    JSONObject cpuJson = instanceJson.getJSONObject("cpu");
                                    JSONObject memoryJson = instanceJson.getJSONObject("memory");
                                    JSONObject filesystemsJson = instanceJson.getJSONObject("filesystems");
                                    if (cpuJson != null) {
                                        if (cpuJson.getJSONObject("util") != null) {
                                            xdPhyVO.setCpuPercent(cpuJson.getJSONObject("util").getString("value") + cpuJson.getJSONObject("util").getString("unit"));
                                        }
                                    }
                                    if (memoryJson != null) {
                                        if (memoryJson.getJSONObject("used") != null) {
                                            ValueUnitVO temp = new ValueUnitVO();
                                            temp.setValue(memoryJson.getJSONObject("used").getString("value"));
                                            temp.setUnit(memoryJson.getJSONObject("used").getString("unit"));
                                            xdPhyVO.setUsedMemory(temp);
                                        }
                                        if (memoryJson.getJSONObject("total") != null) {
                                            ValueUnitVO temp = new ValueUnitVO();
                                            temp.setValue(memoryJson.getJSONObject("total").getString("value"));
                                            temp.setUnit(memoryJson.getJSONObject("total").getString("unit"));
                                            xdPhyVO.setTotalMemory(temp);
                                        }
                                    }
                                    if (filesystemsJson != null) {
                                        List<XdPhyFileSystem> xdPhyFileSystemList = new ArrayList<>();
                                        for (String key : filesystemsJson.keySet()) {
                                            XdPhyFileSystem xdPhyFileSystem = new XdPhyFileSystem();
                                            xdPhyFileSystem.setName(key);
                                            JSONObject tempObject = filesystemsJson.getJSONObject(key);
                                            if (tempObject.getJSONObject("used") != null) {
                                                ValueUnitVO temp = new ValueUnitVO();
                                                temp.setValue(tempObject.getJSONObject("used").getString("value"));
                                                temp.setUnit(tempObject.getJSONObject("used").getString("unit"));
                                                xdPhyFileSystem.setUsedFileSystem(temp);
                                            }
                                            if (tempObject.getJSONObject("total") != null) {
                                                ValueUnitVO temp = new ValueUnitVO();
                                                temp.setValue(tempObject.getJSONObject("total").getString("value"));
                                                temp.setUnit(tempObject.getJSONObject("total").getString("unit"));
                                                xdPhyFileSystem.setTotalFileSystem(temp);
                                            }
                                            xdPhyFileSystemList.add(xdPhyFileSystem);
                                        }
                                        xdPhyVO.setFileSystems(xdPhyFileSystemList);
                                    }
                                }
                                result.add(xdPhyVO);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("XdSourceController.phyList error.",  e);
        }

        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("虚拟资源列表")
    @GetMapping("/vir_list")
    public ServiceResult<List<XdVirVO>> virList() {
        List<XdVirVO> result = new ArrayList<>();
        try {
            xdOneHttpClient.init();
//            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
            HttpResult clusterResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
            ObjectMapper objectMapper = new ObjectMapper();
            clusterResult.setResult("{\n" +
                    "    \"count\": 1,\n" +
                    "    \"next\": null,\n" +
                    "    \"previous\": null,\n" +
                    "    \"results\": [\n" +
                    "        {\n" +
                    "            \"uuid\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\",\n" +
                    "            \"created_at\": \"2025-11-19 17:14\",\n" +
                    "            \"updated_at\": \"2025-11-19 17:14\",\n" +
                    "            \"name\": \"root-cluster-382e\",\n" +
                    "            \"provider\": \"openstack\",\n" +
                    "            \"description\": null,\n" +
                    "            \"external\": false,\n" +
                    "            \"is_local\": true,\n" +
                    "            \"properties\": {},\n" +
                    "            \"default_cluster\": true,\n" +
                    "            \"disaster_recovery\": {},\n" +
                    "            \"services\": [\n" +
                    "                {\n" +
                    "                    \"uuid\": \"e9fc3317-7758-4837-82f2-f5c275692fe2\",\n" +
                    "                    \"name\": \"ops\",\n" +
                    "                    \"type\": \"ops\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10040,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"4a2080ff-d560-4e12-bf89-4e03679bc517\",\n" +
                    "                    \"name\": \"monitor\",\n" +
                    "                    \"type\": \"monitor\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10080,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"ce257510-d573-4b22-b1e3-bb0ff1811180\",\n" +
                    "                    \"name\": \"openstack\",\n" +
                    "                    \"type\": \"openstack\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 5000,\n" +
                    "                    \"auth\": {\n" +
                    "                        \"username\": \"admin\",\n" +
                    "                        \"domain_name\": \"Default\",\n" +
                    "                        \"auth_url\": \"http://192.168.140.4:5000\",\n" +
                    "                        \"project_name\": \"admin\",\n" +
                    "                        \"region_name\": \"RegionOne\",\n" +
                    "                        \"password\": \"FHgL3AfMOi9SXXzaOt9Nu8aJ3G5UJ4t0AVFKISQN\"\n" +
                    "                    },\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"d86fc778-c48d-412b-af51-6188cd727669\",\n" +
                    "                    \"name\": \"rabbitmq\",\n" +
                    "                    \"type\": \"rabbitmq\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 5672,\n" +
                    "                    \"auth\": {\n" +
                    "                        \"username\": \"openstack\",\n" +
                    "                        \"password\": \"sqV3CZUeCm76sGLjPKMOAfjR4h7NIr1RL9gavFyt\"\n" +
                    "                    },\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"98ff59f4-4713-4090-a2d4-c145ab91c41c\",\n" +
                    "                    \"name\": \"octopus\",\n" +
                    "                    \"type\": \"octopus\",\n" +
                    "                    \"host\": \"192.168.140.4\",\n" +
                    "                    \"port\": 10101,\n" +
                    "                    \"auth\": null,\n" +
                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
//                HttpResult virResult = xdOneHttpClient.xdGet("/instances?cluster=" + cluster.getResults().get(0).getUuid(), null);
                HttpResult virResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
                virResult.setResult("{\n" +
                        "    \"count\": 8,\n" +
                        "    \"next\": null,\n" +
                        "    \"previous\": null,\n" +
                        "    \"results\": [\n" +
                        "        {\n" +
                        "            \"name\": \"sn_test_5\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.60\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:d0:e0:d9\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"1d23ddac-9a43-431a-97bc-8c6355701362\",\n" +
                        "            \"instance_name\": \"instance-00000070\",\n" +
                        "            \"host\": \"control-3\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"ACTIVE\",\n" +
                        "            \"status_display\": \"可用\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-29 15:00\",\n" +
                        "            \"updated_at\": \"2025-11-29 15:00\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": true,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": false,\n" +
                        "            \"power_off_enabled\": true,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": true,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": true,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": true,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": true,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 1,\n" +
                        "                \"ram\": 2048,\n" +
                        "                \"disk\": 20,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.small\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"active\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"sn_test_2\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"net1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.140.52\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:d6:6d:3e\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"e805dd60-3c94-4421-abdd-0dd6ea6852f4\",\n" +
                        "            \"instance_name\": \"instance-0000006a\",\n" +
                        "            \"host\": \"control-3\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"SHUTOFF\",\n" +
                        "            \"status_display\": \"关机\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-29 14:12\",\n" +
                        "            \"updated_at\": \"2025-11-30 10:43\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": false,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": true,\n" +
                        "            \"power_off_enabled\": false,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": false,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": false,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": false,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": false,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 1,\n" +
                        "                \"ram\": 1024,\n" +
                        "                \"disk\": 5,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"amphora\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"stopped\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"test1\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"net1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.140.84\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:5a:b5:f8\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"96a896bf-2763-4dee-bc7c-5c91afaafa5f\",\n" +
                        "            \"instance_name\": \"instance-0000005b\",\n" +
                        "            \"host\": \"control-2\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"SHUTOFF\",\n" +
                        "            \"status_display\": \"关机\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-27 20:55\",\n" +
                        "            \"updated_at\": \"2025-11-29 13:31\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": false,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": true,\n" +
                        "            \"power_off_enabled\": false,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": false,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": false,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": false,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": false,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 2,\n" +
                        "                \"ram\": 4096,\n" +
                        "                \"disk\": 40,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.medium\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"stopped\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"HFish-Client\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.29\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:ff:00:18\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
                        "                \"uuid\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
                        "                \"name\": \"openEuler-2203-lts-sp3-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 40,\n" +
                        "                \"min_ram\": 4096,\n" +
                        "                \"size\": 42949672960,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": null,\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": null,\n" +
                        "                \"hw_cdrom_bus\": \"sata\",\n" +
                        "                \"os_distro\": \"openEuler\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"openeuler\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/openEuler-2203-lts-sp3-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"sata\",\n" +
                        "                    \"os_admin_user\": \"openeuler\",\n" +
                        "                    \"os_distro\": \"openEuler\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"a5e195bc3a126fc910593377d3fb2b75c10365ca9e353b68ea83519062159a22acd25d4e8ef8ee392c6f18336e240cf5f57031797b1d328e0a7ff992ed2fe7eb\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/b345f83a-c03c-4af4-8e37-e2e56732e713/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:50\",\n" +
                        "                \"updated_at\": \"2025-11-19 17:03\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"1e7a893e-cdce-4863-bcae-cb0263b2b18f\",\n" +
                        "            \"instance_name\": \"instance-00000055\",\n" +
                        "            \"host\": \"control-2\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"ACTIVE\",\n" +
                        "            \"status_display\": \"可用\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-27 20:35\",\n" +
                        "            \"updated_at\": \"2025-11-27 20:35\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": true,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": false,\n" +
                        "            \"power_off_enabled\": true,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": true,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": true,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": true,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": true,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 4,\n" +
                        "                \"ram\": 8192,\n" +
                        "                \"disk\": 80,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.large\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"active\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"ubuntu24\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.14\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:6e:2f:92\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": null,\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"1330896b-974f-49aa-bea0-4196e1b50557\",\n" +
                        "            \"instance_name\": \"instance-0000003d\",\n" +
                        "            \"host\": \"control-1\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"ACTIVE\",\n" +
                        "            \"status_display\": \"可用\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-23 11:15\",\n" +
                        "            \"updated_at\": \"2025-11-23 15:00\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": true,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": false,\n" +
                        "            \"power_off_enabled\": true,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": true,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": true,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": false,\n" +
                        "            \"backup_enabled\": false,\n" +
                        "            \"rebuild_enabled\": false,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": true,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": true,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 2,\n" +
                        "                \"ram\": 4096,\n" +
                        "                \"disk\": 40,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.medium\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"active\",\n" +
                        "            \"volumes_attached\": [\n" +
                        "                {\n" +
                        "                    \"id\": \"01a0cca1-749d-47e9-ba02-d5f976192fb8\",\n" +
                        "                    \"delete_on_termination\": true\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"id\": \"4223d941-2349-44a1-84b9-17d3e8866b6c\",\n" +
                        "                    \"delete_on_termination\": true\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"test-3\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.52\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:7d:9a:00\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"cc003ee9-d536-4a5a-a7c3-3dbd78c5b387\",\n" +
                        "            \"instance_name\": \"instance-00000007\",\n" +
                        "            \"host\": \"control-3\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"SHUTOFF\",\n" +
                        "            \"status_display\": \"关机\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-19 17:36\",\n" +
                        "            \"updated_at\": \"2025-11-29 14:11\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": false,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": true,\n" +
                        "            \"power_off_enabled\": false,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": false,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": false,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": false,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": false,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 1,\n" +
                        "                \"ram\": 512,\n" +
                        "                \"disk\": 1,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.tiny\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"stopped\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"test-2\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.18\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:b9:de:d4\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"d20efab6-9d6e-4d46-8012-59d3e5ac3252\",\n" +
                        "            \"instance_name\": \"instance-00000004\",\n" +
                        "            \"host\": \"control-2\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"SHUTOFF\",\n" +
                        "            \"status_display\": \"关机\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-19 17:36\",\n" +
                        "            \"updated_at\": \"2025-11-29 14:10\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": false,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": true,\n" +
                        "            \"power_off_enabled\": false,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": false,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": false,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": false,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": false,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 1,\n" +
                        "                \"ram\": 512,\n" +
                        "                \"disk\": 1,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.tiny\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"stopped\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"test-1\",\n" +
                        "            \"az\": \"nova\",\n" +
                        "            \"address\": {\n" +
                        "                \"network1\": {\n" +
                        "                    \"floating\": [],\n" +
                        "                    \"fixed\": [\n" +
                        "                        {\n" +
                        "                            \"version\": 4,\n" +
                        "                            \"addr\": \"192.168.141.3\",\n" +
                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:7e:3f:3a\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "            \"full_image\": {\n" +
                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
                        "                \"name\": \"cirros-arm64\",\n" +
                        "                \"image_type\": \"image\",\n" +
                        "                \"image_type_display\": \"镜像\",\n" +
                        "                \"status\": \"active\",\n" +
                        "                \"status_display\": \"可用\",\n" +
                        "                \"min_disk\": 1,\n" +
                        "                \"min_ram\": 256,\n" +
                        "                \"size\": 117440512,\n" +
                        "                \"disk_format\": \"raw\",\n" +
                        "                \"protected\": false,\n" +
                        "                \"visibility\": \"public\",\n" +
                        "                \"architecture\": \"aarch64\",\n" +
                        "                \"hw_video_model\": null,\n" +
                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                \"os_distro\": \"cirros\",\n" +
                        "                \"os_type\": \"linux\",\n" +
                        "                \"os_admin_user\": \"cirros\",\n" +
                        "                \"is_stable\": true,\n" +
                        "                \"delete_enabled\": true,\n" +
                        "                \"tags\": [],\n" +
                        "                \"properties\": {\n" +
                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
                        "                    \"cpu_arch\": \"aarch64\",\n" +
                        "                    \"hw_boot_menu\": \"true\",\n" +
                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
                        "                    \"os_admin_user\": \"cirros\",\n" +
                        "                    \"os_distro\": \"cirros\",\n" +
                        "                    \"os_type\": \"linux\",\n" +
                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
                        "                    \"os_hidden\": false,\n" +
                        "                    \"os_hash_algo\": \"sha512\",\n" +
                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
                        "                    \"stores\": \"rbd\"\n" +
                        "                },\n" +
                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
                        "            },\n" +
                        "            \"server_group\": null,\n" +
                        "            \"uuid\": \"86d4c304-1d14-4da3-bb41-ca52e29d97dc\",\n" +
                        "            \"instance_name\": \"instance-00000001\",\n" +
                        "            \"host\": \"control-1\",\n" +
                        "            \"fault\": null,\n" +
                        "            \"status\": \"ACTIVE\",\n" +
                        "            \"status_display\": \"可用\",\n" +
                        "            \"task\": null,\n" +
                        "            \"task_display\": null,\n" +
                        "            \"created_at\": \"2025-11-19 17:35\",\n" +
                        "            \"updated_at\": \"2025-11-29 15:04\",\n" +
                        "            \"is_stable\": true,\n" +
                        "            \"vnc_enabled\": true,\n" +
                        "            \"delete_enabled\": true,\n" +
                        "            \"reboot_enabled\": true,\n" +
                        "            \"reset_enabled\": false,\n" +
                        "            \"power_on_enabled\": false,\n" +
                        "            \"power_off_enabled\": true,\n" +
                        "            \"associate_floating_ip_enabled\": true,\n" +
                        "            \"disassociate_floating_ip_enabled\": false,\n" +
                        "            \"pause_enabled\": true,\n" +
                        "            \"unpause_enabled\": false,\n" +
                        "            \"suspend_enabled\": true,\n" +
                        "            \"unsuspend_enabled\": false,\n" +
                        "            \"resize_enabled\": true,\n" +
                        "            \"confirm_resize_enabled\": false,\n" +
                        "            \"revert_resize_enabled\": false,\n" +
                        "            \"snapshot_enabled\": true,\n" +
                        "            \"backup_enabled\": true,\n" +
                        "            \"rebuild_enabled\": true,\n" +
                        "            \"migrate_enabled\": true,\n" +
                        "            \"live_migrate_enabled\": true,\n" +
                        "            \"change_password_enabled\": false,\n" +
                        "            \"volume_attach_enabled\": true,\n" +
                        "            \"edit_enabled\": true,\n" +
                        "            \"metadata\": {},\n" +
                        "            \"tags\": null,\n" +
                        "            \"current_conf\": {\n" +
                        "                \"vcpus\": 1,\n" +
                        "                \"ram\": 512,\n" +
                        "                \"disk\": 1,\n" +
                        "                \"ephemeral\": 0,\n" +
                        "                \"swap\": 0,\n" +
                        "                \"original_name\": \"m1.tiny\",\n" +
                        "                \"extra_specs\": {}\n" +
                        "            },\n" +
                        "            \"vm_state\": \"active\",\n" +
                        "            \"volumes_attached\": [],\n" +
                        "            \"is_baremetal_instance\": false,\n" +
                        "            \"iso_image_attach_enabled\": true,\n" +
                        "            \"disaster_recovery\": {\n" +
                        "                \"enabled\": false,\n" +
                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
                        "            },\n" +
                        "            \"dr_clone_enabled\": false\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}");

                JSONObject jsonObject = JSON.parseObject(virResult.getResult());
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                if (resultsArray !=null && resultsArray.size() > 0) {
                    for (int i = 0; i < resultsArray.size(); i++) {
                        XdVirVO xdVirVO = new XdVirVO();
                        xdVirVO.setName(resultsArray.getJSONObject(i).getString("name"));
                        xdVirVO.setId(resultsArray.getJSONObject(i).getString("uuid"));
                        xdVirVO.setStatus(resultsArray.getJSONObject(i).getString("status"));
                        xdVirVO.setStatusDisplay(resultsArray.getJSONObject(i).getString("status_display"));
                        xdVirVO.setImageId(resultsArray.getJSONObject(i).getString("image_id"));
                        JSONObject imageObject = resultsArray.getJSONObject(i).getJSONObject("full_image");
                        if (imageObject != null) {
                            xdVirVO.setImageName(imageObject.getString("name"));
                        }
                        JSONObject addressObject = resultsArray.getJSONObject(i).getJSONObject("address");
                        if (addressObject != null) {
                            String addr = "";
                            for (String key : addressObject.keySet()) {
                                JSONArray tempJSONArray = addressObject.getJSONObject(key).getJSONArray("fixed");
                                if (tempJSONArray != null && tempJSONArray.size() > 0) {
                                    for (int j=0; j<tempJSONArray.size(); j++) {
                                        addr += "," + tempJSONArray.getJSONObject(j).getString("addr");
                                    }
                                }
                            }
                            if (addr.length() > 0) {
                                addr = addr.substring(1);
                            }
                            xdVirVO.setAddr(addr);
                        }
                        result.add(xdVirVO);
                    }
                }
            }
        } catch (Exception e) {
                log.error("XdSourceController.virList error.",  e);
        }
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("开机")
    @ApiImplicitParam(name = "XdVirUpShut", value = "新增超融合平台", dataTypeClass = XdVirUpShut.class)
    @PostMapping("/up_shut")
    public ServiceResult<PlatformVO> upShut(@RequestBody XdVirUpShut xdVirUpShut) {
        try {
            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
            ObjectMapper objectMapper = new ObjectMapper();
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
                Action action = new Action();
                action.setAction(xdVirUpShut.getAction());
                HttpResult actionResult = xdOneHttpClient.xdPost("/instances/" + xdVirUpShut.getId() + "/action/?cluster=" + cluster.getResults().get(0).getUuid(), null, action);
                JSONObject jsonObject = JSON.parseObject(actionResult.getResult());
                if (jsonObject.getBoolean("success")) {
                    return ResultCommon.success();
                } else if (StringUtils.isNotEmpty(jsonObject.getString("message"))) {
                    return ResultCommon.fail(jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
            log.error("XdSourceController.upShut error.",  e);
        }
        return ResultCommon.success();
    }


}
