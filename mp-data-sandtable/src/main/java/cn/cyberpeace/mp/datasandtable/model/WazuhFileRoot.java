package cn.cyberpeace.mp.datasandtable.model;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 文件json解析
 */
public class WazuhFileRoot {
    private Data data;
    private String message;
    private int error;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public static class Data{
        @JSONField(name = "affected_items")
        private List<FileItem> affectedItems;
        @JSONField(name = "total_affected_items")
        private int totalAffectedItems;
        @JSONField(name = "total_failed_items")
        private int totalFailedItems;
        @JSONField(name = "failed_items")
        private List<String> failedItems;

        public void setAffectedItems(List<FileItem> affectedItems) {
            this.affectedItems = affectedItems;
        }
        public List<FileItem> getAffectedItems() {
            return this.affectedItems;
        }
        public void setTotalAffectedItems(int totalAffectedItems) {
            this.totalAffectedItems = totalAffectedItems;
        }
        public int getTotalAffectedItems() {
            return this.totalAffectedItems;
        }
        public void setTotalFailedItems(int totalFailedItems) {
            this.totalFailedItems = totalFailedItems;
        }
        public int getTotalFailedItems() {
            return this.totalFailedItems;
        }
        public void setFailedItems(List<String> failedItems) {
            this.failedItems = failedItems;
        }
        public List<String> getFailedItems() {
            return this.failedItems;
        }
    }

    /**
     * 文件内容的实体
     */
    @lombok.Data
    public static class FileItem {
        private String arch;
        private Date gid;
        private String uname;
        private String file;
        private String perm;
        private String gname;
        private String uid;
        private Date date;
        private String type;
        private Date mtime;
        private int changes;
        private int inode;
        private String sha1;
        private int size;
        private String md5;
        private String sha256;
    }
}
