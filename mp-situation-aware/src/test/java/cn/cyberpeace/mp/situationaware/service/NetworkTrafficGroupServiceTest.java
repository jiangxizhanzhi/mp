package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.TestSituationAwareApplciation;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = TestSituationAwareApplciation.class)
@RunWith(SpringRunner.class)
public class NetworkTrafficGroupServiceTest {
    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

//    @Test
//    public void testGroupServiceQuery() {
//        List<GroupModel> list = networkTrafficGroupService.queryAllGroups();
//        Assert.isTrue(list.size() > 0, "123");
//    }
//
//    @Test
//    public void testQueryGroupById() {
//        GroupModel groupModel = networkTrafficGroupService.queryGroupById("9787c63b-d79f-4644-8027-b268f89aa8a9");
//        Assert.isTrue(groupModel.getGroupId().equals("9787c63b-d79f-4644-8027-b268f89aa8a9"), "is not equal");
//    }

    /**
     * 新增分组
     */
    @Test
    public void testdeleteByGroupById() {
        networkTrafficGroupService.deleteArkimeInstance("9787c63b-d79f-4644-8027-b268f89aa8a9");
    }


    /**
     * 新增分组
     * @param
     */
    @Test
    public void testAddGroup() {
        List<String> macIds = new ArrayList<>();
        macIds.add("fa:16:3e:00:00:02");
        macIds.add("fc:16:3e:00:00:02");
        GroupModel groupModel1 = GroupModel.builder().groupId("9787c63b-d79f-4644-8027-b268f89aa8a9").macIds(macIds).build();
        networkTrafficGroupService.addArkimeImageInstance(groupModel1);
    }

    @Value("${mp.situationaware.arkime.cpcloud-container-create-url}")
    private String cpCloudUrl;

    @Test
    public void testInit() {
        try {
            String result = HttpUtils.sendGet(cpCloudUrl + "/networks");
            JSONArray networkList = (JSONArray)JSONPath.eval(JSON.parse(result), "$.data.list");
            networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> json.get("name").equals("oj_ext")).findFirst().ifPresent(network ->{
                SituationalAwareConstant.ArkimeNetworkId = (String) network.get("id");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateArkimeImageInstance() {
        String arkimePrefixId = "mp_" + String.valueOf(System.currentTimeMillis());
        networkTrafficGroupService.createArkimeImageInstance(arkimePrefixId);
    }

    @Test
    public void testDeleteArkimeInstance() {
        String instanceName = "mp_1637285914623";
        networkTrafficGroupService.deleteArkimeInstance(instanceName);
    }

    @Test
    public void testDoBindingNetworkTrafficMirror() {
        NetworkTrafficMirrorRequest networkTrafficMirrorRequest = new NetworkTrafficMirrorRequest();
        networkTrafficMirrorRequest.setVmInstanceIds(Lists.newArrayList("a42454fb-e1cf-4375-847c-84ca8d027c69"));
        networkTrafficMirrorRequest.setArkimeInstanceNames(Lists.newArrayList("mp_1637131359990"));
        networkTrafficGroupService.doBindingNetworkTrafficMirror(networkTrafficMirrorRequest);
    }

    @Test
    public void testDeleteNetworkTrafficMirror() {
        NetworkTrafficMirrorRequest networkTrafficMirrorRequest = new NetworkTrafficMirrorRequest();
        networkTrafficMirrorRequest.setVmInstanceIds(Lists.newArrayList("a42454fb-e1cf-4375-847c-84ca8d027c69"));
        networkTrafficMirrorRequest.setArkimeInstanceNames(Lists.newArrayList("mp_1637131359990"));
        networkTrafficGroupService.deleteNetworkTrafficMirror(networkTrafficMirrorRequest);
    }
}
