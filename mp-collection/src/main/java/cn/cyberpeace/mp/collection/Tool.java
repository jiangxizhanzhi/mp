package cn.cyberpeace.mp.collection;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

public class Tool {
    public static void main(String[] args) throws Exception {
        // 选手的ip
        File source = new File("/Users/maojun/dev/app/test/sourceIps");
        // 设备方的ip
        File target = new File("/Users/maojun/dev/app/test/targetIps");

        List<String> sourceIps = FileUtil.readLines(source, "utf8");
        List<String> targetIps = FileUtil.readLines(target, "utf8");
        // 造的数据里面的时间戳 , 表示这个攻击的时间
        // 注意 造数据的时候要看下索引， arkime_sessions3-221210 这个索引是按天生成的
        // 如果需要指定某种类型的流量，可以改 参数里面, suricata.category 的值 , 这个值的类型在 international_message 里面有
        long time = System.currentTimeMillis();
        for(int i = 0 ;i<1;i++){
            for (String sourceIp : sourceIps) {
                for (String targetIp : targetIps) {
                    var result = result(time, sourceIp.trim(), targetIp.trim());
                    HttpUtils.sendPost("http://10.10.100.7:9205/arkime_sessions3-221210/_doc", JSON.toJSONString(result));
                    System.out.println(String.format("send for sourceIp:%s , targetIp:%s success", sourceIp, targetIp));
                }
            }
        }
    }

    public static Object result(long time, String sourceIp, String targetIp) {
        JSONObject result = (JSONObject) JSON.parse(data());
        {
            result.put("@timestamp", time);
        }
        {
            JSONObject source = result.getJSONObject("source");
            source.put("ip", sourceIp);
        }
        {
            JSONObject target = result.getJSONObject("destination");
            target.put("ip", targetIp);
        }
        return result;
    }

    public static String data() {
        return "{\n" +
                       "    \"firstPacket\": 1670553145804,\n" +
                       "    \"lastPacket\": 1670553154978,\n" +
                       "    \"length\": 9174,\n" +
                       "    \"ipProtocol\": 1,\n" +
                       "    \"@timestamp\": 1670553165500,\n" +
                       "    \"source\": {\n" +
                       "        \"ip\": \"10.50.200.122\",\n" +
                       "        \"port\": 0,\n" +
                       "        \"bytes\": 930,\n" +
                       "        \"packets\": 6,\n" +
                       "        \"mac-cnt\": 1,\n" +
                       "        \"mac\": [\n" +
                       "            \"00:0c:29:f6:a6:59\"\n" +
                       "        ]\n" +
                       "    },\n" +
                       "    \"destination\": {\n" +
                       "        \"ip\": \"172.29.216.31\",\n" +
                       "        \"port\": 0,\n" +
                       "        \"bytes\": 930,\n" +
                       "        \"packets\": 6,\n" +
                       "        \"mac-cnt\": 1,\n" +
                       "        \"mac\": [\n" +
                       "            \"e8:ab:f3:af:24:ce\"\n" +
                       "        ]\n" +
                       "    },\n" +
                       "    \"dstRIR\": \"ARIN\",\n" +
                       "    \"network\": {\n" +
                       "        \"packets\": 12,\n" +
                       "        \"bytes\": 1860\n" +
                       "    },\n" +
                       "    \"client\": {\n" +
                       "        \"bytes\": 726\n" +
                       "    },\n" +
                       "    \"server\": {\n" +
                       "        \"bytes\": 726\n" +
                       "    },\n" +
                       "    \"totDataBytes\": 1452,\n" +
                       "    \"segmentCnt\": 1,\n" +
                       "    \"node\": \"localhost\",\n" +
                       "    \"packetPos\": [\n" +
                       "        -119,\n" +
                       "        827756112,\n" +
                       "        356,\n" +
                       "        3444,\n" +
                       "        416,\n" +
                       "        28377,\n" +
                       "        356,\n" +
                       "        5068,\n" +
                       "        416,\n" +
                       "        23783,\n" +
                       "        356,\n" +
                       "        8405,\n" +
                       "        152\n" +
                       "    ],\n" +
                       "    \"fileId\": [\n" +
                       "        119\n" +
                       "    ],\n" +
                       "    \"dstDscpCnt\": 1,\n" +
                       "    \"dstDscp\": [\n" +
                       "        1\n" +
                       "    ],\n" +
                       "    \"dstOuiCnt\": 1,\n" +
                       "    \"dstOui\": [\n" +
                       "        \"Huawei Technologies Co.,Ltd\"\n" +
                       "    ],\n" +
                       "    \"icmp\": {\n" +
                       "        \"code\": [\n" +
                       "            9,\n" +
                       "            0\n" +
                       "        ],\n" +
                       "        \"type\": [\n" +
                       "            0,\n" +
                       "            8\n" +
                       "        ]\n" +
                       "    },\n" +
                       "    \"protocolCnt\": 1,\n" +
                       "    \"protocol\": [\n" +
                       "        \"icmp\"\n" +
                       "    ],\n" +
                       "    \"srcDscpCnt\": 1,\n" +
                       "    \"srcDscp\": [\n" +
                       "        1\n" +
                       "    ],\n" +
                       "    \"srcOuiCnt\": 1,\n" +
                       "    \"srcOui\": [\n" +
                       "        \"VMware, Inc.\"\n" +
                       "    ],\n" +
                       "    \"suricata\": {\n" +
                       "        \"actionCnt\": 1,\n" +
                       "        \"action\": [\n" +
                       "            \"allowed\"\n" +
                       "        ],\n" +
                       "        \"categoryCnt\": 1,\n" +
                       "        \"category\": [\n" +
                       "            \"Generic Protocol Command Decode\"\n" +
                       "        ],\n" +
                       "        \"flowIdCnt\": 1,\n" +
                       "        \"flowId\": [\n" +
                       "            \"1056504447316917\"\n" +
                       "        ],\n" +
                       "        \"gidCnt\": 1,\n" +
                       "        \"gid\": [\n" +
                       "            1\n" +
                       "        ],\n" +
                       "        \"severityCnt\": 1,\n" +
                       "        \"severity\": [\n" +
                       "            3\n" +
                       "        ],\n" +
                       "        \"signatureCnt\": 1,\n" +
                       "        \"signature\": [\n" +
                       "            \"SURICATA ICMPv4 unknown code\"\n" +
                       "        ],\n" +
                       "        \"signatureIdCnt\": 1,\n" +
                       "        \"signatureId\": [\n" +
                       "            2200025\n" +
                       "        ]\n" +
                       "    }\n" +
                       "}";
    }
}
