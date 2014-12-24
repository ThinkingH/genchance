package com.genchance.log.model;

import com.genchance.log.BaseLog;

/**
 * This is an auto generated source,please don't modify it.
 * 
 * @author com.genchance.tools.codegen.ModelLogMsgGenerator
 * @version 1.0.1
 *
 */
 
public class PlayerLog extends BaseLog{
           private String deviceId;
           private String deviceType;
           private String deviceConnectType;
           private String deviceVersion;
        
    public PlayerLog() {    	
    }

    public PlayerLog(
                                        String accountId,
                                                    String accountName,
                                                    long charId,
                                                    String charName,
                                                    String rid,
                                                    String loginType,
                                                    int reason,
                                                    String param,                                    			String deviceId,						String deviceType,						String deviceConnectType,						String deviceVersion						) {
        super(accountId ,accountName ,charId ,charName ,rid ,loginType ,reason ,param );
                    this.deviceId =  deviceId;
                    this.deviceType =  deviceType;
                    this.deviceConnectType =  deviceConnectType;
                    this.deviceVersion =  deviceVersion;
            }

           public String getDeviceId() {
	       return deviceId;
       }
           public String getDeviceType() {
	       return deviceType;
       }
           public String getDeviceConnectType() {
	       return deviceConnectType;
       }
           public String getDeviceVersion() {
	       return deviceVersion;
       }
            
           public void setDeviceId(String deviceId) {
	       this.deviceId = deviceId;
       }
           public void setDeviceType(String deviceType) {
	       this.deviceType = deviceType;
       }
           public void setDeviceConnectType(String deviceConnectType) {
	       this.deviceConnectType = deviceConnectType;
       }
           public void setDeviceVersion(String deviceVersion) {
	       this.deviceVersion = deviceVersion;
       }
    
    public String getLogName() {
        return "player_log";
    }
}