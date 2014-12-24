package com.genchance.log.model;

import com.genchance.log.BaseLog;

/**
 * This is an auto generated source,please don't modify it.
 * 
 * @author com.genchance.tools.codegen.ModelLogMsgGenerator
 * @version 1.0.1
 *
 */
 
public class ItemLog extends BaseLog{
           private int itemId;
           private int currentVal;
           private int changeVal;
        
    public ItemLog() {    	
    }

    public ItemLog(
                                        String accountId,
                                                    String accountName,
                                                    long charId,
                                                    String charName,
                                                    String rid,
                                                    String loginType,
                                                    int reason,
                                                    String param,                                    			int itemId,						int currentVal,						int changeVal						) {
        super(accountId ,accountName ,charId ,charName ,rid ,loginType ,reason ,param );
                    this.itemId =  itemId;
                    this.currentVal =  currentVal;
                    this.changeVal =  changeVal;
            }

           public int getItemId() {
	       return itemId;
       }
           public int getCurrentVal() {
	       return currentVal;
       }
           public int getChangeVal() {
	       return changeVal;
       }
            
           public void setItemId(int itemId) {
	       this.itemId = itemId;
       }
           public void setCurrentVal(int currentVal) {
	       this.currentVal = currentVal;
       }
           public void setChangeVal(int changeVal) {
	       this.changeVal = changeVal;
       }
    
    public String getLogName() {
        return "item_log";
    }
}