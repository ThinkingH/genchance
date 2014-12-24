package com.genchance.log.model;

import com.genchance.log.BaseLog;

/**
 * This is an auto generated source,please don't modify it.
 * 
 * @author com.genchance.tools.codegen.ModelLogMsgGenerator
 * @version 1.0.1
 *
 */
 
public class GoldLog extends BaseLog{
           private long currentVal;
           private long changeVal;
        
    public GoldLog() {    	
    }

    public GoldLog(
                                        String accountId,
                                                    String accountName,
                                                    long charId,
                                                    String charName,
                                                    String rid,
                                                    String loginType,
                                                    int reason,
                                                    String param,                                    			long currentVal,						long changeVal						) {
        super(accountId ,accountName ,charId ,charName ,rid ,loginType ,reason ,param );
                    this.currentVal =  currentVal;
                    this.changeVal =  changeVal;
            }

           public long getCurrentVal() {
	       return currentVal;
       }
           public long getChangeVal() {
	       return changeVal;
       }
            
           public void setCurrentVal(long currentVal) {
	       this.currentVal = currentVal;
       }
           public void setChangeVal(long changeVal) {
	       this.changeVal = changeVal;
       }
    
    public String getLogName() {
        return "gold_log";
    }
}