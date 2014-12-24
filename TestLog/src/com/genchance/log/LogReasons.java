/**
 * 
 */
package com.genchance.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志系统的日志原因定义
 * 
 * @author com.genchance.tools.codegen.ModelLogMsgGenerator
 * @version 1.00 
 * 
 */
public interface LogReasons {

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.FIELD, ElementType.TYPE })
	public @interface ReasonDesc {
		/**
		 * 原因的文字描述
		 * 
		 * @return
		 */
		String value();
	}

	/**
	 * LogReason的通用接口
	 */
	public static interface ILogReason<E extends Enum<E>> {
		/**
		 * 取得原因的序号
		 * 
		 * @return
		 */
		public int getReason();

		public E getReasonEnum();
		
		public E getEnumByReason(int reason);
	}
	

		
	/**
	 * 
	 * 角色日志表 的原因
	 * 
	 */
	public enum PlayerLogReason implements ILogReason<PlayerLogReason> {

				/** 角色创建 1*/
        @ReasonDesc("角色创建")
        PL_CJ(101),				/** 角色删除 2*/
        @ReasonDesc("角色删除")
        PL_SC(102),				/** 角色转移 3*/
        @ReasonDesc("角色转移")
        PL_ZY(103),				/** 登陆 4*/
        @ReasonDesc("登陆")
        PL_DL(104),				/** 登出 5*/
        @ReasonDesc("登出")
        PL_DC(105),				/** 角色创建自动起名 6*/
        @ReasonDesc("角色创建自动起名")
        PL_AUTO_NAME(106),				/** 角色创建开始 7*/
        @ReasonDesc("角色创建开始")
        PL_CJ_START(107),				/** 角色封停 8*/
        @ReasonDesc("角色封停")
        PL_STOP(108),				/** 角色禁言 9*/
        @ReasonDesc("角色禁言")
        PL_MUTE(109),				/** 登录设备(ios) 10*/
        @ReasonDesc("登录设备(ios)")
        PL_DEVICE_IOS(110),				/** 登录设备(ANDROID) 11*/
        @ReasonDesc("登录设备(ANDROID)")
        PL_DEVICE_ANDROID(111),				/** 登录设备信息 12*/
        @ReasonDesc("登录设备信息")
        PL_DEVICE_INFO(112),				/** 角色经验获得 13*/
        @ReasonDesc("角色经验获得")
        PL_EXP_GET(113),				/** 升级 14*/
        @ReasonDesc("升级")
        PL_LVL_UP(114);				
		
		
		/**
		 * 原因序号
		 */
		public final int reason;

		private PlayerLogReason(int reason) {
			this.reason = reason;
		}

		public PlayerLogReason getReasonEnum() {
			return this;
		}

		public int getReason() {
			return this.reason;
		}
		
        public PlayerLogReason getEnumByReason(int id) {
            for (PlayerLogReason eEnum : PlayerLogReason .values()) {
                if (eEnum.reason == id) {
                    return eEnum;
                }
            }
            return null;
        }
	}
    	
	/**
	 * 
	 * 道具日志表 的原因
	 * 
	 */
	public enum ItemLogReason implements ILogReason<ItemLogReason> {

				/** 道具获得_神秘魂匣 1*/
        @ReasonDesc("道具获得_神秘魂匣")
        ITEM_HD_SMHX(401),				/** 道具获得_黄金宝箱 2*/
        @ReasonDesc("道具获得_黄金宝箱")
        ITEM_HD_HJBX(402),				/** 道具获得_青铜宝箱 3*/
        @ReasonDesc("道具获得_青铜宝箱")
        ITEM_HD_QTBX(403),				/** 道具获得_邮箱 4*/
        @ReasonDesc("道具获得_邮箱")
        ITEM_HD_YX(404),				/** 道具获得_英雄转成灵魂石 5*/
        @ReasonDesc("道具获得_英雄转成灵魂石")
        ITEM_HD_HERO(405),				/** 道具获得_商店购买 6*/
        @ReasonDesc("道具获得_商店购买")
        ITEM_HD_SDGM(406),				/** 道具获得_合成 7*/
        @ReasonDesc("道具获得_合成")
        ITEM_HD_HC(407),				/** 道具获得_奖励 8*/
        @ReasonDesc("道具获得_奖励")
        ITEM_HD_JL(408),				/** 道具获得_英雄进阶附魔材料返回 9*/
        @ReasonDesc("道具获得_英雄进阶附魔材料返回")
        ITEM_HD_YXJJ(409),				/** 道具消耗_穿装备 10*/
        @ReasonDesc("道具消耗_穿装备")
        ITEM_XH_CZB(410),				/** 道具消耗_招募英雄 11*/
        @ReasonDesc("道具消耗_招募英雄")
        ITEM_XH_ZMYX(411),				/** 道具消耗_英雄升星 12*/
        @ReasonDesc("道具消耗_英雄升星")
        ITEM_XH_YXSJ(412),				/** 道具消耗_合成 13*/
        @ReasonDesc("道具消耗_合成")
        ITEM_XH_HC(413),				/** 道具消耗_扫荡 14*/
        @ReasonDesc("道具消耗_扫荡")
        ITEM_XH_SD(414),				/** 道具消耗_出售 15*/
        @ReasonDesc("道具消耗_出售")
        ITEM_XH_CS(415),				/** 道具消耗_使用经验丹 16*/
        @ReasonDesc("道具消耗_使用经验丹")
        ITEM_XH_SYJYD(416),				/** 道具消耗_附魔 17*/
        @ReasonDesc("道具消耗_附魔")
        ITEM_XH_FM(417);				
		
		
		/**
		 * 原因序号
		 */
		public final int reason;

		private ItemLogReason(int reason) {
			this.reason = reason;
		}

		public ItemLogReason getReasonEnum() {
			return this;
		}

		public int getReason() {
			return this.reason;
		}
		
        public ItemLogReason getEnumByReason(int id) {
            for (ItemLogReason eEnum : ItemLogReason .values()) {
                if (eEnum.reason == id) {
                    return eEnum;
                }
            }
            return null;
        }
	}
    	
	/**
	 * 
	 * 钻石日志表 的原因
	 * 
	 */
	public enum StoneLogReason implements ILogReason<StoneLogReason> {

				/** 钻石获得_邮件 1*/
        @ReasonDesc("钻石获得_邮件")
        STONE_HD_YJ(301),				/** 钻石获得_奖励 2*/
        @ReasonDesc("钻石获得_奖励")
        STONE_HD_JL(302),				/** 钻石消耗_竞技场重置挑战次数 3*/
        @ReasonDesc("钻石消耗_竞技场重置挑战次数")
        STONE_XH_ARENA_RESET(303),				/** 钻石消耗_精英关卡重置挑战次数 4*/
        @ReasonDesc("钻石消耗_精英关卡重置挑战次数")
        STONE_XH_ELITE_RESET(304),				/** 钻石消耗_购买体力 5*/
        @ReasonDesc("钻石消耗_购买体力")
        STONE_XH_GMTL(305),				/** 钻石消耗_商店购买 6*/
        @ReasonDesc("钻石消耗_商店购买")
        STONE_XH_SDGM(306),				/** 钻石消耗_竞技场秒cd 7*/
        @ReasonDesc("钻石消耗_竞技场秒cd")
        STONE_XH_ARENA_ENDCD(307),				/** 钻石消耗_黄金宝箱 8*/
        @ReasonDesc("钻石消耗_黄金宝箱")
        STONE_XH_HJBX(308),				/** 钻石消耗_聊天发言 9*/
        @ReasonDesc("钻石消耗_聊天发言")
        STONE_XH_LTFY(309),				/** 钻石消耗_商店刷新 10*/
        @ReasonDesc("钻石消耗_商店刷新")
        STONE_XH_SDSX(310),				/** 钻石消耗_英雄附魔 11*/
        @ReasonDesc("钻石消耗_英雄附魔")
        STONE_XH_YXFM(311),				/** 钻石消耗_神秘魂匣 12*/
        @ReasonDesc("钻石消耗_神秘魂匣")
        STONE_XH_SMHX(312),				/** 钻石消耗_恢复技能点 13*/
        @ReasonDesc("钻石消耗_恢复技能点")
        STONE_XH_HFJND(313),				/** 钻石消耗_换名 14*/
        @ReasonDesc("钻石消耗_换名")
        STONE_XH_HM(314),				/** 钻石消耗_天赋重置 15*/
        @ReasonDesc("钻石消耗_天赋重置")
        STONE_XH_TFCZ(315),				/** 钻石消耗_扫荡 16*/
        @ReasonDesc("钻石消耗_扫荡")
        STONE_XH_SD(316),				/** 钻石消耗_点金手 17*/
        @ReasonDesc("钻石消耗_点金手")
        STONE_XH_DJS(317);				
		
		
		/**
		 * 原因序号
		 */
		public final int reason;

		private StoneLogReason(int reason) {
			this.reason = reason;
		}

		public StoneLogReason getReasonEnum() {
			return this;
		}

		public int getReason() {
			return this.reason;
		}
		
        public StoneLogReason getEnumByReason(int id) {
            for (StoneLogReason eEnum : StoneLogReason .values()) {
                if (eEnum.reason == id) {
                    return eEnum;
                }
            }
            return null;
        }
	}
    	
	/**
	 * 
	 * vip日志表 的原因
	 * 
	 */
	public enum VipLogReason implements ILogReason<VipLogReason> {

				/** 获得vip积分GM 1*/
        @ReasonDesc("获得vip积分GM")
        VIP_EXP_GM(901),				/** 获得vip积分充值 2*/
        @ReasonDesc("获得vip积分充值")
        VIP_EXP_RECHARGE(902),				/** vip升级GM 3*/
        @ReasonDesc("vip升级GM")
        VIP_LV_UP_GM(903),				/** vip升级充值 4*/
        @ReasonDesc("vip升级充值")
        VIP_LV_UP_RECHARGE(904);				
		
		
		/**
		 * 原因序号
		 */
		public final int reason;

		private VipLogReason(int reason) {
			this.reason = reason;
		}

		public VipLogReason getReasonEnum() {
			return this;
		}

		public int getReason() {
			return this.reason;
		}
		
        public VipLogReason getEnumByReason(int id) {
            for (VipLogReason eEnum : VipLogReason .values()) {
                if (eEnum.reason == id) {
                    return eEnum;
                }
            }
            return null;
        }
	}
    	
	/**
	 * 
	 * 金币日志表 的原因
	 * 
	 */
	public enum GoldLogReason implements ILogReason<GoldLogReason> {

				/** 金币获得_点金手 1*/
        @ReasonDesc("金币获得_点金手")
        GOLD_HD_DJS(201),				/** 金币消耗_技能点 2*/
        @ReasonDesc("金币消耗_技能点")
        GOLD_XH_JND(202),				/** 金币获得_道具出售 3*/
        @ReasonDesc("金币获得_道具出售")
        GOLD_HD_DJCS(203),				/** 金币消耗_青铜宝箱 4*/
        @ReasonDesc("金币消耗_青铜宝箱")
        GOLD_XH_QTBX(204),				/** 金币消耗_商店购买 5*/
        @ReasonDesc("金币消耗_商店购买")
        GOLD_XH_SDGM(205),				/** 金币消耗_英雄升星 6*/
        @ReasonDesc("金币消耗_英雄升星")
        GOLD_XH_YXSX(206),				/** 金币消耗_招募英雄 7*/
        @ReasonDesc("金币消耗_招募英雄")
        GOLD_XH_ZMYX(207),				/** 金币获得_邮件 8*/
        @ReasonDesc("金币获得_邮件")
        GOLD_HD_YJ(208),				/** 金币获得_奖励 9*/
        @ReasonDesc("金币获得_奖励")
        GOLD_HD_JL(209);				
		
		
		/**
		 * 原因序号
		 */
		public final int reason;

		private GoldLogReason(int reason) {
			this.reason = reason;
		}

		public GoldLogReason getReasonEnum() {
			return this;
		}

		public int getReason() {
			return this.reason;
		}
		
        public GoldLogReason getEnumByReason(int id) {
            for (GoldLogReason eEnum : GoldLogReason .values()) {
                if (eEnum.reason == id) {
                    return eEnum;
                }
            }
            return null;
        }
	}
    }
