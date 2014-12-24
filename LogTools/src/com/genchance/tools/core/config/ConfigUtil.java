package com.genchance.tools.core.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.genchance.tools.core.CoreErrors;
import com.genchance.tools.core.ErrorsUtil;
import com.genchance.tools.core.script.IScriptEngine;
import com.genchance.tools.core.script.impl.JSScriptManagerImpl;
import com.genchance.tools.core.util.StringUtils;

/**
 * 配置相关的工具类
 * 
 *
 */
public class ConfigUtil {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigUtil.class);

	/**
	 * 根据指定的配置类型<tt>configClass</tt>从<tt>configURL</tt>中加载配置
	 * 
	 * @param <T>
	 * @param configClass
	 *            配置的类型
	 * @param configURL
	 *            配置文件的URL,文件内容是一个以JavaScript编写的配置脚本
	 * @return 从configURL加载的配置对象
	 * @exception RuntimeException
	 *                从configClass构造对象失败时抛出此异常
	 * @exception IllegalArgumentException
	 *                配置验证失败时抛出此异常
	 * @exception IllegalStateException
	 *                从configUrl中加载内容失败时抛出此异常
	 */
	public static <T extends IConfig> T buildConfig(Class<T> configClass, URL configURL) {
		if (configClass == null) {
			throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "configClass"));
		}
		if (configURL == null) {
			throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "configURL"));
		}
		if(logger.isInfoEnabled()){
			logger.info("Load config ["+configClass+"] from ["+configURL+"]");
		}
		T _config = null;
		try {
			_config = configClass.newInstance();
		} catch (InstantiationException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e1) {
			throw new RuntimeException(e1);
		}
		IScriptEngine _jsEngine = new JSScriptManagerImpl("UTF-8");
		Map<String, Object> _bindings = new HashMap<String, Object>();
		_bindings.put("config", _config);
		Reader _r = null;
		String _scriptContent = null;
		try {
			_r = new InputStreamReader(configURL.openStream(), "UTF-8");
			_scriptContent = IOUtils.toString(_r);
		} catch (IOException e) {
			throw new IllegalStateException("Can't load config from url [" + configURL + "]");
		} finally {
			IOUtils.closeQuietly(_r);
		}
		_jsEngine.runScript(_bindings, _scriptContent);
		_config.validate();
		return _config;
	}
	
	/**
	 * 修改配置对象属性
	 * @param msg
	 * @param object
	 */
	public static void modifyProperties(String msg, Object object) {
		JSONObject json = JSONObject.fromObject(msg);
		System.out.println(json.toString());
		Set<String> set = json.keySet();
		for (String key : set) {
			String fieldObj = json.get(key).toString();
			if(fieldObj == null){
				return;
			}
			Method[] mm = object.getClass().getMethods();
			for (int i = 0; i < mm.length; i++) {
				String methodName = mm[i].getName();
				if (methodName.indexOf("set") == 0) {
					String a = methodName.substring(3, methodName.length());
					a = StringUtils.firstLower(a);
					if (a.equals(key)) {
						Class<?>[] classs = mm[i].getParameterTypes();
						if (classs.length == 1) {
							Object obj = null;
							if (classs[0].equals(String.class)) {
								obj = StringUtils.getStringValue(fieldObj);
							} else if (classs[0].equals(Short.class)) {
								obj = StringUtils.getIntValue(fieldObj);
							} else if (classs[0].equals(Integer.class)) {
								obj = StringUtils.getIntValue(fieldObj);
							} else if (classs[0].equals(Long.class)) {
								obj = StringUtils.getLongValue(fieldObj);
							} else if (classs[0].equals(Float.class)) {
								obj = StringUtils.getFloatValue(fieldObj);
							} else if (classs[0].equals(Double.class)) {
								obj = StringUtils.getFloatValue(fieldObj);
							} else if (classs[0].equals(Boolean.class)) {
								obj = StringUtils.getBooleanValue(fieldObj);
							} else if (classs[0].isEnum()) {
								obj = StringUtils.getStringValue(fieldObj);
								Class c = classs[0];
								obj = Enum.valueOf(c, fieldObj);
							} else {
								System.out.println(key);
							}
							if (obj != null) {
								try {
									mm[i].invoke(object, obj);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

}
