package com.example.vasclientv2.model.entities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * lớp user để lấy dữ liệu từ response message
 *
 *
 * @author  Hoang NM
 * @version 1.0
 * @since   2020-10-20
 */
public class UserModel implements Serializable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("passwordEnscrypt")
    @Expose
    private String passwordEnscrypt;
    @SerializedName("roldCode")
    @Expose
    private String roldCode;
    @SerializedName("createdTime")
    @Expose
    private String createdTime;
    @SerializedName("lastEditedTime")
    @Expose
    private String lastEditedTime;
    @SerializedName("actived")
    @Expose
    private Boolean actived;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;
    @SerializedName("groupUser")
    @Expose
    private String groupUser;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getGroupUser() {
        return groupUser;
    }

    public void setGroupUser(String groupUser) {
        this.groupUser = groupUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordEnscrypt() {
        return passwordEnscrypt;
    }

    public void setPasswordEnscrypt(String passwordEnscrypt) {
        this.passwordEnscrypt = passwordEnscrypt;
    }

    public String getRoldCode() {
        return roldCode;
    }

    public void setRoldCode(String roldCode) {
        this.roldCode = roldCode;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastEditedTime() {
        return lastEditedTime;
    }

    public void setLastEditedTime(String lastEditedTime) {
        this.lastEditedTime = lastEditedTime;
    }

    public Boolean getActived() {
        return actived;
    }

    public void setActived(Boolean actived) {
        this.actived = actived;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", passwordEnscrypt='" + passwordEnscrypt + '\'' +
                ", roldCode='" + roldCode + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", lastEditedTime='" + lastEditedTime + '\'' +
                ", actived=" + actived +
                '}';
    }
}
