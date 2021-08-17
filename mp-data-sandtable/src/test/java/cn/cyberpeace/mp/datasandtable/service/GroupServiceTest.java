package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.GroupModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest(classes = TestDatasandtableApplciation.class)
@RunWith(SpringRunner.class)
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

    /**
     * 新增分组
     */
    @Test
    public void testdeleteByGroupById() {
        groupService.deleteByGroupId("9787c63b-d79f-4644-8027-b268f89aa8a9");
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
        groupService.addGroup(groupModel1);
    }
}
