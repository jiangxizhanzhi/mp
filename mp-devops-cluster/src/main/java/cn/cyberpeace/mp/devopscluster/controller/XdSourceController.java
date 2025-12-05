package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.base.util.http.XdOneHttpClient;
import cn.cyberpeace.mp.base.util.http.XdTwoHttpClient;
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
    @Autowired
    private XdTwoHttpClient xdTwoHttpClient;

    @ApiOperation("物理资源列表")
    @GetMapping("/phy_list")
    public ServiceResult<List<XdPhyVO>> phyList() {
        List<XdPhyVO> result = new ArrayList<>();

        try {
            xdOneHttpClient.init();
            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
//            HttpResult clusterResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
            ObjectMapper objectMapper = new ObjectMapper();
//            clusterResult.setResult("{\n" +
//                    "    \"count\": 1,\n" +
//                    "    \"next\": null,\n" +
//                    "    \"previous\": null,\n" +
//                    "    \"results\": [\n" +
//                    "        {\n" +
//                    "            \"uuid\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\",\n" +
//                    "            \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"updated_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"name\": \"root-cluster-382e\",\n" +
//                    "            \"provider\": \"openstack\",\n" +
//                    "            \"description\": null,\n" +
//                    "            \"external\": false,\n" +
//                    "            \"is_local\": true,\n" +
//                    "            \"properties\": {},\n" +
//                    "            \"default_cluster\": true,\n" +
//                    "            \"disaster_recovery\": {},\n" +
//                    "            \"services\": [\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"e9fc3317-7758-4837-82f2-f5c275692fe2\",\n" +
//                    "                    \"name\": \"ops\",\n" +
//                    "                    \"type\": \"ops\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10040,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"4a2080ff-d560-4e12-bf89-4e03679bc517\",\n" +
//                    "                    \"name\": \"monitor\",\n" +
//                    "                    \"type\": \"monitor\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10080,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"ce257510-d573-4b22-b1e3-bb0ff1811180\",\n" +
//                    "                    \"name\": \"openstack\",\n" +
//                    "                    \"type\": \"openstack\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5000,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"admin\",\n" +
//                    "                        \"domain_name\": \"Default\",\n" +
//                    "                        \"auth_url\": \"http://192.168.140.4:5000\",\n" +
//                    "                        \"project_name\": \"admin\",\n" +
//                    "                        \"region_name\": \"RegionOne\",\n" +
//                    "                        \"password\": \"FHgL3AfMOi9SXXzaOt9Nu8aJ3G5UJ4t0AVFKISQN\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"d86fc778-c48d-412b-af51-6188cd727669\",\n" +
//                    "                    \"name\": \"rabbitmq\",\n" +
//                    "                    \"type\": \"rabbitmq\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5672,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"openstack\",\n" +
//                    "                        \"password\": \"sqV3CZUeCm76sGLjPKMOAfjR4h7NIr1RL9gavFyt\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"98ff59f4-4713-4090-a2d4-c145ab91c41c\",\n" +
//                    "                    \"name\": \"octopus\",\n" +
//                    "                    \"type\": \"octopus\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10101,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                }\n" +
//                    "            ]\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}");
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
                HttpResult phyStaResult = xdOneHttpClient.xdGet("/proxy/monitor/nodes?cluster=" + cluster.getResults().get(0).getUuid(), null);
//                HttpResult phyStaResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//                phyStaResult.setResult("{\n" +
//                        "    \"192.168.140.5\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"3.58\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"41\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sda1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"155982388565.33\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"397\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"461\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    },\n" +
//                        "    \"192.168.140.6\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"2.8\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"36\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sdd1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sdd2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sdd3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"46340962304\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"335\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"313\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"vxlan_sys_4789\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    },\n" +
//                        "    \"192.168.140.7\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"3.52\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"34\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sda1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"47302253226.67\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"424\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"427\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"vxlan_sys_4789\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    }\n" +
//                        "}");
                JSONObject phyStaJson = JSON.parseObject(phyStaResult.getResult());

                HttpResult phyResult = xdOneHttpClient.xdGet("/proxy/monitor/prometheus/measures?cluster=" + cluster.getResults().get(0).getUuid() + "&queries[]=max_over_time(node_dmi_info[1y])&queries[]=max_over_time(node_os_info[1y])&queries[]=max_over_time(node_uname_info[1y])", null);
//                HttpResult phyResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//                phyResult.setResult("[\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_dmi_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_os_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-1\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-2\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-3\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_uname_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"up\"\n" +
//                        "    }\n" +
//                        "]");
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
                                xdPhyVO.setCompany("1");
                                xdPhyVO.setCompany_name("华网创新");
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
                                                if (StringUtils.isNotEmpty(temp.getUnit()) && StringUtils.isNotEmpty(temp.getValue()) && "bytes".equals(temp.getUnit().toLowerCase())) {
                                                    temp.setValue(String.format("%.2f",Double.parseDouble(temp.getValue())/(1024*1024*1024)));
                                                    temp.setUnit("GB");
                                                }
                                                xdPhyFileSystem.setUsedFileSystem(temp);
                                            }
                                            if (tempObject.getJSONObject("total") != null) {
                                                ValueUnitVO temp = new ValueUnitVO();
                                                temp.setValue(tempObject.getJSONObject("total").getString("value"));
                                                temp.setUnit(tempObject.getJSONObject("total").getString("unit"));
                                                if (StringUtils.isNotEmpty(temp.getUnit()) && StringUtils.isNotEmpty(temp.getValue()) && "bytes".equals(temp.getUnit().toLowerCase())) {
                                                    temp.setValue(String.format("%.2f",Double.parseDouble(temp.getValue())/(1024*1024*1024)));
                                                    temp.setUnit("GB");
                                                }
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
            log.error("XdSourceController.phyList one error.",  e);
        }
        try {
            xdTwoHttpClient.init();
            HttpResult phyResult = xdTwoHttpClient.xdGet("/janus/20180725/hosts", null);
//            HttpResult phyResult = xdTwoHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//            phyResult.setResult("{\n" +
//                    "    \"message\": \"\",\n" +
//                    "    \"code\": 0,\n" +
//                    "    \"data\": {\n" +
//                    "        \"total_size\": 3,\n" +
//                    "        \"data\": [\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.12\",\n" +
//                    "                \"ip\": \"192.168.140.12\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba5876cb0\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 52428.8,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.1,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 15990.041500000001,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.05,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 319800.83,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.11\",\n" +
//                    "                \"ip\": \"192.168.140.11\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba58770bc\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 31457.28,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.06,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 6336.978000000001,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.02,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 316848.9,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.13\",\n" +
//                    "                \"ip\": \"192.168.140.13\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba5875d54\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 73400.32,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.14,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 22399.3637,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.07,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 319990.91,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            }\n" +
//                    "        ],\n" +
//                    "        \"next_page_num\": \"\",\n" +
//                    "        \"page_num\": 0,\n" +
//                    "        \"page_size\": 100\n" +
//                    "    }\n" +
//                    "}");
            JSONObject jsonObject = JSON.parseObject(phyResult.getResult());
            if (jsonObject != null && jsonObject.getJSONObject("data") != null && CollectionUtils.isNotEmpty(jsonObject.getJSONObject("data").getJSONArray("data"))) {
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++) {
                    XdPhyVO xdPhyVO = new XdPhyVO();
                    xdPhyVO.setCompany("2");
                    xdPhyVO.setCompany_name("深信服");
                    xdPhyVO.setNodeName(jsonArray.getJSONObject(i).getString("id"));
                    xdPhyVO.setSysName("");
                    xdPhyVO.setInstance(jsonArray.getJSONObject(i).getString("ip"));
                    if (jsonArray.getJSONObject(i).getJSONObject("cpu") != null) {
                        xdPhyVO.setCpuPercent(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("cpu").getDouble("ratio")*100) + "%");
                    }
                    if (jsonArray.getJSONObject(i).getJSONObject("memory") != null) {
                        ValueUnitVO valueUnitVOUsed = new ValueUnitVO();
                        valueUnitVOUsed.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("memory").getDouble("used_mb")/1024));
                        valueUnitVOUsed.setUnit("GB");
                        xdPhyVO.setUsedMemory(valueUnitVOUsed);
                        ValueUnitVO valueUnitVOTotal = new ValueUnitVO();
                        valueUnitVOTotal.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("memory").getDouble("total_mb")/1024));
                        valueUnitVOTotal.setUnit("GB");
                        xdPhyVO.setTotalMemory(valueUnitVOTotal);
                    }
                    if (jsonArray.getJSONObject(i).getJSONObject("storage") != null) {
                        ValueUnitVO valueUnitVOTotal = new ValueUnitVO();
                        valueUnitVOTotal.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("storage").getDouble("total_mb")/1024));
                        valueUnitVOTotal.setUnit("GB");
                        ValueUnitVO valueUnitVOUsed = new ValueUnitVO();
                        valueUnitVOUsed.setValue("");
                        valueUnitVOUsed.setUnit("");
                        XdPhyFileSystem xdPhyFileSystem = new XdPhyFileSystem();
                        xdPhyFileSystem.setName("总量");
                        xdPhyFileSystem.setTotalFileSystem(valueUnitVOTotal);
                        xdPhyFileSystem.setUsedFileSystem(valueUnitVOUsed);
                        List<XdPhyFileSystem> xdPhyFileSystemList = new ArrayList<>();
                        xdPhyFileSystemList.add(xdPhyFileSystem);
                        xdPhyVO.setFileSystems(xdPhyFileSystemList);
                    }
                    result.add(xdPhyVO);
                }
            }
        } catch (Exception e) {
            log.error("XdSourceController.phyList two error.",  e);
        }

        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("虚拟资源列表")
    @GetMapping("/vir_list")
    public ServiceResult<List<XdVirVO>> virList() {
        List<XdVirVO> result = new ArrayList<>();
        try {
            xdOneHttpClient.init();
            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
//            HttpResult clusterResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
            ObjectMapper objectMapper = new ObjectMapper();
//            clusterResult.setResult("{\n" +
//                    "    \"count\": 1,\n" +
//                    "    \"next\": null,\n" +
//                    "    \"previous\": null,\n" +
//                    "    \"results\": [\n" +
//                    "        {\n" +
//                    "            \"uuid\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\",\n" +
//                    "            \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"updated_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"name\": \"root-cluster-382e\",\n" +
//                    "            \"provider\": \"openstack\",\n" +
//                    "            \"description\": null,\n" +
//                    "            \"external\": false,\n" +
//                    "            \"is_local\": true,\n" +
//                    "            \"properties\": {},\n" +
//                    "            \"default_cluster\": true,\n" +
//                    "            \"disaster_recovery\": {},\n" +
//                    "            \"services\": [\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"e9fc3317-7758-4837-82f2-f5c275692fe2\",\n" +
//                    "                    \"name\": \"ops\",\n" +
//                    "                    \"type\": \"ops\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10040,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"4a2080ff-d560-4e12-bf89-4e03679bc517\",\n" +
//                    "                    \"name\": \"monitor\",\n" +
//                    "                    \"type\": \"monitor\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10080,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"ce257510-d573-4b22-b1e3-bb0ff1811180\",\n" +
//                    "                    \"name\": \"openstack\",\n" +
//                    "                    \"type\": \"openstack\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5000,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"admin\",\n" +
//                    "                        \"domain_name\": \"Default\",\n" +
//                    "                        \"auth_url\": \"http://192.168.140.4:5000\",\n" +
//                    "                        \"project_name\": \"admin\",\n" +
//                    "                        \"region_name\": \"RegionOne\",\n" +
//                    "                        \"password\": \"FHgL3AfMOi9SXXzaOt9Nu8aJ3G5UJ4t0AVFKISQN\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"d86fc778-c48d-412b-af51-6188cd727669\",\n" +
//                    "                    \"name\": \"rabbitmq\",\n" +
//                    "                    \"type\": \"rabbitmq\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5672,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"openstack\",\n" +
//                    "                        \"password\": \"sqV3CZUeCm76sGLjPKMOAfjR4h7NIr1RL9gavFyt\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"98ff59f4-4713-4090-a2d4-c145ab91c41c\",\n" +
//                    "                    \"name\": \"octopus\",\n" +
//                    "                    \"type\": \"octopus\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10101,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                }\n" +
//                    "            ]\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}");
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
                HttpResult virResult = xdOneHttpClient.xdGet("/instances?cluster=" + cluster.getResults().get(0).getUuid(), null);
//                HttpResult virResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//                virResult.setResult("{\n" +
//                        "    \"count\": 8,\n" +
//                        "    \"next\": null,\n" +
//                        "    \"previous\": null,\n" +
//                        "    \"results\": [\n" +
//                        "        {\n" +
//                        "            \"name\": \"sn_test_5\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.60\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:d0:e0:d9\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"1d23ddac-9a43-431a-97bc-8c6355701362\",\n" +
//                        "            \"instance_name\": \"instance-00000070\",\n" +
//                        "            \"host\": \"control-3\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"ACTIVE\",\n" +
//                        "            \"status_display\": \"可用\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-29 15:00\",\n" +
//                        "            \"updated_at\": \"2025-11-29 15:00\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": true,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": false,\n" +
//                        "            \"power_off_enabled\": true,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": true,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": true,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": true,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": true,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 1,\n" +
//                        "                \"ram\": 2048,\n" +
//                        "                \"disk\": 20,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.small\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"active\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"sn_test_2\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"net1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.140.52\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:d6:6d:3e\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"e805dd60-3c94-4421-abdd-0dd6ea6852f4\",\n" +
//                        "            \"instance_name\": \"instance-0000006a\",\n" +
//                        "            \"host\": \"control-3\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"SHUTOFF\",\n" +
//                        "            \"status_display\": \"关机\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-29 14:12\",\n" +
//                        "            \"updated_at\": \"2025-11-30 10:43\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": false,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": true,\n" +
//                        "            \"power_off_enabled\": false,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": false,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": false,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": false,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": false,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 1,\n" +
//                        "                \"ram\": 1024,\n" +
//                        "                \"disk\": 5,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"amphora\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"stopped\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"test1\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"net1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.140.84\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:5a:b5:f8\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"96a896bf-2763-4dee-bc7c-5c91afaafa5f\",\n" +
//                        "            \"instance_name\": \"instance-0000005b\",\n" +
//                        "            \"host\": \"control-2\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"SHUTOFF\",\n" +
//                        "            \"status_display\": \"关机\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-27 20:55\",\n" +
//                        "            \"updated_at\": \"2025-11-29 13:31\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": false,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": true,\n" +
//                        "            \"power_off_enabled\": false,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": false,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": false,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": false,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": false,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 2,\n" +
//                        "                \"ram\": 4096,\n" +
//                        "                \"disk\": 40,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.medium\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"stopped\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"HFish-Client\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.29\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:ff:00:18\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
//                        "                \"uuid\": \"b345f83a-c03c-4af4-8e37-e2e56732e713\",\n" +
//                        "                \"name\": \"openEuler-2203-lts-sp3-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 40,\n" +
//                        "                \"min_ram\": 4096,\n" +
//                        "                \"size\": 42949672960,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": null,\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": null,\n" +
//                        "                \"hw_cdrom_bus\": \"sata\",\n" +
//                        "                \"os_distro\": \"openEuler\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"openeuler\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/openEuler-2203-lts-sp3-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"sata\",\n" +
//                        "                    \"os_admin_user\": \"openeuler\",\n" +
//                        "                    \"os_distro\": \"openEuler\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"a5e195bc3a126fc910593377d3fb2b75c10365ca9e353b68ea83519062159a22acd25d4e8ef8ee392c6f18336e240cf5f57031797b1d328e0a7ff992ed2fe7eb\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/b345f83a-c03c-4af4-8e37-e2e56732e713/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:50\",\n" +
//                        "                \"updated_at\": \"2025-11-19 17:03\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"1e7a893e-cdce-4863-bcae-cb0263b2b18f\",\n" +
//                        "            \"instance_name\": \"instance-00000055\",\n" +
//                        "            \"host\": \"control-2\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"ACTIVE\",\n" +
//                        "            \"status_display\": \"可用\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-27 20:35\",\n" +
//                        "            \"updated_at\": \"2025-11-27 20:35\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": true,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": false,\n" +
//                        "            \"power_off_enabled\": true,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": true,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": true,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": true,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": true,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 4,\n" +
//                        "                \"ram\": 8192,\n" +
//                        "                \"disk\": 80,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.large\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"active\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"ubuntu24\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.14\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:6e:2f:92\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": null,\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"1330896b-974f-49aa-bea0-4196e1b50557\",\n" +
//                        "            \"instance_name\": \"instance-0000003d\",\n" +
//                        "            \"host\": \"control-1\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"ACTIVE\",\n" +
//                        "            \"status_display\": \"可用\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-23 11:15\",\n" +
//                        "            \"updated_at\": \"2025-11-23 15:00\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": true,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": false,\n" +
//                        "            \"power_off_enabled\": true,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": true,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": true,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": false,\n" +
//                        "            \"backup_enabled\": false,\n" +
//                        "            \"rebuild_enabled\": false,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": true,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": true,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 2,\n" +
//                        "                \"ram\": 4096,\n" +
//                        "                \"disk\": 40,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.medium\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"active\",\n" +
//                        "            \"volumes_attached\": [\n" +
//                        "                {\n" +
//                        "                    \"id\": \"01a0cca1-749d-47e9-ba02-d5f976192fb8\",\n" +
//                        "                    \"delete_on_termination\": true\n" +
//                        "                },\n" +
//                        "                {\n" +
//                        "                    \"id\": \"4223d941-2349-44a1-84b9-17d3e8866b6c\",\n" +
//                        "                    \"delete_on_termination\": true\n" +
//                        "                }\n" +
//                        "            ],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"test-3\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.52\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:7d:9a:00\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"cc003ee9-d536-4a5a-a7c3-3dbd78c5b387\",\n" +
//                        "            \"instance_name\": \"instance-00000007\",\n" +
//                        "            \"host\": \"control-3\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"SHUTOFF\",\n" +
//                        "            \"status_display\": \"关机\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-19 17:36\",\n" +
//                        "            \"updated_at\": \"2025-11-29 14:11\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": false,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": true,\n" +
//                        "            \"power_off_enabled\": false,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": false,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": false,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": false,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": false,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 1,\n" +
//                        "                \"ram\": 512,\n" +
//                        "                \"disk\": 1,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.tiny\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"stopped\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"test-2\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.18\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:b9:de:d4\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"d20efab6-9d6e-4d46-8012-59d3e5ac3252\",\n" +
//                        "            \"instance_name\": \"instance-00000004\",\n" +
//                        "            \"host\": \"control-2\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"SHUTOFF\",\n" +
//                        "            \"status_display\": \"关机\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-19 17:36\",\n" +
//                        "            \"updated_at\": \"2025-11-29 14:10\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": false,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": true,\n" +
//                        "            \"power_off_enabled\": false,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": false,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": false,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": false,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": false,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 1,\n" +
//                        "                \"ram\": 512,\n" +
//                        "                \"disk\": 1,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.tiny\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"stopped\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"test-1\",\n" +
//                        "            \"az\": \"nova\",\n" +
//                        "            \"address\": {\n" +
//                        "                \"network1\": {\n" +
//                        "                    \"floating\": [],\n" +
//                        "                    \"fixed\": [\n" +
//                        "                        {\n" +
//                        "                            \"version\": 4,\n" +
//                        "                            \"addr\": \"192.168.141.3\",\n" +
//                        "                            \"OS-EXT-IPS:type\": \"fixed\",\n" +
//                        "                            \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:7e:3f:3a\"\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"image_id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "            \"full_image\": {\n" +
//                        "                \"id\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"uuid\": \"988aa85b-24c3-4d7d-93ce-d0fdac73fbbe\",\n" +
//                        "                \"name\": \"cirros-arm64\",\n" +
//                        "                \"image_type\": \"image\",\n" +
//                        "                \"image_type_display\": \"镜像\",\n" +
//                        "                \"status\": \"active\",\n" +
//                        "                \"status_display\": \"可用\",\n" +
//                        "                \"min_disk\": 1,\n" +
//                        "                \"min_ram\": 256,\n" +
//                        "                \"size\": 117440512,\n" +
//                        "                \"disk_format\": \"raw\",\n" +
//                        "                \"protected\": false,\n" +
//                        "                \"visibility\": \"public\",\n" +
//                        "                \"architecture\": \"aarch64\",\n" +
//                        "                \"hw_video_model\": null,\n" +
//                        "                \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                \"os_distro\": \"cirros\",\n" +
//                        "                \"os_type\": \"linux\",\n" +
//                        "                \"os_admin_user\": \"cirros\",\n" +
//                        "                \"is_stable\": true,\n" +
//                        "                \"delete_enabled\": true,\n" +
//                        "                \"tags\": [],\n" +
//                        "                \"properties\": {\n" +
//                        "                    \"owner_specified.openstack.md5\": \"\",\n" +
//                        "                    \"owner_specified.openstack.sha256\": \"\",\n" +
//                        "                    \"owner_specified.openstack.object\": \"images/cirros-arm64\",\n" +
//                        "                    \"cpu_arch\": \"aarch64\",\n" +
//                        "                    \"hw_boot_menu\": \"true\",\n" +
//                        "                    \"hw_cdrom_bus\": \"scsi\",\n" +
//                        "                    \"os_admin_user\": \"cirros\",\n" +
//                        "                    \"os_distro\": \"cirros\",\n" +
//                        "                    \"os_type\": \"linux\",\n" +
//                        "                    \"hw_qemu_guest_agent\": \"no\",\n" +
//                        "                    \"hw_scsi_model\": \"virtio-scsi\",\n" +
//                        "                    \"os_hidden\": false,\n" +
//                        "                    \"os_hash_algo\": \"sha512\",\n" +
//                        "                    \"os_hash_value\": \"5ec80aa349713f06c9a4ae97bc2d6c4059b80a17837cebf052445199bc96fc36307e358de391eb85179744c5848fec6a2247d39feda4f261a43bff046cc67148\",\n" +
//                        "                    \"direct_url\": \"rbd://382ee7c7-336a-41ae-b71e-9c14365bdcd3/images/988aa85b-24c3-4d7d-93ce-d0fdac73fbbe/snap\",\n" +
//                        "                    \"stores\": \"rbd\"\n" +
//                        "                },\n" +
//                        "                \"created_at\": \"2025-11-19 16:45\",\n" +
//                        "                \"updated_at\": \"2025-11-24 14:24\"\n" +
//                        "            },\n" +
//                        "            \"server_group\": null,\n" +
//                        "            \"uuid\": \"86d4c304-1d14-4da3-bb41-ca52e29d97dc\",\n" +
//                        "            \"instance_name\": \"instance-00000001\",\n" +
//                        "            \"host\": \"control-1\",\n" +
//                        "            \"fault\": null,\n" +
//                        "            \"status\": \"ACTIVE\",\n" +
//                        "            \"status_display\": \"可用\",\n" +
//                        "            \"task\": null,\n" +
//                        "            \"task_display\": null,\n" +
//                        "            \"created_at\": \"2025-11-19 17:35\",\n" +
//                        "            \"updated_at\": \"2025-11-29 15:04\",\n" +
//                        "            \"is_stable\": true,\n" +
//                        "            \"vnc_enabled\": true,\n" +
//                        "            \"delete_enabled\": true,\n" +
//                        "            \"reboot_enabled\": true,\n" +
//                        "            \"reset_enabled\": false,\n" +
//                        "            \"power_on_enabled\": false,\n" +
//                        "            \"power_off_enabled\": true,\n" +
//                        "            \"associate_floating_ip_enabled\": true,\n" +
//                        "            \"disassociate_floating_ip_enabled\": false,\n" +
//                        "            \"pause_enabled\": true,\n" +
//                        "            \"unpause_enabled\": false,\n" +
//                        "            \"suspend_enabled\": true,\n" +
//                        "            \"unsuspend_enabled\": false,\n" +
//                        "            \"resize_enabled\": true,\n" +
//                        "            \"confirm_resize_enabled\": false,\n" +
//                        "            \"revert_resize_enabled\": false,\n" +
//                        "            \"snapshot_enabled\": true,\n" +
//                        "            \"backup_enabled\": true,\n" +
//                        "            \"rebuild_enabled\": true,\n" +
//                        "            \"migrate_enabled\": true,\n" +
//                        "            \"live_migrate_enabled\": true,\n" +
//                        "            \"change_password_enabled\": false,\n" +
//                        "            \"volume_attach_enabled\": true,\n" +
//                        "            \"edit_enabled\": true,\n" +
//                        "            \"metadata\": {},\n" +
//                        "            \"tags\": null,\n" +
//                        "            \"current_conf\": {\n" +
//                        "                \"vcpus\": 1,\n" +
//                        "                \"ram\": 512,\n" +
//                        "                \"disk\": 1,\n" +
//                        "                \"ephemeral\": 0,\n" +
//                        "                \"swap\": 0,\n" +
//                        "                \"original_name\": \"m1.tiny\",\n" +
//                        "                \"extra_specs\": {}\n" +
//                        "            },\n" +
//                        "            \"vm_state\": \"active\",\n" +
//                        "            \"volumes_attached\": [],\n" +
//                        "            \"is_baremetal_instance\": false,\n" +
//                        "            \"iso_image_attach_enabled\": true,\n" +
//                        "            \"disaster_recovery\": {\n" +
//                        "                \"enabled\": false,\n" +
//                        "                \"current_cluster\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\"\n" +
//                        "            },\n" +
//                        "            \"dr_clone_enabled\": false\n" +
//                        "        }\n" +
//                        "    ]\n" +
//                        "}");

                JSONObject jsonObject = JSON.parseObject(virResult.getResult());
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                if (resultsArray !=null && resultsArray.size() > 0) {
                    for (int i = 0; i < resultsArray.size(); i++) {
                        XdVirVO xdVirVO = new XdVirVO();
                        xdVirVO.setCompany("1");
                        xdVirVO.setCompany_name("华网创新");
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
                log.error("XdSourceController.virList one error.",  e);
        }
        try {
            xdTwoHttpClient.init();
            HttpResult virResult = xdTwoHttpClient.xdGet("/janus/20180725/servers", null);
//            HttpResult virResult = xdTwoHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//            virResult.setResult("{\n" +
//                    "    \"message\": \"\",\n" +
//                    "    \"code\": 0,\n" +
//                    "    \"data\": {\n" +
//                    "        \"total_size\": 10,\n" +
//                    "        \"data\": [\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 512.0,\n" +
//                    "                    \"read_speed_bitps\": 4704.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 51200.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.01,\n" +
//                    "                    \"used_mhz\": 99.94\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 51200.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:5D:74:F5\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.212\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 601390,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 2239.78,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.55\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"685f1b11-5486-44e2-a1ec-57313a76c3f8\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 31744.0,\n" +
//                    "                    \"write_iops\": 2.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.212\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.212 waf + hfish\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 14680.8,\n" +
//                    "                    \"total_mb\": 51200.0,\n" +
//                    "                    \"storage_file_size_mb\": 18607.62,\n" +
//                    "                    \"ratio\": 0.29\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Ubuntu 24.04.3 LTS\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 624.0,\n" +
//                    "                    \"read_speed_bitps\": 4704.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 51200.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 51200.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:5F:63:0E\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.218\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 510515,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1428.68,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.35\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"892c0f6e-1b4b-453b-b442-83e79b6a9831\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 11776.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.218\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.218 拒绝服务攻击实验 ubuntu 24.04\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 14812.84,\n" +
//                    "                    \"total_mb\": 51200.0,\n" +
//                    "                    \"storage_file_size_mb\": 20140.75,\n" +
//                    "                    \"ratio\": 0.29\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Ubuntu 24.04.3 LTS\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 432.0,\n" +
//                    "                    \"read_speed_bitps\": 4704.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 51200.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 51200.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:B5:C8:60\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.219\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 500070,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1478.23,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.36\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"def9def4-fbc0-4fdc-976d-31f07022f265\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 48640.0,\n" +
//                    "                    \"write_iops\": 9.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.219\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.219 科目拒绝服务攻击 ubuntu-24.04\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 15922.16,\n" +
//                    "                    \"total_mb\": 51200.0,\n" +
//                    "                    \"storage_file_size_mb\": 20857.5,\n" +
//                    "                    \"ratio\": 0.31\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Ubuntu 24.04.3 LTS\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 432.0,\n" +
//                    "                    \"read_speed_bitps\": 4704.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 51200.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 51200.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:66:17:A0\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.214\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 500213,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1047.83,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.26\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"f465911c-69e5-4e32-90b6-84b19ee331a6\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 11264.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.214\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.214 0day应对 ubuntu-24.04\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 11937.91,\n" +
//                    "                    \"total_mb\": 51200.0,\n" +
//                    "                    \"storage_file_size_mb\": 15066.38,\n" +
//                    "                    \"ratio\": 0.23\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Ubuntu 24.04.3 LTS\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 0.0,\n" +
//                    "                    \"read_speed_bitps\": 4680.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 40960.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.24,\n" +
//                    "                    \"used_mhz\": 2398.51\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 40960.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:06:B0:AC\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 433360,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 2154.84,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.53\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"e57f2336-8031-4b39-b344-34802b7c7edd\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 2806623.0,\n" +
//                    "                    \"read_iops\": 138.0,\n" +
//                    "                    \"write_speed_byteps\": 105360.0,\n" +
//                    "                    \"write_iops\": 5.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.221 360沙箱 win10\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 4,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 27569.12,\n" +
//                    "                    \"total_mb\": 40954.77,\n" +
//                    "                    \"storage_file_size_mb\": 32355.62,\n" +
//                    "                    \"ratio\": 0.67\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"other64\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Windows 10\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 776.0,\n" +
//                    "                    \"read_speed_bitps\": 5464.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 40960.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 40960.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:0E:8B:55\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.235\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 338305,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1633.47,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.4\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"351ddc89-c378-4cc4-9e54-628a36b25637\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 512.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.235\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"Centos7 vulhub_模板部署\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 19774.85,\n" +
//                    "                    \"total_mb\": 40960.0,\n" +
//                    "                    \"storage_file_size_mb\": 20816.0,\n" +
//                    "                    \"ratio\": 0.48\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"CentOS Linux release 7.9.2009\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 0.0,\n" +
//                    "                    \"read_speed_bitps\": 4680.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 40960.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 40960.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:10:81:7C\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.56\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 83827,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1616.52,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.39\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"20368d90-01f0-40ae-9774-2583935a58a9\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 512.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.56\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"km2-1\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 19776.87,\n" +
//                    "                    \"total_mb\": 40960.0,\n" +
//                    "                    \"storage_file_size_mb\": 20816.0,\n" +
//                    "                    \"ratio\": 0.48\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"CentOS Linux release 7.9.2009\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 0.0,\n" +
//                    "                    \"read_speed_bitps\": 4680.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 40960.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 40960.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"\",\n" +
//                    "                    \"warning\": 0,\n" +
//                    "                    \"warning_type\": \"\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"72a6ff36-7d00-4a41-b688-f86e62016101\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:56:2A:A4\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"virtio\",\n" +
//                    "                        \"ip_address\": \"192.168.140.233\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"ee104c6e-030b-487d-bbc3-e746334ac059\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 1,\n" +
//                    "                \"uptime\": 80848,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1581.05,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.39\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"88c095c0-dd77-492b-aa30-ead094338882\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 512.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [\n" +
//                    "                    \"192.168.140.233\"\n" +
//                    "                ],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5875d54\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.233 Centos7 struts2\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 2,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 19774.86,\n" +
//                    "                    \"total_mb\": 40960.0,\n" +
//                    "                    \"storage_file_size_mb\": 20832.12,\n" +
//                    "                    \"ratio\": 0.48\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.13\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"CentOS Linux release 7.9.2009\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 0.0,\n" +
//                    "                    \"read_speed_bitps\": 17784.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 8192.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 19987.55,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 8192.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"云主机未安装性能优化工具\",\n" +
//                    "                    \"warning\": 1,\n" +
//                    "                    \"warning_type\": \"vtool_uninstalled\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1_f76ae1e5\",\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"b2bf40b4-2637-4849-b2f2-e3b37236d16b\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:A4:54:B8\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"rtl8139\",\n" +
//                    "                        \"ip_address\": \"\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"c7fbd868-e1f6-480c-851d-7b387cee13ea\"\n" +
//                    "                    },\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": \"物理出口1_f76ae1e5\",\n" +
//                    "                        \"vif_id\": \"net1\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"b2bf40b4-2637-4849-b2f2-e3b37236d16b\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:41:8F:E9\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"e1000\",\n" +
//                    "                        \"ip_address\": \"\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": \"c7fbd868-e1f6-480c-851d-7b387cee13ea\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 0,\n" +
//                    "                \"uptime\": 504003,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 642.53,\n" +
//                    "                    \"total_mb\": 8192.0,\n" +
//                    "                    \"ratio\": 0.08\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"06fbd3b6-e712-44df-ad4e-4c08d3efebc7\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 19987.55,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 1639.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba5876cb0\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"192.168.140.206msflinux\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 1,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 4,\n" +
//                    "                \"memory_mb\": 8192.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 2560.0,\n" +
//                    "                    \"total_mb\": 8192.0,\n" +
//                    "                    \"storage_file_size_mb\": 2609.12,\n" +
//                    "                    \"ratio\": 0.31\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.12\",\n" +
//                    "                \"cores\": 8,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [\n" +
//                    "                    \"classic\"\n" +
//                    "                ],\n" +
//                    "                \"os_name\": \"Ubuntu\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"network_status\": {\n" +
//                    "                    \"write_speed_bitps\": 0.0,\n" +
//                    "                    \"read_speed_bitps\": 0.0\n" +
//                    "                },\n" +
//                    "                \"disks\": [\n" +
//                    "                    {\n" +
//                    "                        \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                        \"preallocate\": \"off\",\n" +
//                    "                        \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                        \"eagerly_scrub\": 0,\n" +
//                    "                        \"size_mb\": 51200.0,\n" +
//                    "                        \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                        \"storage_file\": \"vm-disk-1.qcow2\",\n" +
//                    "                        \"id\": \"ide0\",\n" +
//                    "                        \"physical_disk_type\": \"\"\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"cpu_status\": {\n" +
//                    "                    \"total_mhz\": 9993.78,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"used_mhz\": 0.0\n" +
//                    "                },\n" +
//                    "                \"storage_mb\": 51200.0,\n" +
//                    "                \"group_name\": \"\",\n" +
//                    "                \"vmtype\": \"vm\",\n" +
//                    "                \"keypair\": null,\n" +
//                    "                \"warning\": {\n" +
//                    "                    \"warning_info\": \"云主机未安装性能优化工具\",\n" +
//                    "                    \"warning\": 1,\n" +
//                    "                    \"warning_type\": \"vtool_uninstalled\"\n" +
//                    "                },\n" +
//                    "                \"encrypted\": 0,\n" +
//                    "                \"gpu_conf\": null,\n" +
//                    "                \"shutdown_duration\": 0,\n" +
//                    "                \"az_name\": \"HCI\",\n" +
//                    "                \"instant_vm\": \"\",\n" +
//                    "                \"networks\": [\n" +
//                    "                    {\n" +
//                    "                        \"ipv6_address\": \"\",\n" +
//                    "                        \"name\": null,\n" +
//                    "                        \"vif_id\": \"net0\",\n" +
//                    "                        \"subnet_id\": \"\",\n" +
//                    "                        \"vpc_name\": \"\",\n" +
//                    "                        \"custom_gateway_ip\": null,\n" +
//                    "                        \"cidr\": \"\",\n" +
//                    "                        \"subnet_gateway_ip\": \"\",\n" +
//                    "                        \"subnet_name\": \"\",\n" +
//                    "                        \"connect\": 1,\n" +
//                    "                        \"port_id\": \"\",\n" +
//                    "                        \"mac_address\": \"FE:FC:FE:89:F9:04\",\n" +
//                    "                        \"vpc_id\": \"\",\n" +
//                    "                        \"model\": \"e1000\",\n" +
//                    "                        \"ip_address\": \"\",\n" +
//                    "                        \"network_type\": \"\",\n" +
//                    "                        \"device_id\": null\n" +
//                    "                    }\n" +
//                    "                ],\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"storage_tag_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
//                    "                \"vtool_installed\": 0,\n" +
//                    "                \"uptime\": 158808,\n" +
//                    "                \"user_id\": null,\n" +
//                    "                \"floatingip\": {\n" +
//                    "                    \"sharedbandwidth_name\": \"\",\n" +
//                    "                    \"floatingip_id\": \"\",\n" +
//                    "                    \"bandwidth\": 0,\n" +
//                    "                    \"floating_ip_address\": \"\",\n" +
//                    "                    \"bind_status\": \"\",\n" +
//                    "                    \"line_type\": \"\",\n" +
//                    "                    \"line_type_id\": \"\",\n" +
//                    "                    \"sharedbandwidth_id\": \"\"\n" +
//                    "                },\n" +
//                    "                \"protection_enable\": \"\",\n" +
//                    "                \"memory_status\": {\n" +
//                    "                    \"used_mb\": 1241.99,\n" +
//                    "                    \"total_mb\": 4096.0,\n" +
//                    "                    \"ratio\": 0.3\n" +
//                    "                },\n" +
//                    "                \"gpu_status\": {\n" +
//                    "                    \"graphics_mem_total\": 0,\n" +
//                    "                    \"graphics_count\": 0,\n" +
//                    "                    \"graphics_mem_used\": 0,\n" +
//                    "                    \"graphics_mem_ratio\": 0.0,\n" +
//                    "                    \"graphics_ratio\": 0.0\n" +
//                    "                },\n" +
//                    "                \"dh_id\": \"\",\n" +
//                    "                \"id\": \"8d83a17d-1fa0-4305-9270-eaaa3ba85aea\",\n" +
//                    "                \"is_stopped\": 0,\n" +
//                    "                \"advance_param\": {\n" +
//                    "                    \"schedopt\": 0,\n" +
//                    "                    \"balloon_memory\": 1,\n" +
//                    "                    \"onboot\": 0,\n" +
//                    "                    \"abnormal_recovery\": 0\n" +
//                    "                },\n" +
//                    "                \"schedule_policies\": [],\n" +
//                    "                \"power_state\": \"on\",\n" +
//                    "                \"storage_id\": \"62c115ff_vs_vol_rep3\",\n" +
//                    "                \"mhz\": 9993.78,\n" +
//                    "                \"project_id\": \"3f24c204a7504a1fba9db087ba03701e\",\n" +
//                    "                \"user_name\": \"\",\n" +
//                    "                \"location\": 0,\n" +
//                    "                \"io_status\": {\n" +
//                    "                    \"read_speed_byteps\": 0.0,\n" +
//                    "                    \"read_iops\": 0.0,\n" +
//                    "                    \"write_speed_byteps\": 0.0,\n" +
//                    "                    \"write_iops\": 0.0\n" +
//                    "                },\n" +
//                    "                \"project_name\": \"admin\",\n" +
//                    "                \"sockets\": 2,\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"group_id_path\": \"\",\n" +
//                    "                \"tags\": [],\n" +
//                    "                \"ips\": [],\n" +
//                    "                \"group_name_path\": \"\",\n" +
//                    "                \"image_name\": \"\",\n" +
//                    "                \"has_gpu\": 0,\n" +
//                    "                \"image_id\": \"\",\n" +
//                    "                \"protection_name\": \"\",\n" +
//                    "                \"protection_type\": \"\",\n" +
//                    "                \"protection_id\": \"\",\n" +
//                    "                \"expire_time\": \"unlimited\",\n" +
//                    "                \"host_id\": \"host-189ba58770bc\",\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"dh_name\": \"\",\n" +
//                    "                \"group_id\": \"\",\n" +
//                    "                \"name\": \"新型网络dns隧道搭建 ubuntu-24.04\",\n" +
//                    "                \"backup_file_count\": 0,\n" +
//                    "                \"os_installed\": 1,\n" +
//                    "                \"type\": \"hci\",\n" +
//                    "                \"alarm\": {\n" +
//                    "                    \"alarm_info\": [],\n" +
//                    "                    \"alarm\": 0\n" +
//                    "                },\n" +
//                    "                \"storage_name\": \"虚拟存储卷1\",\n" +
//                    "                \"cores_per_socket\": 2,\n" +
//                    "                \"memory_mb\": 4096.0,\n" +
//                    "                \"storage_status\": {\n" +
//                    "                    \"used_mb\": 14336.0,\n" +
//                    "                    \"total_mb\": 51200.0,\n" +
//                    "                    \"storage_file_size_mb\": 14400.0,\n" +
//                    "                    \"ratio\": 0.28\n" +
//                    "                },\n" +
//                    "                \"cipher\": \"\",\n" +
//                    "                \"in_protection\": 0,\n" +
//                    "                \"host_name\": \"192.168.140.11\",\n" +
//                    "                \"cores\": 4,\n" +
//                    "                \"os_type\": \"l2664\",\n" +
//                    "                \"floatingips\": [],\n" +
//                    "                \"network_type\": [],\n" +
//                    "                \"os_name\": \"Ubuntu\",\n" +
//                    "                \"iolog\": []\n" +
//                    "            }\n" +
//                    "        ],\n" +
//                    "        \"next_page_num\": \"\",\n" +
//                    "        \"page_num\": 0,\n" +
//                    "        \"page_size\": 100\n" +
//                    "    }\n" +
//                    "}");
            JSONObject jsonObject = JSON.parseObject(virResult.getResult());
            if (jsonObject != null && jsonObject.getJSONObject("data") != null && CollectionUtils.isNotEmpty(jsonObject.getJSONObject("data").getJSONArray("data"))) {
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++) {
                    XdVirVO xdVirVO = new XdVirVO();
                    xdVirVO.setCompany("2");
                    xdVirVO.setCompany_name("深信服");
                    xdVirVO.setId(jsonArray.getJSONObject(i).getString("id"));
                    xdVirVO.setName(jsonArray.getJSONObject(i).getString("name"));
                    xdVirVO.setImageId(jsonArray.getJSONObject(i).getString("image_id"));
                    xdVirVO.setImageName(jsonArray.getJSONObject(i).getString("image_name"));
                    if ("running".equals(jsonArray.getJSONObject(i).getString("status"))) {
                        xdVirVO.setStatus("ACTIVE");
                        xdVirVO.setStatusDisplay("可用");
                    } else {
                        xdVirVO.setStatus("SHUTOFF");
                        xdVirVO.setStatusDisplay("关机");
                    }
                    if (CollectionUtils.isNotEmpty(jsonArray.getJSONObject(i).getJSONArray("networks"))) {
                        JSONArray networks = jsonArray.getJSONObject(i).getJSONArray("networks");
                        String addr = "";
                        for (int j=0;j<networks.size();j++) {
                            if (StringUtils.isNotEmpty(networks.getJSONObject(j).getString("ip_address"))) {
                                addr += "," + networks.getJSONObject(j).getString("ip_address");
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
        } catch (Exception e) {
            log.error("XdSourceController.phyList two error.",  e);
        }
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("开机")
    @ApiImplicitParam(name = "xdVirUpShut", value = "新增超融合平台", dataTypeClass = XdVirUpShut.class)
    @PostMapping("/up_shut")
    public ServiceResult<PlatformVO> upShut(@RequestBody XdVirUpShut xdVirUpShut) {
        log.info("XdSourceController.virList begin. xdVirUpShut:{}", xdVirUpShut);
        try {
            if ("1".equals(xdVirUpShut.getCompany())) {
                xdOneHttpClient.init();
                HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
                ObjectMapper objectMapper = new ObjectMapper();
                Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
                if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
                    Action action = new Action();
                    action.setAction(xdVirUpShut.getAction());
                    HttpResult actionResult = xdOneHttpClient.xdPost("/instances/" + xdVirUpShut.getId() + "/action/?cluster=" + cluster.getResults().get(0).getUuid(), null, action);
                    JSONObject jsonObject = JSON.parseObject(actionResult.getResult());
                    if (jsonObject.getBoolean("success")!=null && jsonObject.getBoolean("success")) {
                        return ResultCommon.success();
                    } else if (StringUtils.isNotEmpty(jsonObject.getString("message"))) {
                        return ResultCommon.fail(jsonObject.getString("message"));
                    }
                }
            } else if ("2".equals(xdVirUpShut.getCompany())) {
                xdTwoHttpClient.init();

                String url = "";
                if ("power_on".equals(xdVirUpShut.getAction())) {
                    url = "/janus/20180725/servers/" + xdVirUpShut.getId() + "/start";
                } else if ("power_off".equals(xdVirUpShut.getAction())) {
                    url = "/janus/20180725/servers/" + xdVirUpShut.getId() + "/stop";
                }
                HttpResult actionResult = xdTwoHttpClient.xdPost(url, null, null);
                JSONObject jsonObject = JSON.parseObject(actionResult.getResult());
                return ResultCommon.success();
            }
        } catch (Exception e) {
            log.error("XdSourceController.upShut error.",  e);
        }
        return ResultCommon.success();
    }




    @ApiOperation("物理资源统计")
    @GetMapping("/phy_statics")
    public ServiceResult<XdPhyStatistics> phyStatics() {
        List<XdPhyVO> result = new ArrayList<>();

        try {
            xdOneHttpClient.init();
            HttpResult clusterResult = xdOneHttpClient.xdGet("/clusters?detail=true&tenantId=2", null);
//            HttpResult clusterResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
            ObjectMapper objectMapper = new ObjectMapper();
//            clusterResult.setResult("{\n" +
//                    "    \"count\": 1,\n" +
//                    "    \"next\": null,\n" +
//                    "    \"previous\": null,\n" +
//                    "    \"results\": [\n" +
//                    "        {\n" +
//                    "            \"uuid\": \"382ee7c7-336a-41ae-b71e-9c14365bdcd3\",\n" +
//                    "            \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"updated_at\": \"2025-11-19 17:14\",\n" +
//                    "            \"name\": \"root-cluster-382e\",\n" +
//                    "            \"provider\": \"openstack\",\n" +
//                    "            \"description\": null,\n" +
//                    "            \"external\": false,\n" +
//                    "            \"is_local\": true,\n" +
//                    "            \"properties\": {},\n" +
//                    "            \"default_cluster\": true,\n" +
//                    "            \"disaster_recovery\": {},\n" +
//                    "            \"services\": [\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"e9fc3317-7758-4837-82f2-f5c275692fe2\",\n" +
//                    "                    \"name\": \"ops\",\n" +
//                    "                    \"type\": \"ops\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10040,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"4a2080ff-d560-4e12-bf89-4e03679bc517\",\n" +
//                    "                    \"name\": \"monitor\",\n" +
//                    "                    \"type\": \"monitor\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10080,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"ce257510-d573-4b22-b1e3-bb0ff1811180\",\n" +
//                    "                    \"name\": \"openstack\",\n" +
//                    "                    \"type\": \"openstack\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5000,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"admin\",\n" +
//                    "                        \"domain_name\": \"Default\",\n" +
//                    "                        \"auth_url\": \"http://192.168.140.4:5000\",\n" +
//                    "                        \"project_name\": \"admin\",\n" +
//                    "                        \"region_name\": \"RegionOne\",\n" +
//                    "                        \"password\": \"FHgL3AfMOi9SXXzaOt9Nu8aJ3G5UJ4t0AVFKISQN\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"d86fc778-c48d-412b-af51-6188cd727669\",\n" +
//                    "                    \"name\": \"rabbitmq\",\n" +
//                    "                    \"type\": \"rabbitmq\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 5672,\n" +
//                    "                    \"auth\": {\n" +
//                    "                        \"username\": \"openstack\",\n" +
//                    "                        \"password\": \"sqV3CZUeCm76sGLjPKMOAfjR4h7NIr1RL9gavFyt\"\n" +
//                    "                    },\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"uuid\": \"98ff59f4-4713-4090-a2d4-c145ab91c41c\",\n" +
//                    "                    \"name\": \"octopus\",\n" +
//                    "                    \"type\": \"octopus\",\n" +
//                    "                    \"host\": \"192.168.140.4\",\n" +
//                    "                    \"port\": 10101,\n" +
//                    "                    \"auth\": null,\n" +
//                    "                    \"created_at\": \"2025-11-19 17:14\",\n" +
//                    "                    \"updated_at\": \"2025-11-19 17:14\"\n" +
//                    "                }\n" +
//                    "            ]\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}");
            Cluster cluster = objectMapper.readValue(clusterResult.getResult(), Cluster.class);
            if (cluster != null && CollectionUtils.isNotEmpty(cluster.getResults()) && StringUtils.isNotEmpty(cluster.getResults().get(0).getUuid())) {
                HttpResult phyStaResult = xdOneHttpClient.xdGet("/proxy/monitor/nodes?cluster=" + cluster.getResults().get(0).getUuid(), null);
//                HttpResult phyStaResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//                phyStaResult.setResult("{\n" +
//                        "    \"192.168.140.5\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"3.58\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"41\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sda1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"155982388565.33\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"397\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"461\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    },\n" +
//                        "    \"192.168.140.6\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"2.8\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"36\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sdd1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sdd2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sdd3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"46340962304\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"335\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"313\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"vxlan_sys_4789\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    },\n" +
//                        "    \"192.168.140.7\": {\n" +
//                        "        \"cpu\": {\n" +
//                        "            \"util\": {\n" +
//                        "                \"value\": \"3.52\",\n" +
//                        "                \"unit\": \"%\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"memory\": {\n" +
//                        "            \"used\": {\n" +
//                        "                \"value\": \"34\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            },\n" +
//                        "            \"total\": {\n" +
//                        "                \"value\": \"754\",\n" +
//                        "                \"unit\": \"GB\"\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"filesystems\": {\n" +
//                        "            \"/dev/sda1\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"6782976\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"523968512\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda2\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"196136960\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1020702720\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"/dev/sda3\": {\n" +
//                        "                \"used\": {\n" +
//                        "                    \"value\": \"47302253226.67\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                },\n" +
//                        "                \"total\": {\n" +
//                        "                    \"value\": \"1769341595648\",\n" +
//                        "                    \"unit\": \"Bytes\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        \"network\": {\n" +
//                        "            \"enp125s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp125s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f0\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f1\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f2\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"424\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"427\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"enp189s0f3\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"1\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            \"vxlan_sys_4789\": {\n" +
//                        "                \"receive_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                },\n" +
//                        "                \"transmit_rate\": {\n" +
//                        "                    \"value\": \"0\",\n" +
//                        "                    \"unit\": \"KB/s\"\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        }\n" +
//                        "    }\n" +
//                        "}");
                JSONObject phyStaJson = JSON.parseObject(phyStaResult.getResult());

                HttpResult phyResult = xdOneHttpClient.xdGet("/proxy/monitor/prometheus/measures?cluster=" + cluster.getResults().get(0).getUuid() + "&queries[]=max_over_time(node_dmi_info[1y])&queries[]=max_over_time(node_os_info[1y])&queries[]=max_over_time(node_uname_info[1y])", null);
//                HttpResult phyResult = xdOneHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//                phyResult.setResult("[\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"bios_date\": \"01/04/2025\",\n" +
//                        "                    \"bios_release\": \"7.38\",\n" +
//                        "                    \"bios_vendor\": \"Huawei Corp.\",\n" +
//                        "                    \"bios_version\": \"7.38\",\n" +
//                        "                    \"board_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"board_name\": \"BC82AMDDUA\",\n" +
//                        "                    \"board_vendor\": \"PowerLeader\",\n" +
//                        "                    \"board_version\": \"V200R023C10\",\n" +
//                        "                    \"chassis_asset_tag\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"chassis_vendor\": \"PowerLeader\",\n" +
//                        "                    \"chassis_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"product_family\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_name\": \"PR210K\",\n" +
//                        "                    \"product_sku\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"product_version\": \"To be filled by O.E.M.\",\n" +
//                        "                    \"system_vendor\": \"PowerLeader\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.736,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_dmi_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"id\": \"openEuler\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"name\": \"openEuler\",\n" +
//                        "                    \"pretty_name\": \"openEuler 24.03 (LTS-SP2)\",\n" +
//                        "                    \"version\": \"24.03 (LTS-SP2)\",\n" +
//                        "                    \"version_id\": \"24.03\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.769,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_os_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-1\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-2\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"domainname\": \"(none)\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\",\n" +
//                        "                    \"machine\": \"aarch64\",\n" +
//                        "                    \"nodename\": \"control-3\",\n" +
//                        "                    \"release\": \"6.6.0-98.0.0.103.oe2403sp2.aarch64\",\n" +
//                        "                    \"sysname\": \"Linux\",\n" +
//                        "                    \"version\": \"#1 SMP Wed Jun 25 23:19:26 CST 2025\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.797,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"max_over_time(node_uname_info[1y])\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "        \"data\": [\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.5:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.6:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"metric\": {\n" +
//                        "                    \"__name__\": \"up\",\n" +
//                        "                    \"instance\": \"192.168.140.7:9100\",\n" +
//                        "                    \"job\": \"node\"\n" +
//                        "                },\n" +
//                        "                \"value\": [\n" +
//                        "                    1764476065.828,\n" +
//                        "                    \"1\"\n" +
//                        "                ]\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"unit\": null,\n" +
//                        "        \"metric\": \"up\"\n" +
//                        "    }\n" +
//                        "]");
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
                                xdPhyVO.setCompany("1");
                                xdPhyVO.setCompany_name("华网创新");
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
                                                if (StringUtils.isNotEmpty(temp.getUnit()) && StringUtils.isNotEmpty(temp.getValue()) && "bytes".equals(temp.getUnit().toLowerCase())) {
                                                    temp.setValue(String.format("%.2f",Double.parseDouble(temp.getValue())/(1024*1024*1024)));
                                                    temp.setUnit("GB");
                                                }
                                                xdPhyFileSystem.setUsedFileSystem(temp);
                                            }
                                            if (tempObject.getJSONObject("total") != null) {
                                                ValueUnitVO temp = new ValueUnitVO();
                                                temp.setValue(tempObject.getJSONObject("total").getString("value"));
                                                temp.setUnit(tempObject.getJSONObject("total").getString("unit"));
                                                if (StringUtils.isNotEmpty(temp.getUnit()) && StringUtils.isNotEmpty(temp.getValue()) && "bytes".equals(temp.getUnit().toLowerCase())) {
                                                    temp.setValue(String.format("%.2f",Double.parseDouble(temp.getValue())/(1024*1024*1024)));
                                                    temp.setUnit("GB");
                                                }
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
        try {
            xdTwoHttpClient.init();
            HttpResult phyResult = xdTwoHttpClient.xdGet("/janus/20180725/hosts", null);
//            HttpResult phyResult = xdTwoHttpClient.xdGet("/api/mp/devops/cluster/out_interface/page_real_equipment", null);
//            phyResult.setResult("{\n" +
//                    "    \"message\": \"\",\n" +
//                    "    \"code\": 0,\n" +
//                    "    \"data\": {\n" +
//                    "        \"total_size\": 3,\n" +
//                    "        \"data\": [\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.12\",\n" +
//                    "                \"ip\": \"192.168.140.12\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba5876cb0\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 52428.8,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.1,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 15990.041500000001,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.05,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 319800.83,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.11\",\n" +
//                    "                \"ip\": \"192.168.140.11\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba58770bc\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 31457.28,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.06,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 6336.978000000001,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.02,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 316848.9,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"status\": \"running\",\n" +
//                    "                \"type\": \"h\",\n" +
//                    "                \"description\": \"\",\n" +
//                    "                \"name\": \"192.168.140.13\",\n" +
//                    "                \"ip\": \"192.168.140.13\",\n" +
//                    "                \"storage\": {\n" +
//                    "                    \"total_mb\": 25429452.1875\n" +
//                    "                },\n" +
//                    "                \"id\": \"host-189ba5875d54\",\n" +
//                    "                \"reserves\": {\n" +
//                    "                    \"memory_mb\": 0\n" +
//                    "                },\n" +
//                    "                \"memory\": {\n" +
//                    "                    \"used_mb\": 73400.32,\n" +
//                    "                    \"allocation_ratio\": 1.0,\n" +
//                    "                    \"total_mb\": 524288.0,\n" +
//                    "                    \"ratio\": 0.14,\n" +
//                    "                    \"allocation_max\": 491520.0\n" +
//                    "                },\n" +
//                    "                \"gpu\": {\n" +
//                    "                    \"used\": 0.0,\n" +
//                    "                    \"ratio\": 0.0,\n" +
//                    "                    \"memory_used_mb\": 0.0,\n" +
//                    "                    \"memory_ratio\": 0.0,\n" +
//                    "                    \"total\": 0,\n" +
//                    "                    \"memory_total_mb\": 0.0\n" +
//                    "                },\n" +
//                    "                \"alarm_count\": 0,\n" +
//                    "                \"cpu\": {\n" +
//                    "                    \"used_mhz\": 22399.3637,\n" +
//                    "                    \"allocation_ratio\": 2.5,\n" +
//                    "                    \"ratio\": 0.07,\n" +
//                    "                    \"allocation_max\": 290.0,\n" +
//                    "                    \"cores_per_socket\": 32,\n" +
//                    "                    \"socket_count\": 2,\n" +
//                    "                    \"total_mhz\": 319990.91,\n" +
//                    "                    \"type\": \"Hygon C86 7285 32-core Processor\",\n" +
//                    "                    \"core_count\": 64\n" +
//                    "                },\n" +
//                    "                \"az_id\": \"1ea9375a-d2e7-4c7b-bfe3-5b4864fe9b24\",\n" +
//                    "                \"is_use_vs\": 1\n" +
//                    "            }\n" +
//                    "        ],\n" +
//                    "        \"next_page_num\": \"\",\n" +
//                    "        \"page_num\": 0,\n" +
//                    "        \"page_size\": 100\n" +
//                    "    }\n" +
//                    "}");
            JSONObject jsonObject = JSON.parseObject(phyResult.getResult());
            if (jsonObject != null && jsonObject.getJSONObject("data") != null && CollectionUtils.isNotEmpty(jsonObject.getJSONObject("data").getJSONArray("data"))) {
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++) {
                    XdPhyVO xdPhyVO = new XdPhyVO();
                    xdPhyVO.setCompany("2");
                    xdPhyVO.setCompany_name("深信服");
                    xdPhyVO.setNodeName(jsonArray.getJSONObject(i).getString("id"));
                    xdPhyVO.setSysName("");
                    xdPhyVO.setInstance(jsonArray.getJSONObject(i).getString("ip"));
                    if (jsonArray.getJSONObject(i).getJSONObject("cpu") != null) {
                        xdPhyVO.setCpuPercent(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("cpu").getDouble("ratio")*100) + "%");
                    }
                    if (jsonArray.getJSONObject(i).getJSONObject("memory") != null) {
                        ValueUnitVO valueUnitVOUsed = new ValueUnitVO();
                        valueUnitVOUsed.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("memory").getDouble("used_mb")/1024));
                        valueUnitVOUsed.setUnit("GB");
                        xdPhyVO.setUsedMemory(valueUnitVOUsed);
                        ValueUnitVO valueUnitVOTotal = new ValueUnitVO();
                        valueUnitVOTotal.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("memory").getDouble("total_mb")/1024));
                        valueUnitVOTotal.setUnit("GB");
                        xdPhyVO.setTotalMemory(valueUnitVOTotal);
                    }
                    if (jsonArray.getJSONObject(i).getJSONObject("storage") != null) {
                        ValueUnitVO valueUnitVOTotal = new ValueUnitVO();
                        valueUnitVOTotal.setValue(String.format("%.2f",jsonArray.getJSONObject(i).getJSONObject("storage").getDouble("total_mb")/1024));
                        valueUnitVOTotal.setUnit("GB");
                        ValueUnitVO valueUnitVOUsed = new ValueUnitVO();
                        valueUnitVOUsed.setValue("");
                        valueUnitVOUsed.setUnit("");
                        XdPhyFileSystem xdPhyFileSystem = new XdPhyFileSystem();
                        xdPhyFileSystem.setName("总量");
                        xdPhyFileSystem.setTotalFileSystem(valueUnitVOTotal);
                        xdPhyFileSystem.setUsedFileSystem(valueUnitVOUsed);
                        List<XdPhyFileSystem> xdPhyFileSystemList = new ArrayList<>();
                        xdPhyFileSystemList.add(xdPhyFileSystem);
                        xdPhyVO.setFileSystems(xdPhyFileSystemList);
                    }
                    result.add(xdPhyVO);
                }
            }
        } catch (Exception e) {
            log.error("XdSourceController.phyList two error.",  e);
        }

        XdPhyStatistics xdPhyStatistics = new XdPhyStatistics();
        xdPhyStatistics.setCompanyName1("华网创新");
        xdPhyStatistics.setCompanyName2("深信服");
        xdPhyStatistics.setCompanyName3("信大网域");
        Double cpuPercent1 = 0.00D;
        Integer cpuNum1 = 0;
        Double memoryUsed1 = 0.00D;
        Double memoryTotal1 = 0.00D;
        Double fileSystemUsed1 = 0.00D;
        Double fileSystemTotal1 = 0.00D;
        Double cpuPercent2 = 0.00D;
        Integer cpuNum2 = 0;
        Double memoryUsed2 = 0.00D;
        Double memoryTotal2 = 0.00D;
        Double fileSystemUsed2 = 0.00D;
        Double fileSystemTotal2 = 0.00D;
        Double cpuPercent3 = 0.00D;
        Integer cpuNum3 = 0;
        Double memoryUsed3 = 0.00D;
        Double memoryTotal3 = 0.00D;
        Double fileSystemUsed3 = 0.00D;
        Double fileSystemTotal3 = 0.00D;
        if (CollectionUtils.isNotEmpty(result)) {
            for (XdPhyVO temp : result) {
                if ("1".equals(temp.getCompany())) {
                    if (StringUtils.isNotEmpty(temp.getCpuPercent())) {
                        cpuPercent1 += Double.parseDouble(temp.getCpuPercent().replace("%", ""));
                        cpuNum1++;
                    }
                    if (temp.getUsedMemory() != null && StringUtils.isNotEmpty(temp.getUsedMemory().getValue())) {
                        memoryUsed1 += Double.parseDouble(temp.getUsedMemory().getValue());
                    }
                    if (temp.getTotalMemory() != null && StringUtils.isNotEmpty(temp.getTotalMemory().getValue())) {
                        memoryTotal1 += Double.parseDouble(temp.getTotalMemory().getValue());
                    }
                    if (CollectionUtils.isNotEmpty(temp.getFileSystems())) {
                        for (XdPhyFileSystem xdPhyFileSystem : temp.getFileSystems()) {
                            if (xdPhyFileSystem.getUsedFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getUsedFileSystem().getValue())) {
                                fileSystemUsed1 += Double.parseDouble(xdPhyFileSystem.getUsedFileSystem().getValue());
                            }
                            if (xdPhyFileSystem.getTotalFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getTotalFileSystem().getValue())) {
                                fileSystemTotal1 += Double.parseDouble(xdPhyFileSystem.getTotalFileSystem().getValue());
                            }
                        }
                    }
                } else if ("2".equals(temp.getCompany())) {
                    if (StringUtils.isNotEmpty(temp.getCpuPercent())) {
                        cpuPercent2 += Double.parseDouble(temp.getCpuPercent().replace("%", ""));
                        cpuNum2++;
                    }
                    if (temp.getUsedMemory() != null && StringUtils.isNotEmpty(temp.getUsedMemory().getValue())) {
                        memoryUsed2 += Double.parseDouble(temp.getUsedMemory().getValue());
                    }
                    if (temp.getTotalMemory() != null && StringUtils.isNotEmpty(temp.getTotalMemory().getValue())) {
                        memoryTotal2 += Double.parseDouble(temp.getTotalMemory().getValue());
                    }
                    if (CollectionUtils.isNotEmpty(temp.getFileSystems())) {
                        for (XdPhyFileSystem xdPhyFileSystem : temp.getFileSystems()) {
                            if (xdPhyFileSystem.getUsedFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getUsedFileSystem().getValue())) {
                                fileSystemUsed2 += Double.parseDouble(xdPhyFileSystem.getUsedFileSystem().getValue());
                            }
                            if (xdPhyFileSystem.getTotalFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getTotalFileSystem().getValue())) {
                                fileSystemTotal2 += Double.parseDouble(xdPhyFileSystem.getTotalFileSystem().getValue());
                            }
                        }
                    }
                } else if ("3".equals(temp.getCompany())) {
                    if (StringUtils.isNotEmpty(temp.getCpuPercent())) {
                        cpuPercent3 += Double.parseDouble(temp.getCpuPercent().replace("%", ""));
                        cpuNum3++;
                    }
                    if (temp.getUsedMemory() != null && StringUtils.isNotEmpty(temp.getUsedMemory().getValue())) {
                        memoryUsed3 += Double.parseDouble(temp.getUsedMemory().getValue());
                    }
                    if (temp.getTotalMemory() != null && StringUtils.isNotEmpty(temp.getTotalMemory().getValue())) {
                        memoryTotal3 += Double.parseDouble(temp.getTotalMemory().getValue());
                    }
                    if (CollectionUtils.isNotEmpty(temp.getFileSystems())) {
                        for (XdPhyFileSystem xdPhyFileSystem : temp.getFileSystems()) {
                            if (xdPhyFileSystem.getUsedFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getUsedFileSystem().getValue())) {
                                fileSystemUsed3 += Double.parseDouble(xdPhyFileSystem.getUsedFileSystem().getValue());
                            }
                            if (xdPhyFileSystem.getTotalFileSystem() != null && StringUtils.isNotEmpty(xdPhyFileSystem.getTotalFileSystem().getValue())) {
                                fileSystemTotal3 += Double.parseDouble(xdPhyFileSystem.getTotalFileSystem().getValue());
                            }
                        }
                    }
                }
            }
        }
        if (cpuPercent1 > 0 && cpuNum1 > 0) {
            xdPhyStatistics.setCpuPercent1(Double.parseDouble(String.format("%.2f",cpuPercent1/cpuNum1)));
        }
        if (memoryTotal1 > 0) {
            xdPhyStatistics.setMemoryPercent1(Double.parseDouble(String.format("%.2f",memoryUsed1*100/memoryTotal1)));
        }
        if (fileSystemTotal1 > 0) {
            xdPhyStatistics.setFileSystemPercent1(Double.parseDouble(String.format("%.2f",fileSystemUsed1*100/fileSystemTotal1)));
        }

        if (cpuPercent2 > 0 && cpuNum2 > 0) {
            xdPhyStatistics.setCpuPercent2(Double.parseDouble(String.format("%.2f",cpuPercent2/cpuNum2)));
        }
        if (memoryTotal2 > 0) {
            xdPhyStatistics.setMemoryPercent2(Double.parseDouble(String.format("%.2f",memoryUsed2*100/memoryTotal2)));
        }
        if (fileSystemTotal2 > 0) {
            xdPhyStatistics.setFileSystemPercent2(Double.parseDouble(String.format("%.2f",fileSystemUsed2*100/fileSystemTotal2)));
        }

        if (cpuPercent3 > 0 && cpuNum3 > 0) {
            xdPhyStatistics.setCpuPercent3(Double.parseDouble(String.format("%.2f",cpuPercent3/cpuNum3)));
        }
        if (memoryTotal3 > 0) {
            xdPhyStatistics.setMemoryPercent3(Double.parseDouble(String.format("%.2f",memoryUsed3*100/memoryTotal3)));
        }
        if (fileSystemTotal3 > 0) {
            xdPhyStatistics.setFileSystemPercent3(Double.parseDouble(String.format("%.2f",fileSystemUsed3*100/fileSystemTotal3)));
        }

        return new ServiceResult<>(xdPhyStatistics).asResult();
    }


}
