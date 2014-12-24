package com.genchance.tools.core.config;

/**
 * {@link IConfig}的简单实现
 * 
 *
 */
public abstract class ConfigSupport implements IConfig {
	/** 生产模式:0 调式模式:1 */
	private int debug = 0;
	/** 系统配置的版本号 */
	private String version;

	/**
	 * 判断是否是调式模式
	 * 
	 * @return
	 */

	@Override
	public boolean isDebug() {
		return debug == 1;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	/**
	 * @return the debug
	 */
	public int getDebug() {
		return debug;
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(int debug) {
		this.debug = debug;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
