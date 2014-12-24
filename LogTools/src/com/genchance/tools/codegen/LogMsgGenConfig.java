package com.genchance.tools.codegen;

import com.genchance.tools.core.CoreErrors;
import com.genchance.tools.core.ErrorsUtil;
import com.genchance.tools.core.config.ConfigSupport;

/**
 * 日志消息自动生成的配置文�?
 * 
 *
 */
public class LogMsgGenConfig extends ConfigSupport{
	/** 导出xml, XXXLog.java文件对应logserver的包�?*/
	private String packageName;
	
	/** 导出MessageType.java对应logserver的包�?*/
	private String logServerDir;
	
	/** 导出LZRLogService.java文件对应gameserver的包�?*/
	private String logServiceDir;
	
	/** 导出logs相关文件存放的根目录 */
	private String logSrcGenDir;
	
	/** 导出logs ibatis配置文件片段存放的根目录 */
	private String logResGenDir;
	
	/** 导出LZRLogService.java文件存放的根目录 */
	private String gsGenDir;
	
	/** 自动导出消息文件的配置文�?*/
	private String logConfig;
	
	/** 消息模板目录 */
	private String msgDir;
	
	/** 包名  */
	private String packageNameSub;
	
	/** 配置文件  */
	private String excelPath;
	
	/** LogReasons.java对应logserver的包�?*/
	private String reasonsDir;
	
	/** 导出LogReasons.java相关文件存放的根目录 */
	private String reasonLogSrcGenDir;
	
	/** 导出LogReasons.java相关文件存放的根目录 */
	private String gmGenSrcDir;
	
	/** 导出LogReasons.java相关文件存放的根目录 */
	private String gmGenJspDir;
	
	private String gmbeanDir;
	
	private String gmJspDir;
	private boolean gm;
	
	private String gameCode;
	
	private String gameName;
	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setLogServerDir(String logServerDir) {
		this.logServerDir = logServerDir;
	}
	
	public String getLogServerDir() {
		return logServerDir;
	}
	
	public void setLogServiceDir(String logServiceDir) {
		this.logServiceDir = logServiceDir;
	}
	
	public String getLogServiceDir() {
		return logServiceDir;
	}
	
	public void setLogSrcGenDir(String logSrcGenDir) {
		this.logSrcGenDir = logSrcGenDir;
	}
	
	public String getLogSrcGenDir() {
		return logSrcGenDir;
	}
	
	public void setLogResGenDir(String logResGenDir) {
		this.logResGenDir = logResGenDir;
	}
	
	public String getLogResGenDir() {
		return logResGenDir;
	}
	
	public void setGsGenDir(String gsGenDir) {
		this.gsGenDir = gsGenDir;
	}
	public String getGsGenDir() {
		return gsGenDir;
	}
	
	public void setLogConfig(String logConfig) {
		this.logConfig = logConfig;
	}
	public String getLogConfig() {
		return logConfig;
	}
	
	public void setMsgDir(String msgDir) {
		this.msgDir = msgDir;
	}
	
	public String getMsgDir() {
		return msgDir;
	}
	
	@Override
	public void validate() {
		if (excelPath == null) {
			throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "logConfig"));
		}
	}

	public String getPackageNameSub() {
		return packageNameSub;
	}

	public void setPackageNameSub(String packageNameSub) {
		this.packageNameSub = packageNameSub;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getReasonsDir() {
		return reasonsDir;
	}

	public void setReasonsDir(String reasonsDir) {
		this.reasonsDir = reasonsDir;
	}

	public String getReasonLogSrcGenDir() {
		return reasonLogSrcGenDir;
	}

	public void setReasonLogSrcGenDir(String reasonLogSrcGenDir) {
		this.reasonLogSrcGenDir = reasonLogSrcGenDir;
	}

	public String getGmGenSrcDir() {
		return gmGenSrcDir;
	}

	public void setGmGenSrcDir(String gmGenSrcDir) {
		this.gmGenSrcDir = gmGenSrcDir;
	}

	public String getGmGenJspDir() {
		return gmGenJspDir;
	}

	public void setGmGenJspDir(String gmGenJspDir) {
		this.gmGenJspDir = gmGenJspDir;
	}

	public String getGmbeanDir() {
		return gmbeanDir;
	}

	public void setGmbeanDir(String gmbeanDir) {
		this.gmbeanDir = gmbeanDir;
	}

	public String getGmJspDir() {
		return gmJspDir;
	}

	public void setGmJspDir(String gmJspDir) {
		this.gmJspDir = gmJspDir;
	}

	public boolean isGm() {
		return gm;
	}

	public void setGm(boolean gm) {
		this.gm = gm;
	}

	public String getGameCode() {
		return gameCode;
	}

	public String getGameName() {
		return gameName;
	}
}
