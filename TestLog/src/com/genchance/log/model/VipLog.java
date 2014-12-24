package com.genchance.log.model;

import com.genchance.log.BaseLog;

/**
 * This is an auto generated source,please don't modify it.
 * 
 * @author com.genchance.tools.codegen.ModelLogMsgGenerator
 * @version 1.0.1
 *
 */
 
public class VipLog extends BaseLog{
           private int currentVal;
           private int changeVal;
        
    public VipLog() {    	
    }

    public VipLog(
                                        String accountId,
                                                    String accountName,
                                                    long charId,
                                                    String charName,
                                                    String rid,
                                                    String loginType,
                                                    int reason,
                                                    String param,                                    			int currentVal,						int changeVal						) {
        super(accountId ,accountName ,charId ,charName ,rid ,loginType ,reason ,param );
                    this.currentVal =  currentVal;
                    this.changeVal =  changeVal;
            }

           public int getCurrentVal() {
	       return currentVal;
       }
           public int getChangeVal() {
	       return changeVal;
       }
            
           public void setCurrentVal(int currentVal) {
	       this.currentVal = currentVal;
       }
           public void setChangeVal(int changeVal) {
	       this.changeVal = changeVal;
       }
    
    public String getLogName() {
        return "vip_log";
    }
}