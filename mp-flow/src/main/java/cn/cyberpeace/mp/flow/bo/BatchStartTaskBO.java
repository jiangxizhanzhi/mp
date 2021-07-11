package cn.cyberpeace.mp.flow.bo;

import lombok.Data;

import java.util.List;


@Data
public class BatchStartTaskBO {

    
    private List<TaskSipBO> taskSipList;


    
    private int runType;

}
