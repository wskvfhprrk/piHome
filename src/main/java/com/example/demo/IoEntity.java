package com.example.demo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/3 8:59
 * 实体类
 */
@Data
public class IoEntity implements Serializable {
    /**
     * 输入open的红外信号(主键)
     */
    private String openInHex;
    /**
     * 输入close的红外信号(主键)
     */
    private String closeInHex;
    /**
     * 分组名称（房间）
     */
    private String groupName;
    /**
     * 开关名称
     */
    private String switchName;
    /**
     * 开关状态：true——关闭，false——断开
     */
    private Boolean status;
    /**
     * 转换输出open的射频信号
     */
    private String openOutHex;
    /**
     * 转换输出的close射频信号
     */
    private String closeOutHex;

    public IoEntity(String openInHex, String closeInHex, String groupName, String switchName, Boolean status, String openOutHex, String closeOutHex) {
        this.openInHex = openInHex;
        this.closeInHex = closeInHex;
        this.groupName = groupName;
        this.switchName = switchName;
        this.status = status;
        this.openOutHex = openOutHex;
        this.closeOutHex = closeOutHex;
    }

    public IoEntity() {
    }
}
