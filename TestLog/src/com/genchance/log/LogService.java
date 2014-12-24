package com.genchance.log;

import com.genchance.log.model.PlayerLog;
import com.genchance.log.model.GoldLog;
import com.genchance.log.model.StoneLog;
import com.genchance.log.model.ItemLog;
import com.genchance.log.model.VipLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.genchance.log.LogReasons;

/**
 * 
 * 文件名：LogService
 * <p>
 * 功能：LogService
 * <p>
 * 版本：1.0.0
 * <p>
 * 日期：2011-04-05
 * <p>
 * 作者：XXX
 * <p>
 * 版权：(c)XX游戏
 */
public class LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    
    /**  游戏编码 */
    private String gameCode;
    /**  游戏区ID */
    private int regionID;
    /** 游戏服务器ID */
    private int serverID;

    private static LogService logService = null;

    public static LogService getInstance() {
        if (logService == null) {
            logService = new LogService();
        }
        return logService;
    }
    /**
     * 初始化
     *
     * @param gameCode
     *            gameCode
     * @param regionID
     *            游戏区ID
     * @param serverID
     *            游戏服务器ID
     */
    public void init(String gameCode, int regionID, int serverID) {
        if (logService == null) {
            logService.setGameCode(gameCode);
            logService.setRegionID(regionID);
            serverID = regionID * 1000 + serverID;
            logService.setServerID(serverID);

            if (logger.isInfoEnabled()) {
                logger.info("init Log service gameCode: " + gameCode + " regionID: " + regionID + " serverID: " + serverID);
            }
        } else {
            logger.error("init Log Service faild!");
            System.exit(0);
        }

    }

        /**
     * 发送角色日志表
               * @param accountId 用户Id
                    * @param accountName 用户名
                    * @param charId 角色Id
                    * @param charName 角色名
                    * @param rid 关联ID=是什么？
                    * @param loginType 登录方式
                    * @param reason 变化原因
                    * @param param 具体原因的参数(登陆记录角色IP)
                    * @param deviceId 设备id
          * @param deviceType 设备类型
          * @param deviceConnectType 网络环境
          * @param deviceVersion 操作系统版本
          */
    public void sendPlayerLog(String accountId,                                
                                                String accountName,                                
                                                long charId,
                                                String charName,
                                                String rid
            ,          String loginType
            ,          LogReasons.PlayerLogReason reason
            ,                 String param
                          , String deviceId , String deviceType , String deviceConnectType , String deviceVersion ) {
        try {

            PlayerLog log = new PlayerLog(
                                                accountId,                                
                                                accountName,                                
                                                charId,                                
                                                charName,                                
                                                rid,                                
                                                loginType,                                
                                                reason.reason,                                
                                                param                                
                                        ,deviceId
                            ,deviceType
                            ,deviceConnectType
                            ,deviceVersion
                        );
            
            logGameInfo(log);
        } catch (Exception e) {
            logger.error("log error:", e);
        }
    }
        /**
     * 发送金币日志表
               * @param accountId 用户Id
                    * @param accountName 用户名
                    * @param charId 角色Id
                    * @param charName 角色名
                    * @param rid 关联ID=是什么？
                    * @param loginType 登录方式
                    * @param reason 变化原因
                    * @param param 具体原因的参数(登陆记录角色IP)
                    * @param currentVal 当前值
          * @param changeVal 变化值
          */
    public void sendGoldLog(String accountId,                                
                                                String accountName,                                
                                                long charId,
                                                String charName,
                                                String rid
            ,          String loginType
            ,          LogReasons.GoldLogReason reason
            ,                 String param
                          , long currentVal , long changeVal ) {
        try {

            GoldLog log = new GoldLog(
                                                accountId,                                
                                                accountName,                                
                                                charId,                                
                                                charName,                                
                                                rid,                                
                                                loginType,                                
                                                reason.reason,                                
                                                param                                
                                        ,currentVal
                            ,changeVal
                        );
            
            logGameInfo(log);
        } catch (Exception e) {
            logger.error("log error:", e);
        }
    }
        /**
     * 发送钻石日志表
               * @param accountId 用户Id
                    * @param accountName 用户名
                    * @param charId 角色Id
                    * @param charName 角色名
                    * @param rid 关联ID=是什么？
                    * @param loginType 登录方式
                    * @param reason 变化原因
                    * @param param 具体原因的参数(登陆记录角色IP)
                    * @param currentVal 当前值
          * @param changeVal 变化值
          */
    public void sendStoneLog(String accountId,                                
                                                String accountName,                                
                                                long charId,
                                                String charName,
                                                String rid
            ,          String loginType
            ,          LogReasons.StoneLogReason reason
            ,                 String param
                          , long currentVal , long changeVal ) {
        try {

            StoneLog log = new StoneLog(
                                                accountId,                                
                                                accountName,                                
                                                charId,                                
                                                charName,                                
                                                rid,                                
                                                loginType,                                
                                                reason.reason,                                
                                                param                                
                                        ,currentVal
                            ,changeVal
                        );
            
            logGameInfo(log);
        } catch (Exception e) {
            logger.error("log error:", e);
        }
    }
        /**
     * 发送道具日志表
               * @param accountId 用户Id
                    * @param accountName 用户名
                    * @param charId 角色Id
                    * @param charName 角色名
                    * @param rid 关联ID=是什么？
                    * @param loginType 登录方式
                    * @param reason 变化原因
                    * @param param 具体原因的参数(登陆记录角色IP)
                    * @param itemId 道具id
          * @param currentVal 当前值
          * @param changeVal 变化值
          */
    public void sendItemLog(String accountId,                                
                                                String accountName,                                
                                                long charId,
                                                String charName,
                                                String rid
            ,          String loginType
            ,          LogReasons.ItemLogReason reason
            ,                 String param
                          , int itemId , int currentVal , int changeVal ) {
        try {

            ItemLog log = new ItemLog(
                                                accountId,                                
                                                accountName,                                
                                                charId,                                
                                                charName,                                
                                                rid,                                
                                                loginType,                                
                                                reason.reason,                                
                                                param                                
                                        ,itemId
                            ,currentVal
                            ,changeVal
                        );
            
            logGameInfo(log);
        } catch (Exception e) {
            logger.error("log error:", e);
        }
    }
        /**
     * 发送vip日志表
               * @param accountId 用户Id
                    * @param accountName 用户名
                    * @param charId 角色Id
                    * @param charName 角色名
                    * @param rid 关联ID=是什么？
                    * @param loginType 登录方式
                    * @param reason 变化原因
                    * @param param 具体原因的参数(登陆记录角色IP)
                    * @param currentVal 当前值
          * @param changeVal 变化值
          */
    public void sendVipLog(String accountId,                                
                                                String accountName,                                
                                                long charId,
                                                String charName,
                                                String rid
            ,          String loginType
            ,          LogReasons.VipLogReason reason
            ,                 String param
                          , int currentVal , int changeVal ) {
        try {

            VipLog log = new VipLog(
                                                accountId,                                
                                                accountName,                                
                                                charId,                                
                                                charName,                                
                                                rid,                                
                                                loginType,                                
                                                reason.reason,                                
                                                param                                
                                        ,currentVal
                            ,changeVal
                        );
            
            logGameInfo(log);
        } catch (Exception e) {
            logger.error("log error:", e);
        }
    }
        
    /**
     * 获得rid
     * 
     * @return rid
     */
    public String getRid() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获得gameCode
     *
     * @return gameCode
     */
    public String getGameCode() {
        return this.gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    /**
     * 获得regionID
     *
     * @return regionID
     */
    public int getRegionID() {
        return this.regionID;
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    /**
     * 获得serverID
     *
     * @return serverID
     */
    public int getServerID() {
        return this.serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    /**
     * 记录log4j日志
     * @param logBean logInfo           日志信息
     */
    public static void logGameInfo(BaseLog logBean){
        LoggerFactory.getLogger(logBean.getLogName()).warn(logBean.toString());
    }

}