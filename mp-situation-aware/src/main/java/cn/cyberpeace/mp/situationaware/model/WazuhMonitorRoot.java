 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhMonitorRoot.java.
 // File size 5266.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class WazuhMonitorRoot<T> {

    /**
     * 代理信息，包含代理的IP、名称和ID。
     */
    private Agent agent;
    /**
     * 管理器信息，主要指Wazuh manager的名称。
     */
    private Manager manager;
    /**
     * 规则信息，包括规则的触发次数、是否发送邮件通知、级别、描述和所属组。
     */
    private Rule rule;
    /**
     * 解码器信息，主要指解码器的名称。
     */
    private Decoder decoder;
    /**
     * 事件的完整日志。
     */
    @JSONField(name = "full_log")
    private String fullLog;
    /**
     * 事件的输入类型。
     */
    private Input input;
    /**
     * 事件的时间戳。
     */
    private Date timestamp;
    /**
     * 事件的发生位置。
     */
    private String location;
    /**
     * 事件的唯一ID。
     */
    private String id;
    /**
     * 存储任意类型的数据，取决于具体使用场景。
     */
    private T data;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public String getFullLog() {
        return fullLog;
    }

    public void setFullLog(String fullLog) {
        this.fullLog = fullLog;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Input {

        private String type;

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }
    }

    public static class Decoder {

        private String name;

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }

    public static class Rule{
        private int firedtimes;
        private boolean mail;
        private int level;
        private String description;
        private List<String> groups;
        private String id;

        public int getFiredtimes() {
            return firedtimes;
        }

        public void setFiredtimes(int firedtimes) {
            this.firedtimes = firedtimes;
        }

        public boolean isMail() {
            return mail;
        }

        public void setMail(boolean mail) {
            this.mail = mail;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getGroups() {
            return groups;
        }

        public void setGroups(List<String> groups) {
            this.groups = groups;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Manager{
        private String name;

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }

    public static class Agent{
        private String ip;
        private String name;
        private String id;

        public void setIp(String ip) {
            this.ip = ip;
        }
        public String getIp() {
            return this.ip;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return this.id;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
