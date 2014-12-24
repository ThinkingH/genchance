package com.genchance.tools.core.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class StringUtils {
	public static String trim(String str) {
		if (str == null) {
			str = "";
		} else {
			str = str.trim();
		}
		if (str.length() == 0) {
			return str;
		}

		if (str.charAt(0) == '"') {
			str = str.substring(1);
		}

		if (str.charAt(str.length() - 1) == '"') {
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}

	public static String[] getStringList(String str) {
		str = trim(str);
		if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}
		String sep = ",";
		if (str.indexOf(':') >= 0) {
			sep = ":";
		}
		return str.split(sep);
	}

	public static String[] getStringList(String str, String sep) {
		str = trim(str);
		return str.split(sep);
	}

	public static int[] getIntArray(String str, String sep) {
		String[] prop = getStringList(str, sep);
		List<Integer> tmp = new ArrayList<Integer>();
		for (int i = 0; i < prop.length; i++) {
			try {
				int r = Integer.parseInt(prop[i]);
				tmp.add(r);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		int[] ints = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); i++) {
			ints[i] = tmp.get(i);
		}
		return ints;
	}

	public static List<Integer> getIntList(String str, String sep) {
		String[] prop = getStringList(str, sep);
		List<Integer> tmp = new ArrayList<Integer>();
		for (int i = 0; i < prop.length; i++) {
			try {
				int r = Integer.parseInt(prop[i]);
				tmp.add(r);
			} catch (Exception e) {
			}
		}
		return tmp;
	}

	public static String join(String[] strs, String sep) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(strs[0]);
		for (int i = 1; i < strs.length; i++) {
			buffer.append(sep).append(strs[i]);
		}
		return buffer.toString();
	}

	public static double[] getDoubleList(String str) {
		String[] prop = getStringList(str);
		double[] ds = new double[prop.length];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = Double.parseDouble(prop[i]);
		}
		return ds;
	}
	/**增加返回double类型方法
	 * add by yangshaobo 20101203
	 * @param str
	 * @return
	 */
	public static double getDouble(String str){
		return Double.parseDouble(str);
	}

	public static List<String> getListBySplit(String str, String split) {
		List<String> list = new ArrayList<String>();
		if (str == null || str.trim().equalsIgnoreCase(""))
			return null;
		String[] strs = str.split(split);
		for (String temp : strs) {
			if (temp != null && !temp.trim().equalsIgnoreCase("")) {
				list.add(temp.trim());
			}
		}
		return list;
	}

	public static int[] getIntList(String str) {
		String[] prop = getStringList(str);
		List<Integer> tmp = new ArrayList<Integer>();
		for (int i = 0; i < prop.length; i++) {
			try {
				String sInt = prop[i].trim();
				if (sInt.length() < 20) {
					int r = Integer.parseInt(prop[i].trim());
					tmp.add(r);
				}
			} catch (Exception e) {
			}
		}
		int[] ints = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); i++) {
			ints[i] = tmp.get(i);
		}
		return ints;

	}

	public static String toWrapString(Object obj, String content) {
		if (obj == null) {
			return "null";
		} else {
			return obj.getClass().getName() + "@" + obj.hashCode() + "[\r\n"
					+ content + "\r\n]";
		}
	}

	// 将1,2,3和{1,2,3}格式的字符串转化为JDK的bitset
	// 考虑了两边是否有{}，数字两边是否有空格，是否合法数字
	public static BitSet bitSetFromString(String str) {
		if (str == null) {
			return new BitSet();
		}
		if (str.startsWith("{")) {
			str = str.substring(1);
		}
		if (str.endsWith("}")) {
			str = str.substring(0, str.length() - 1);
		}
		int[] ints = getIntList(str);
		BitSet bs = new BitSet();
		for (int i : ints) {
			bs.set(i);
		}
		return bs;
	}

	public static boolean hasExcludeChar(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (int i = 0; i < chs.length; i++) {

				if (Character.getType(chs[i]) == Character.PRIVATE_USE) {

					return checkChinese(str);
				}

			}
		}
		return false;
	}
	
	public static boolean checkChinese(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}
		char ac[] = str.toCharArray();
		int i = 0;
		for (int j = ac.length; i < j; i++) {
			char c = ac[i];
			if (c <= '\024') {
				return false;
			}
			if ("\001,;|<>`&!*(~^)-#? :'/$=\\\"".indexOf(c) >= 0) {
				return false;
			}
		}

		return true;
	}

	public static String replaceSql(String str) {
		if (str != null) {
			return str.replaceAll("'", "’").replaceAll("<", "&lt;").replaceAll(
					">", "&gt;").replaceAll("\"", "&quot;");
		}
		return "";
	}

	/**
	 * 判断两个字符串是否相等
	 * 
	 * @param s1
	 * @param s2
	 * @return true,字符串相等;false,字符串不相等
	 */
	public static boolean isEquals(String s1, String s2) {
		if (s1 != null) {
			return s1.equals(s2);
		}
		if (s2 != null) {
			return false;
		}
		// 两个字符串都是null
		return true;
	}

	/**
	 * 将obj转变为String表示
	 * 
	 * @param obj
	 * @param excludes
	 * @return
	 */
	public static String obj2String(Object obj, Map<String, Boolean> excludes) {
		BaseReflectionToStringBuilder _builder = new BaseReflectionToStringBuilder(
				obj, ToStringStyle.SHORT_PREFIX_STYLE, excludes);
		return _builder.toString();
	}

	/**
	 * 重载ReflectionToStringBuilder,用于将BaseMessage用字符串表示,但是不处理buf字段
	 * 
	 * @author <a href="mailto:dongyong.wang@opi-corp.com">wang dong yong<a>
	 * 
	 */
	private static class BaseReflectionToStringBuilder extends
			ReflectionToStringBuilder {
		private final Map<String, Boolean> excludes;

		public BaseReflectionToStringBuilder(Object object,
				ToStringStyle style, Map<String, Boolean> excludes) {
			super(object, style);
			this.excludes = excludes;
		}

		@Override
		protected boolean accept(Field field) {
			boolean _accepted = true;
			if (this.excludes != null) {
				_accepted = this.excludes.get(field.getName()) == null;
			}
			return super.accept(field) && _accepted;
		}
	}

	/**
	 * 判断字符串是否时数字
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isDigit(String text) {
		String reg = "[-]*[\\d]+[\\.\\d+]*";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(text);
		return mat.matches();
	}

	/**
	 * 判断一句话是否是汉语
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isChiness(String text) {
		String reg = "[\\w]*[\\u4e00-\\u9fa5]+[\\w]*";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(text);
		boolean result = mat.matches();
		return result;
	}

	/**
	 * 判断单个字符是否是汉语
	 * 
	 * @param cha
	 * @return
	 */
	public static boolean isChineseChar(char cha) {
		String reg = "[\\u4e00-\\u9fff]";
		Pattern pat = Pattern.compile(reg);
		String text = Character.toString(cha);
		Matcher mat = pat.matcher(text);
		boolean result = mat.matches();
		return result;
	}

	/**
	 * 判断字符是否是字母(包括大小写)或者数字
	 * 
	 * @param cha
	 * @return
	 */
	public static boolean isLetterAndDigit(String cha) {
		String reg = "[\\w]+";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(cha);
		boolean result = mat.matches();
		return result;
	}

	/**
	 * 返回字符串中汉字的数量
	 * 
	 * @param test
	 * @return
	 */
	public static int getChineseCount(String test) {
		int count = 0;
		boolean tempResult = false;
		for (int i = 0; i < test.length(); i++) {
			char cha = test.charAt(i);
			tempResult = isChineseChar(cha);
			if (tempResult) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 返回字符串中字母和数字的个数，其中字母包括大小写
	 * 
	 * @param text
	 * @return
	 */
	public static int getLetterAndDigitCount(String text) {
		int count = 0;
		boolean tempResult = false;
		for (int i = 0; i < text.length(); i++) {
			tempResult = isLetterAndDigit(text);
			if (tempResult) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return true,字符串是空的;false,字符串不是空的
	 */
	public static boolean isEmpty(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return true;
		}
		return false;
	}

	/**
	 * 获得字符串的int值
	 * 
	 * @param str
	 * @return 0, 字符串是null或者是空串
	 */
	public static int getIntValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return 0;
		}
		try {
			return (int) Double.parseDouble(str.trim());
		} catch (NumberFormatException e) {
			return new BigDecimal(str).intValue();
		}
	}
	
	/**
	 * 获得字符串的int值
	 * 
	 * @param str
	 * @return 0, 字符串是null或者是空串
	 */
	public static short getShortValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return 0;
		}
		try {
			return (short) Double.parseDouble(str.trim());
		} catch (NumberFormatException e) {
			return new BigDecimal(str).shortValue();
		}
	}

	/**
	 * 获得字符串的String值
	 * 
	 * @param str
	 * @return "", 字符串是null或者是空串
	 */
	public static String getStringValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return "";
		}
		return str.trim();
	}

	/**
	 * 获得字符串的float值
	 * 
	 * @param str
	 * @return 0, 字符串是null或者是空串
	 */
	public static float getFloatValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return 0;
		}
		try {
			return Float.parseFloat(str.trim());
		} catch (NumberFormatException e) {
			return new BigDecimal(str).floatValue();
		}
	}

	/**
	 * 获得字符串的boolean值
	 * 
	 * @param str
	 * @return false, 字符串是null或者是空串
	 */
	public static boolean getBooleanValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return false;
		}
		return str.trim().equals("1");
	}

	/**
	 * 获得字符串的long值
	 * 
	 * @param str
	 * @return 0, 字符串是null或者是空串
	 */
	public static long getLongValue(String str) {
		if (str == null || (str.trim().length() == 0)) {
			return 0;
		}
		try {
			return Long.parseLong(str.trim());
		} catch (NumberFormatException e) {
			return new BigDecimal(str).longValue();
		}
	}
	
    /**
     * 数组转化为String
     * @param s
     * @param sep
     * @return
     */
    public static String arrayToString(String[] s, char sep) {
        if (s == null || s.length == 0){
        	return "";
        }
        StringBuffer buf = new StringBuffer();
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                if (i > 0)
                    buf.append(sep);
                buf.append(s[i]);
            }
        }
        return buf.toString();
    }
    
    /**
	 * 大写第一个字母
	 * 
	 * @param propName
	 * @return
	 */
	public static String firstCapital(String propName) {
		char[] ch = propName.toCharArray();
		if (ch.length <= 0) {
			return "";
		}
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * 小写第一个字母
	 * 
	 * @param propName
	 * @return
	 */
	public static String firstLower(String propName) {
		char[] ch = propName.toCharArray();
		if (ch.length <= 0) {
			return "";
		}
		if (ch[0] >= 'A' && ch[0] <= 'Z') {
			ch[0] = (char) (ch[0] + 32);
		}
		return new String(ch);
	}

	/**
	 * 将字符串首字母大写
	 * 
	 * @param s
	 * @return
	 */
	public static String upperCaseFirstCharOnly(String s) {
		if (s == null || s.length() < 1) {
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}}
