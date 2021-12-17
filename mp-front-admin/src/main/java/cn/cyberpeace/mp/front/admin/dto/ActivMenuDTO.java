package cn.cyberpeace.mp.front.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivMenuDTO {

    private String id;

    private String name;

    private String value;

    private String authority;

    private String path_name;

    private int status;

    private int read_status;

    private int write_status;

    private int execute_status;

    private String per_status;

    private List<String> active;

    private List<String> edition;

    List<ActivMenuDTO> children;

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    public String getAuthority() {
//        return authority;
//    }
//
//    public void setAuthority(String authority) {
//        this.authority = authority;
//    }
//
//    public String getPath_name() {
//        return path_name;
//    }
//
//    public void setPath_name(String path_name) {
//        this.path_name = path_name;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public int getRead_status() {
//        return read_status;
//    }
//
//    public void setRead_status(int read_status) {
//        this.read_status = read_status;
//    }
//
//    public int getWrite_status() {
//        return write_status;
//    }
//
//    public void setWrite_status(int write_status) {
//        this.write_status = write_status;
//    }
//
//    public int getExecute_status() {
//        return execute_status;
//    }
//
//    public void setExecute_status(int execute_status) {
//        this.execute_status = execute_status;
//    }
//
//    public String getPer_status() {
//        return per_status;
//    }
//
//    public void setPer_status(String per_status) {
//        this.per_status = per_status;
//    }
//
//    public List<ActivMenuDTO> getChildren() {
//        return children;
//    }
//
//    public void setChildren(List<ActivMenuDTO> children) {
//        this.children = children;
//    }
}
