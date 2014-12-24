import com.genchance.log.LogReasons;
import com.genchance.log.LogService;

/**
 * Created by i on 14/12/23.
 */
public class TestLog {
    public static void main(String[] args) {
        //服务器启动时初始化日志服务 需要配合log4j配置使用
        String gameCode = "pokersg";
        int regionID = 1;
        int serverID = 1;
        LogService.getInstance().init(gameCode, regionID, serverID);

        //基本参数
        String accountID = "test";  //账号的平台id(唯一)
        String accountName = "test@xxx.com"; //账号的平台用户名
        long charId = 123456789;  //角色id(唯一)
        String charName = "张三丰"; //角色名
        String rid = LogService.getInstance().getRid(); //一个逻辑处理产生多条日志时使用此方法先获取rid
        String loginType = "itools";  //登陆类型 平台定义的,游戏服务器在player session中保留player登陆方式

        //角色升级
        String param = "12";
        LogService.getInstance().sendPlayerLog(accountID,
                accountName,
                charId, charName,
                rid,
                loginType,
                LogReasons.PlayerLogReason.PL_LVL_UP,   //日志reason ：角色升级
                param,
                "", "", "", ""
        );

        //关联日志使用同一个rid
        rid = LogService.getInstance().getRid(); //一个逻辑处理产生多条日志时使用此方法先获取rid

        //商店购买物品 金币日志(金币是游戏内二级货币)
        param = "竞技场商店";
        LogService.getInstance().sendGoldLog(accountID,
                accountName,
                charId, charName,
                rid,
                loginType,
                LogReasons.GoldLogReason.GOLD_XH_SDGM,
                param,
                100,   //金币剩余100
                10  //金币消耗10
                );

        //商店购买物品 物品日志
        param = "竞技场商店";
        int itemId = 1001;
        LogService.getInstance().sendItemLog(accountID,
                accountName,
                charId, charName,
                rid,          //同一个rid
                loginType,
                LogReasons.ItemLogReason.ITEM_HD_SDGM,
                param,
                itemId,
                101,   //item剩余100
                1  //item获得1
        );
    }
}
