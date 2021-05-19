package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.Application;
import cn.cyberpeace.mp.base.config.websocket.WebSocketConfig;
import cn.cyberpeace.mp.data.sandtable.model.GroupModel;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"cn.cyberpeace.mp"}, excludeFilters={
    @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=WebSocketConfig.class)})
public class GroupServiceTest {
    @Autowired
    private GroupService groupService;

    @Test
    public void testGroupServiceQuery() {
        List<GroupModel> list = groupService.queryAllGroups();
        Assert.isTrue(list.size() > 0, "123");
    }

    @Test
    public void testQueryGroupById() {
        GroupModel groupModel = groupService.queryGroupById("9787c63b-d79f-4644-8027-b268f89aa8a9");
        Assert.isTrue(groupModel.getGroupId().equals("9787c63b-d79f-4644-8027-b268f89aa8a9"), "is not equal");
    }
}
