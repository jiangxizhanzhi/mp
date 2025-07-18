package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class MockData {

    @Getter
    @Setter
    public static class Params {
        private String index;
        private String sourceIp;
        private Integer sourcePort;
        private String destIp;
        private Integer destPort;
        private String category;
    }


    public static void createData(Params params) {
        // 隔8秒之后创建数据
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> res = getData(params);
            RestHighLevelClient levelClient = (RestHighLevelClient) BeanCommon.getBean("wazuhRestHighLevelClient");
            IndexRequest request = new IndexRequest(params.getIndex()).source(JsonCommon.stringify(res), XContentType.JSON);
            try {
                levelClient.index(request, RequestOptions.DEFAULT);

                // 刷新下缓存
                RefreshRequest refreshRequest = new RefreshRequest();
                refreshRequest.indices(params.getIndex());
                refreshRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
                levelClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("failed to exec create data", e);
            }
        }).start();
    }

    public static Map<String, Object> getData(Params params) {
        Map<String, Object> template = JsonCommon.parseMap(template());
        Map<String, Object> source = (Map<String, Object>) template.get("source");
        source.put("ip", params.getSourceIp());
        source.put("port", params.getSourcePort());
        Map<String, Object> dest = (Map<String, Object>) template.get("destination");
        dest.put("ip", params.getDestIp());
        dest.put("port", params.getDestPort());
        Map<String, Object> suricata = (Map<String, Object>) template.get("suricata");
        suricata.put("category", Arrays.asList(params.getCategory()));
        template.put("@timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return template;
    }

    public static String template() {
        return "{\n" + "    \"firstPacket\": 1752630624295,\n" + "    \"lastPacket\": 1752630624295,\n" + "    \"length\": 0,\n" + "    \"ipProtocol\": 6,\n" + "    \"tcpflags\": {\n" + "        \"syn\": 1,\n" + "        \"syn-ack\": 0,\n" + "        \"ack\": 0,\n" + "        \"psh\": 0,\n" + "        \"fin\": 0,\n" + "        \"rst\": 1,\n" + "        \"urg\": 0,\n" + "        \"srcZero\": 0,\n" + "        \"dstZero\": 0\n" + "    },\n" + "    \"@timestamp\": 1752630633532,\n" + "    \"source\": {\n" + "        \"ip\": \"192.168.1.130\",\n" + "        \"port\": 38151,\n" + "        \"bytes\": 58,\n" + "        \"packets\": 1,\n" + "        \"mac-cnt\": 1,\n" + "        \"mac\": [\n" + "            \"fa:16:3e:2d:9d:b9\"\n" + "        ]\n" + "    },\n" + "    \"destination\": {\n" + "        \"ip\": \"192.168.1.232\",\n" + "        \"port\": 5432,\n" + "        \"bytes\": 54,\n" + "        \"packets\": 1,\n" + "        \"mac-cnt\": 1,\n" + "        \"mac\": [\n" + "            \"fa:16:3e:10:62:f6\"\n" + "        ]\n" + "    },\n" + "    \"srcRIR\": \"ARIN\",\n" + "    \"dstRIR\": \"ARIN\",\n" + "    \"network\": {\n" + "        \"packets\": 2,\n" + "        \"bytes\": 112,\n" + "        \"community_id\": \"1:P9/yHTYZgTcYK6YYL42uVnDlsS8=\"\n" + "    },\n" + "    \"client\": {\n" + "        \"bytes\": 0\n" + "    },\n" + "    \"server\": {\n" + "        \"bytes\": 0\n" + "    },\n" + "    \"totDataBytes\": 0,\n" + "    \"segmentCnt\": 1,\n" + "    \"node\": \"2b24660e09be\",\n" + "    \"packetPos\": [\n" + "        -106,\n" + "        14929901,\n" + "        74\n" + "    ],\n" + "    \"fileId\": [\n" + "        106\n" + "    ],\n" + "    \"protocolCnt\": 1,\n" + "    \"protocol\": [\n" + "        \"tcp\"\n" + "    ],\n" + "    \"suricata\": {\n" + "        \"actionCnt\": 1,\n" + "        \"action\": [\n" + "            \"allowed\"\n" + "        ],\n" + "        \"categoryCnt\": 1,\n" + "        \"category\": [\n" + "            \"Potentially Bad Traffic\"\n" + "        ],\n" + "        \"flowIdCnt\": 1,\n" + "        \"flowId\": [\n" + "            \"1511076959126149\"\n" + "        ],\n" + "        \"gidCnt\": 1,\n" + "        \"gid\": [\n" + "            1\n" + "        ],\n" + "        \"severityCnt\": 1,\n" + "        \"severity\": [\n" + "            2\n" + "        ],\n" + "        \"signatureCnt\": 1,\n" + "        \"signature\": [\n" + "            \"ET SCAN Suspicious inbound to PostgreSQL port 5432\"\n" + "        ],\n" + "        \"signatureIdCnt\": 1,\n" + "        \"signatureId\": [\n" + "            2010939\n" + "        ]\n" + "    }\n" + "}";
    }
}
