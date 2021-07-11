package cn.cyberpeace.mp.devopscluster.thread;


import java.util.Map;


public interface ITask<R,T> {

    
    R execute(T e, Map<String, Object> params);
}
