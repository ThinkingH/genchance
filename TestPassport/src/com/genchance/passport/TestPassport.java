package com.genchance.passport;

import com.imop.platform.local.HandlerFactory;
import com.imop.platform.local.config.LocalConfig;
import com.imop.platform.local.exception.LocalException;
import com.imop.platform.local.handler.IHandler;
import com.imop.platform.local.response.LoginResponse;
import com.imop.platform.local.response.UseActivityCodeResponse;
import com.imop.platform.local.type.LoginType;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;

public class TestPassport {
    private static Logger log = org.apache.log4j.Logger.getLogger(TestPassport.class);

    public static void main(String[] args) {
        //passport初始化信息
        String gamecode = "pokersg"; //平台分配的gamecode
        String gameKey = "9fcb253028de4bdd3a5811e304583e67"; //平台分配的gameKey
        int regionId = 1;    //平台分配的服务器大区id   默认分配1
        int serverId = 1997; //平台分配的服务器id  默认开通3个 1999 ，1998 ，1997 测试服务器id
        String gameServerDomain = "domain"; //服务器domain不含http 申请服务器时候提供的信息，默认domain
        String passportDomain = "http://passport.genchance.com/passport/"; // 平台地址
        String reportDomain = "http://passport.genchance.com/passport/";  // 汇报地址（暂时没使用）

        LocalConfig lc = null;
        try {
            lc = new LocalConfig();
            lc.setGameKey(gameKey);
            lc.setAreaId(regionId);
            lc.setServerId(serverId);
            lc.setDomain(gameServerDomain);
            lc.setRequestDomain(passportDomain);
            lc.setReportDomain(reportDomain);
            lc.setGamecode(gamecode);

            IHandler localHandler = HandlerFactory.createHandler(lc);

            //登陆演示
            //游戏服务器和平台通讯一般都是使用cookie（token）方式，用户名密码方式是游戏客户端和平台通讯用的
            //loginType一般由客户端SDK返回
            LoginType loginType = LoginType.USERNAME_PWD; //不同SDK返回的信息内容不一样对应SDK返回的loginType字段
            String username = "test";  //不同SDK返回的信息内容不一样对应SDK返回的username字段
            String password = "123456";   //不同SDK返回的信息内容不一样对应SDK返回的password字段
            String ip = "8.8.8.8"; //需要获取玩家的最终IP
            LoginResponse loginResponse = localHandler.login(username, password, ip, loginType);

            // 检查登录是否成功
            if (loginResponse != null && !loginResponse.isSuccess()) {

                log.warn("[local service] local jar -- login fail: "
                        + loginResponse.getReturnCode()
                        + ". username:"
                        + username
                        + ". password:"
                        + password
                        + ". ip:"
                        + ip
                        + ". type:"
                        + loginType);
            }

            //推送通知
            String roleId = "12345678"; //游戏角色ID，推送按照角色可以直接推送，此ID在客户端setTags的时候会设置进去
            String title = "通知标题";
            String content = "通知内容";
            long timeToLive = 72; //过期时间 小时？
            localHandler.notifyWithRoleId(roleId, title, content, timeToLive, ip);

            //向平台请求验证媒体激活码
            long userId = 123456;
            roleId = String.valueOf(12345678); //roleId应该是long的
            String activityCode = "激活码"; //激活码，平台发放的
            UseActivityCodeResponse ur = localHandler.useActivityCode(userId, roleId, activityCode, ip);

            // 检查验证是否成功
            if (ur != null && !ur.isSuccess()) {

                log.warn("[local service] local jar -- useActivityCode by userId/roleId/activityCode fail: "
                        + ur.getReturnCode()
                        + ". userId:"
                        + userId
                        + ". roleId:"
                        + roleId
                        + ". activityCode:"
                        + activityCode
                        + ". ip:"
                        + ip);

            }


        } catch (LocalException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
