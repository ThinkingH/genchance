package com.genchance.tools.codegen;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.genchance.tools.core.CoreErrors;
import com.genchance.tools.core.ErrorsUtil;
import com.genchance.tools.core.config.IConfig;
import com.genchance.tools.core.script.IScriptEngine;
import com.genchance.tools.core.script.impl.JSScriptManagerImpl;

/**
 * 日志消息生成器，包括xml配置文件和java文件
 * 
 *
 */
public class ModelLogMsgGenerator {
	private static final String COMMON_FIELD_PREFIX = "commonField";
	// serverid 开始 不是log_uid公用字段的最小行号
	private static final int commonFieldMinIndex = 18;
	// 非公用字段的最小行号
	private static final int privateFieldMinIndex = 26; 
	
	private static Map<String, Reasion> reasionmap = null;
	private static final Logger logger = LoggerFactory.getLogger(ModelLogMsgGenerator.class);

	public static void main(String[] args) throws IOException, BiffException {
		
		Properties _vp = new Properties();
		_vp.put("file.resource.loader.path", "resource/logs/codetemplate");
		try {
			Velocity.init(_vp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		LogMsgGenConfig _config = buildLogMsgGenConfig();
		String _packageName = _config.getPackageName();//导出model文件对应logserver的包�?
		String _logServiceDir = _config.getLogServiceDir(); //导出文件对应gameserver的包�?
		String _logSrcGenDir = _config.getLogSrcGenDir();//导出logs文件的根目录
		String _logResGenDir = _config.getLogResGenDir();//导出logs ibatis配置文件片段的根目录
		String _gsGenDir = _config.getGsGenDir();//导出gameserver文件根目�?
		String _packageNameSub = _config.getPackageNameSub();//com.mop.mo.logs
		String _excelPath = _config.getExcelPath(); //配置文件
		String _reasonsDir = _config.getReasonsDir(); //LogReasons.java对应logserver的包�?
		String _reasonLogSrcGenDir = _config.getReasonLogSrcGenDir();//导出LogReasons.java相关文件存放的根目录
		String _gameCode = _config.getGameCode();//游戏Code
		String _gameName = _config.getGameName();//游戏名称
		Workbook workbook = Workbook.getWorkbook(new File(_excelPath));
		/* 解析日志配置文件列表 */
		List<LogConfig> _logConfigs = getLogConfigs(workbook.getSheet("表一览"));
		
		/* 删除导出的旧文件 */ 
		deleteOutFile(new File("out"));
		if(logger.isInfoEnabled()) {
			logger.info("Delete dir : game_tools/out/");
		}
		
		/* 导出MessageType.java */
//		generateMessageTypeConfig(_logConfigs, _packageNameSub+"."+_logServerDir, _logSrcGenDir, _packageNameSub);
		/* 导出LogMessageRecognizer.java */
//		generateLogMsgRecognizer(_logConfigs, _packageNameSub+"."+_logServerDir, _logSrcGenDir, _packageNameSub);
		
		/* 加载日志消息的共有字�?*/
		Map<String, Field> sharedFieldMap = new LinkedHashMap<String, Field>();
		importSharedField(workbook.getSheet(_logConfigs.get(0).getLogFile()), sharedFieldMap);
		
		/* 导出日志xml配置文件和日志消�?java */
		for(LogConfig _logType : _logConfigs) {
			generateMessageConfig(workbook.getSheet(_logType.getLogFile()), _packageNameSub+"."+_packageName, _logSrcGenDir, _logType, sharedFieldMap, _packageNameSub, _logResGenDir);
		}
		
		/* 导出GameServer下的日志接口文件LZRLogService.java */
		generateMOLogService(_logConfigs, _logServiceDir, _gsGenDir,sharedFieldMap, _packageNameSub, _reasonsDir);

        generateBaseLog(_logServiceDir,_gsGenDir,_packageNameSub);

		/* 导出logserver的ibatis sqlMap配置 */
//		generateLogsIbatisConfig(_logConfigs,  _logResGenDir);
		/* 导出LogReasons的配�?java */
		generateLogReasons(workbook.getSheet("reason_list"), _reasonsDir, _reasonLogSrcGenDir, _logConfigs);
		
		generateLogReasonsDictionary(workbook.getSheet("reason_list"), _reasonsDir, _reasonLogSrcGenDir, _logConfigs,_gameCode,_gameName);
//		if (_config.isGm()){
//			gmtoolsLogMessages(_config.getGmGenSrcDir(), _config.getGmbeanDir(), _logConfigs,
//					sharedFieldMap);
//			gmtoolslogconfig(_config.getGmGenSrcDir(), _config.getGmbeanDir(), _logConfigs,
//					sharedFieldMap);
//			gmtoolslogconfig(_config.getGmGenSrcDir(), _config.getGmbeanDir(), _logConfigs, sharedFieldMap);
//			gmtoolslogBean(_config.getGmGenSrcDir(), _config.getGmbeanDir(), _logConfigs, sharedFieldMap);
//			gmtoolslogjsp(_config.getGmGenJspDir(), _config.getGmJspDir(), _logConfigs, sharedFieldMap);
//		}
	}

	private static void generateLogReasonsDictionary(Sheet sheet, String reasonsDir , String genDir, List<LogConfig> _logConfigs, String _gameCode, String _gameName)throws IOException {

		int row = sheet.getRows();
		reasionmap = new HashMap<String, Reasion>();
		for (int i = 1;i < row;i++){
			Cell[] cells = sheet.getRow(i);
			if (cells.length < 8){
				continue;
			}
			Cell tableName = cells[3];
			if (isCellEmpty(tableName)){
				continue;
			}
			String key = StringUtils.capitalize(convert(tableName.getContents()) + "Reason");
			String shortName = tableName.getContents();
			//reason_id
			if (isCellEmpty(cells[6])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason_id"));
			}
			//reason
			if (isCellEmpty(cells[5])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason"));
			}
			//reason_desc
			if (isCellEmpty(cells[7])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason_desc"));
			}
			ReasionInfo res = new ReasionInfo(cells[6].getContents(), cells[5].getContents(), cells[7].getContents());
			if (reasionmap.containsKey(key)){
				reasionmap.get(key).addList(res);
			} else {
				List<ReasionInfo> list = new ArrayList<ReasionInfo>();
				list.add(res);
				String logName = null;
				for (LogConfig log : _logConfigs){
					if (log.equals(key)){
						logName = log.getLogInfo();
						break;
					}
				}
				if (logName == null && "".equals(logName)){
					throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", convert(tableName.getContents())));
				}
				reasionmap.put(key, new Reasion(key,shortName, logName, list));
			}
		}
		
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("reasonsDirs", reasonsDir);
		_context.put("reasionList", reasionmap.values());
		_context.put("gameCode", _gameCode);
		_context.put("gameName", _gameName);
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogReasonsDictionary.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, reasonsDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "LogReasonsDictionary.json")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("LogReasons.java is generated at " + _srcDist.getAbsolutePath());
		}
	
		
	}

	private static LogMsgGenConfig buildLogMsgGenConfig() {
//		ClassLoader _classLoader = Thread.currentThread().getContextClassLoader();
//		URL _url = _classLoader.getResource("log_msg_gen.cfg.js");
		return buildConfig(LogMsgGenConfig.class, "resource/log_msg_gen.cfg.js");
	}
	
	/**
	 * 生成日志消息识别器文�?
	 * 
//	 * @param logFileName
	 * @param logServerDir
	 * @param genDir
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateLogMsgRecognizer( List<LogConfig> logTypes, String logServerDir, String genDir,String packageName) throws FileNotFoundException, IOException, UnsupportedEncodingException {
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("logTypes", logTypes);
		_context.put("packageName", packageName);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogMsgRecognizer.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, logServerDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "LogMessageRecognizer.java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("LogMessageRecognizer.java is generated at " + _srcDist.getAbsolutePath());
		}

	}
	
	/**
	 * 生成日志消息类型文件
	 * 
	 * @param logFileName
	 * @param logServerDir
	 * @param genDir
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateMessageTypeConfig( List<LogConfig> logConfigs, String logServerDir, String genDir, String packageName) throws FileNotFoundException, IOException, UnsupportedEncodingException {
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("logConfigs", logConfigs);
		_context.put("packageName", packageName);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogMsgType.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, logServerDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "MessageType.java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("MessageType.java is generated at " + _srcDist.getAbsolutePath());
		}
	}
	
	/**
	 * 生成GameServer下的接口文件
	 * 
	 * @param logConfigs
	 * @param serviceDir
	 * @param genDir
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateMOLogService( List<LogConfig> logConfigs, String serviceDir, String genDir,Map<String, Field> sharedFieldMap,String packageName,String reasonsDirs) throws FileNotFoundException, IOException, UnsupportedEncodingException {
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("logConfigs", logConfigs);
		_context.put("sharedFields", sharedFieldMap.values());
		_context.put("packageName", packageName);
		_context.put("reasonsDirs", reasonsDirs);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogService.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, serviceDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "LogService.java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("MOLogService.java is generated at " + _srcDist.getAbsolutePath());
		}
	}

    private static void generateBaseLog(String serviceDir, String genDir,String _packageNameSub) throws FileNotFoundException, IOException, UnsupportedEncodingException {
        VelocityContext _context = new VelocityContext();
        _context.put("packageNameSub", _packageNameSub);

        StringWriter _readWriter = new StringWriter();
        try {
            Velocity.mergeTemplate("BaseLog.template", "UTF-8",	_context, _readWriter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        File _srcDist = new File(genDir, serviceDir.replace('.', '/'));
        if (!_srcDist.exists()) {
            if (!_srcDist.mkdirs()) {
                throw new RuntimeException("Can't create dir " + _srcDist);
            }
        }
        Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "BaseLog.java")), "UTF-8");
        _fileWriter.write(_readWriter.toString());
        _fileWriter.close();

        if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
            ModelLogMsgGenerator.logger.info("BaseLog.java is generated at " + _srcDist.getAbsolutePath());
        }
    }

	/**
	 * 生成xml日志配置文件
	 * 
	 * @param msgConfig
	 * @param packageName
	 * @param sourceDir
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateMessageConfig(Sheet sheet, String packageName, String sourceDir, LogConfig logConfig, Map<String, Field> sharedFieldMap, String packageNameSub, String _logResGenDir) throws FileNotFoundException, IOException, UnsupportedEncodingException {

		// 解析消息定义的字�?
		Map<String, Field> _fieldMap = new LinkedHashMap<String, Field>();
		//_fieldMap.putAll(sharedFieldMap);
		int row = sheet.getRows();
		System.out.println("sheet:"+sheet.getName());
		for (int i = privateFieldMinIndex;i < row;i++){
			Cell[] cells = sheet.getRow(i);
			if (cells.length < 14){
				continue;
			}
			Cell fieldName = cells[1];
			if (isCellEmpty(fieldName)){
				continue;
			}
			String contents = "";
			//长度
			if (!isCellEmpty(cells[5])){
				contents = cells[5].getContents();
			}
			
			//not null
			if (!isCellEmpty(cells[13])){
				contents += " not null";
			}
			//默认�?
			if (!isCellEmpty(cells[6])){
				contents += " default " + cells[6].getContents();
			}
			//uk
			if (!isCellEmpty(cells[9])){
				contents += " key";
			}
			System.out.println("field:" + fieldName.getContents());
			Field _f = new Field(fieldName.getContents(), cells[4].getContents(), contents, cells[2].getContents());
			if(_fieldMap.containsKey(_f.getName())) {
				throw new IllegalArgumentException("Duplicate variable name '" + _f.getName() + "' in " + sheet.getName() + " !");
			}
			_fieldMap.put(_f.getName(), _f);
		}

		logConfig.setFields(_fieldMap.values());
		
		String _ibatisFile = logConfig.getLogFileName(); 
		
		_ibatisFile = _ibatisFile.substring(0, _ibatisFile.lastIndexOf("Log")).toLowerCase();

		String _logName = logConfig.getLogFile();
		
		List<Field> _allFields = new ArrayList<Field>();
		_allFields.addAll(sharedFieldMap.values());
		_allFields.addAll(logConfig.getFields());
/*
		// 生成消息的xml配置文件
		VelocityContext _context = new VelocityContext();
		_context.put("logName", _logName);
		_context.put("msgName", packageName+"."+ logConfig.getLogFileName());
		_context.put("fields", logConfig.getFields());
		_context.put("sharedFields",sharedFieldMap.values());
		_context.put("allFields", _allFields);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogMsgConfigure.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(_logResGenDir, "logdbsqlmap");
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, _ibatisFile+ ".xml")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();

		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info(_logName + ".xml is generated at " + _srcDist.getAbsolutePath());
		}
*/
		generateMessageSource2(logConfig.getLogFile(), packageName, sourceDir, logConfig.getFields(),sharedFieldMap, packageNameSub);
	}
	/**
	 * 生成日志消息源文�?
	 * 
	 * @param msgName
	 * @param packageName
	 * @param sourceDir
	 * @param fields
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateMessageSource2(String logFile, String packageName,String sourceDir, List<Field> fields,Map<String, Field> sharedFieldMap, String packageNameSub) throws FileNotFoundException, IOException, UnsupportedEncodingException {

		String[] args = logFile.substring(0, logFile.lastIndexOf("_log")).split("_");
		String str = "";
		for (String s : args){
			str += StringUtils.capitalize(s);
		}
		String _className =   str + "Log";
		List<Field> _allFields = new ArrayList<Field>();
		_allFields.addAll(sharedFieldMap.values());
		_allFields.addAll(fields);

		// 生成消息的Java文件
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("className", _className);
		_context.put("packageName", packageName);
		_context.put("upperClassName", logFile.substring(0, logFile.lastIndexOf("_log")).toUpperCase());
		_context.put("fields", fields);
		_context.put("sharedFields",sharedFieldMap.values());
		_context.put("allFields", _allFields);
		_context.put("packageNameSub", packageNameSub);
		_context.put("logFile", logFile);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogMsgClass.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(sourceDir, packageName.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, _className + ".java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info(_className + ".java is generated at " + _srcDist.getAbsolutePath());
		}
	}
	
	/**
	 * 加载日志消息的共有字�?
	 * 
	 * @param sharedFieldFile
	 * 			存放共有字段的文�?
	 * @param sharedFieldMap
	 * 			存放共有字段的表
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void importSharedField(Sheet sheet, Map<String, Field> sharedFieldMap) throws FileNotFoundException, IOException, UnsupportedEncodingException {
		

		for (int i = commonFieldMinIndex;i < privateFieldMinIndex;i++){
			Cell[] cells = sheet.getRow(i);
			if (cells.length < 14){
				continue;
			}
			Cell fieldName = cells[1];
			if (isCellEmpty(fieldName)){
				continue;
			}

			String contents = "";
			//长度
			if (!isCellEmpty(cells[5])){
				contents = cells[5].getContents();
			}
			
			//not null
			if (!isCellEmpty(cells[13])){
				contents += " not null";
			}
			//默认�?
			if (!isCellEmpty(cells[6])){
				contents += " default " + cells[6].getContents();
			}
			//uk
			if (!isCellEmpty(cells[9])){
				contents += " key";
			}
			Field _f = new Field(fieldName.getContents(), cells[4].getContents(), contents, cells[2].getContents());
			if(sharedFieldMap.containsKey(_f.getName())) {
				throw new IllegalArgumentException("Duplicate variable name '" + _f.getName() + "' in " + sheet.getName() + " !");
			}
			sharedFieldMap.put(_f.getName(), _f);
		}
	}
	/**
	 * 生成Log server的配置文件sqlMap信息
	 * 
	 * @param logConfigs
	 * @param packageName
	 * @param genDir
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void generateLogsIbatisConfig( List<LogConfig> logConfigs, String genDir) throws FileNotFoundException, IOException, UnsupportedEncodingException {
		// 生成消息的Java文件

		
		String _endInfo = ".xml";
		
		File _srcDist = new File(genDir);
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		List<String> list = new ArrayList<String>();
		for(LogConfig _logConfig : logConfigs) {
			String _tableName = _logConfig.getUpperLogType().replaceAll("_", "").toLowerCase();
			list.add(_tableName + _endInfo);
		}
		VelocityContext _context = new VelocityContext();
		_context.put("filenames", list);
	
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("dbConfigure.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "logdbconfig.xml")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("log_ibatis_config_section.xml is generated at " + _srcDist.getAbsolutePath());
		}
	}
	
	/**
	 * 导出新文件前，先清空之前�?
	 * 
	 * @param path
	 */
	private static void deleteOutFile(File path) {
		if(!path.exists()) {
			return;
		}
		
		if(path.isFile()) {
			path.delete();
			return;
		}
		
		File[] _files = path.listFiles();
		for(File file : _files) {
			deleteOutFile(file);
		}
		path.delete();
	}
	
	/**
	 * 日志文件信息
	 * 
	 * @author <a href="mailto:yong.fang@opi-corp.com">fang yong<a>
	 *
	 */
	public static class LogConfig {
		/** 日志配置文件�?*/
		private final String logFile;
		/** 日志的类型�? */
		private final String logTypeValue;
		/** 日志的注�?*/
		private final String logInfo;
		
		private List<Field> fields = new ArrayList<Field>();
		
		public LogConfig(String logFile, String logTypeValue, String logInfo) {
			this.logFile = logFile;
			this.logTypeValue = logTypeValue;
			this.logInfo = logInfo;
		}

		/**
		 * 获得大写日志类型名，不包�?Log"
		 * 
		 * @return
		 */
		public String getUpperLogType() {
//			return logFile.substring(0, logFile.lastIndexOf("Log.txt")).toUpperCase();
//			System.out.println(logFile);
			return logFile.substring(0, logFile.lastIndexOf("_log")).toUpperCase();
		}
		
		public String getLogFile() {
			return logFile;
		}

		/**
		 * 获得日志文件名，不带扩展�?
		 * 
		 * @return
		 */
		public String getLogFileName() {
			String[] args = logFile.substring(0, logFile.lastIndexOf("_log")).split("_");
			String str = "";
			for (String s : args){
				str += StringUtils.capitalize(s);
			}
			return  str + "Log";
//			return logFile.substring(0, logFile.lastIndexOf(".txt"));
		}

		/**
		 * 获得日志枚举类型名称
		 * 
		 * @return
		 */
		public String getLogEnumName() {
//			String[] args = logFile.substring(0, logFile.lastIndexOf("_log")).split("_");
//			String str = "";
//			for (String s : args){
//				str += StringUtils.capitalize(s);
//			}
//			return  str.toUpperCase() + "_LOG";
			
			return logFile.toUpperCase();
//			return logFile.substring(0, logFile.lastIndexOf(".txt"));
		}
		
		public boolean equals(String str){
			if ((convert(logFile) + "Reason").toUpperCase().equals(str.toUpperCase())){
				return true;
			} else {
				return false;
			}
		}
		public String getLogTypeValue() {
			return logTypeValue;
		}

		public String getLogInfo() {
			return logInfo;
		}

		public void setFields(Collection<Field> fields) {
			this.fields.addAll(fields);
		}

		public List<Field> getFields() {
			return fields;
		}
	}
	/**
	 * 日志消息字段
	 * 
	 * @author <a href="mailto:yong.fang@opi-corp.com">fang yong<a>
	 *
	 */
	public static class Field {
		/** 字段�?*/
		private final String name;
		/** 字段类型 */
		private final String type;
		/** 字段的sql额外约束 */
		private final String constraint;
		/** 是否为该字段建立索引 */
		private final boolean isKey;
		/** 字段注释 */
		private final String fieldInfo;
		/** 数据库字段名*/
		private final String columnName;
		
		
		private static final Map<String, String> _typeMap = new HashMap<String, String>();
		private static final Map<String, String> _readFuncMap = new HashMap<String,	String>();
		private static final Map<String, String> _writeFuncMap = new HashMap<String, String>();
		static {
			// int
			_typeMap.put("int", "int(11)");
			_readFuncMap.put("int", "readInt()");
			_writeFuncMap.put("int", "writeInt");
			// long
			_typeMap.put("long", "bigint");
			_readFuncMap.put("long", "readLong()");
			_writeFuncMap.put("long", "writeLong");
			// String
			_typeMap.put("String", "varchar");
			_readFuncMap.put("String", "readString()");
			_writeFuncMap.put("String", "writeString");
			// bytes
			_typeMap.put("byte[]", "blob");
			_readFuncMap.put("byte[]", "readByteArray()");
			_writeFuncMap.put("byte[]", "writeByteArray");
			// timestamp
			_typeMap.put("timestamp", "timestamp");
			_readFuncMap.put("timestamp", "readString()");
			_writeFuncMap.put("timestamp", "writeString");
			// timestamp
			_typeMap.put("text", "text");
			_readFuncMap.put("text", "readString()");
			_writeFuncMap.put("text", "writeString");
		}

		public Field(String columnName, String type, String constraint, String fieldInfo) {
			super();
//			this.name = name.trim();
//			this.columnName = this.name.replaceAll("([A-Z]{1})","_$1").toLowerCase();
			this.columnName = columnName;
			this.name = convert(columnName);
			/* 类型转换为小�? 字符串类型对应String */
			String lowerCaseType = type.trim().toLowerCase();
			if(lowerCaseType.equals("string") || lowerCaseType.equals("varchar")){
				this.type = "String";
			}
			else{
				this.type = lowerCaseType;
			}	
			
			/* 类型有效性检�?*/
			if(!(this.type.equals("int") || this.type.equals("long") || this.type.equals("String")  || this.type.equals("byte[]") || this.type.equals("text") || this.type.equals("timestamp"))) {
				throw new IllegalArgumentException("Unsupported variable type '" + this.type + "'! Notice: only 'int', 'long' and 'String' supported!");
			}
			
			this.fieldInfo = fieldInfo.trim();
			
			if(constraint.contains("key")) {
				constraint = constraint.replaceAll("key", "");
				isKey = true;
			} else {
				isKey = false;
			}
			
			if(this.type.equals("String") && (constraint.trim()).equals("")) {
				this.constraint = "256";
			} else if (this.type.equals("String") ){
				this.constraint = constraint.replaceAll("([0-9]+{1})","($1)").toLowerCase().trim();
			}
			else{
				this.constraint = constraint.trim();
			}
		}

		/**
		 * @return the columnName
		 */
		public String getColumnName() {
			return columnName;
		}

		public String getName() {
			return name;
		}

		/**
		 * 字段名转为首字母大写
		 * 
		 * @return
		 */
		public String getUpperColumnName() {
			char firstChar = columnName.charAt(0);
			return Character.toUpperCase(firstChar) + columnName.substring(1);
		}
		
		/**
		 * 字段名转为首字母大写
		 * 
		 * @return
		 */
		public String getUpperName() {
			char firstChar = name.charAt(0);
			return Character.toUpperCase(firstChar) + name.substring(1);
		}

		public String getType() {
			if ("timestamp".equalsIgnoreCase(type)){
				return "long";
			} else if ("text".equalsIgnoreCase(type)){
				return "String";
			} else {
				return type;
			}
			
		}
		
		public String getReadType() {
			return _typeMap.get(type);
		}

		public String getReadFunc() {
			return _readFuncMap.get(type);
		}

		public String getWriteFunc() {
			return _writeFuncMap.get(type);
		}

		public String getConstraint() {
			return constraint;
		}

		public String getFieldInfo() {
			return fieldInfo;
		}
		
		public boolean getIsKey() {
			return isKey;
		}
	}
	
	private static List<LogConfig> getLogConfigs(Sheet sheet){
		int row = sheet.getRows();
		List<LogConfig> list = new ArrayList<LogConfig>();
		for (int i = 6;i < row;i++){
			Cell[] cells = sheet.getRow(i);
			if (cells.length < 15){
				continue;
			}
			Cell tableName = cells[1];
			if (isCellEmpty(tableName)){
				continue;
			}
			LogConfig _log = new LogConfig(tableName.getContents(), cells[14].getContents(), cells[2].getContents());
			list.add(_log);
		}
		return list;
	}
	
	private static String convert(String name){
		String[] strs = name.split("_");
		String str = StringUtils.uncapitalize(strs[0]);
		for (int i = 1 ; i < strs.length;i++){
			str += StringUtils.capitalize(strs[i]);
		}
//		System.out.println(str);
		return str;
	}
	

	/**
	 * 根据指定的配置类�?tt>configClass</tt>�?tt>configURL</tt>中加载配�?
	 * 
	 * @param <T>
	 * @param configClass
	 *            配置的类�?
//	 * @param configURL
	 *            配置文件的URL,文件内容是一个以JavaScript编写的配置脚�?
	 * @return 从configURL加载的配置对�?
	 * @exception RuntimeException
	 *                从configClass构�?对象失败时抛出此异常
	 * @exception IllegalArgumentException
	 *                配置验证失败时抛出此异常
	 * @exception IllegalStateException
	 *                从configUrl中加载内容失败时抛出此异�?
	 */
	private static <T extends IConfig> T buildConfig(Class<T> configClass, String file) {
		if (configClass == null) {
			throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "configClass"));
		}
		if (file == null) {
			throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "configURL"));
		}
		if(logger.isInfoEnabled()){
			logger.info("Load config ["+configClass+"] from ["+file+"]");
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
			_r = new InputStreamReader(new FileInputStream(file), "UTF-8");
			_scriptContent = IOUtils.toString(_r);
		} catch (IOException e) {
			throw new IllegalStateException("Can't load config from url [" + file + "]");
		} finally {
			IOUtils.closeQuietly(_r);
		}
		_jsEngine.runScript(_bindings, _scriptContent);
		_config.validate();
		return _config;
	}
	
	private static boolean isCellEmpty(Cell cell){
		if (cell == null || cell.getType() == CellType.EMPTY || 
				cell.getContents() == null || "".equals(cell.getContents().trim())){
			return true;
		}
		return false;
	}
	
	private static void generateLogReasons(Sheet sheet, String reasonsDir , String genDir, List<LogConfig> _logConfigs) throws IOException{
		int row = sheet.getRows();
		reasionmap = new HashMap<String, Reasion>();
		for (int i = 1;i < row;i++){
			Cell[] cells = sheet.getRow(i);
			if (cells.length < 8){
				continue;
			}
			Cell tableName = cells[3];
			if (isCellEmpty(tableName)){
				continue;
			}
			String key = StringUtils.capitalize(convert(tableName.getContents()) + "Reason");
			String shortName = tableName.getContents();
			//reason_id
			if (isCellEmpty(cells[6])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason_id"));
			}
			//reason
			if (isCellEmpty(cells[5])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason"));
			}
			//reason_desc
			if (isCellEmpty(cells[7])){
				throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", "reason_desc"));
			}
			ReasionInfo res = new ReasionInfo(cells[6].getContents(), cells[5].getContents(), cells[7].getContents());
			if (reasionmap.containsKey(key)){
				reasionmap.get(key).addList(res);
			} else {
				List<ReasionInfo> list = new ArrayList<ReasionInfo>();
				list.add(res);
				String logName = null;
				for (LogConfig log : _logConfigs){
					if (log.equals(key)){
						logName = log.getLogInfo();
						break;
					}
				}
				if (logName == null && "".equals(logName)){
					throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", convert(tableName.getContents())));
				}
				reasionmap.put(key, new Reasion(key, shortName,logName, list));
			}
		}
		
		VelocityContext _context = new VelocityContext();
		_context.put("generator", ModelLogMsgGenerator.class.getName());
		_context.put("date", new Date());
		_context.put("reasonsDirs", reasonsDir);
		_context.put("reasionList", reasionmap.values());
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("LogReasons.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, reasonsDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "LogReasons.java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		
		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("LogReasons.java is generated at " + _srcDist.getAbsolutePath());
		}
	}
	public static class Reasion {
		
		/** log_table */
		private String key;
		
		/** log表名 */
		private String tableName;
		
		private List<ReasionInfo> list;
		
		private String shortName;
		
		public Reasion(String key, String shortName,String tableName, List<ReasionInfo> list){
			this.key = key;
			this.shortName = shortName;
			this.tableName = tableName;
			this.list = list;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<ReasionInfo> getList() {
			return list;
		}

		public void setList(List<ReasionInfo> list) {
			this.list = list;
		}
		
		public void addList(ReasionInfo res){
			for (ReasionInfo info: list){
				if (info.getId().toUpperCase().equals(res.getId().toUpperCase())){
					throw new IllegalArgumentException(ErrorsUtil.error(CoreErrors.ARG_NOT_NULL_EXCEPT, "", res.getId() + "    " + info.getId() + "    重复"));
				}
			}
			this.list.add(res);
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		

	}
	public static class ReasionInfo {
		
		/** reason_id */
		private String id;
		
		/** reason */
		private String value;
		
		/** reason_desc */
		private String desc;
		
		public ReasionInfo(String id, String value, String desc){
			this.id = id.toUpperCase();
			this.value = value;
			this.desc = desc;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private static void gmtoolsLogMessages(String genDir, String beanDir, List<LogConfig> _logConfigs,
			Map<String, Field> sharedFieldMap) throws IOException {

		VelocityContext _context = new VelocityContext();
		_context.put("commonFieldPrefix", COMMON_FIELD_PREFIX);
		_context.put("sharedFields",sharedFieldMap.values());
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/logMessages_zh_CN.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, "".replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		_context.remove("sharedFields");
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist,
				"logMessages_zh_CN.properties")), "UTF-8");
		for (LogConfig logConfig : _logConfigs) {
			_context = new VelocityContext();
			String logFile = logConfig.getLogFile();
			_context.put("logConfig", logConfig);
			_context.put("fields", logConfig.getFields());
			String key = StringUtils.capitalize(convert(logFile) + "Reason");
			_context.put("reasion", reasionmap.get(key));
			try {
				Velocity.mergeTemplate("gmtools/logMessages_zh_CN.template", "UTF-8", _context,
						_readWriter);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();


		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("logMessages_zh_CN.properties is generated at "
					+ _srcDist.getAbsolutePath());
		}
	}

	private static void gmtoolslogconfig(String genDir, String beanDir, List<LogConfig> _logConfigs,
			Map<String, Field> sharedFieldMap) throws IOException {

		VelocityContext _context = new VelocityContext();
		_context.put("logConfigs", _logConfigs);
		_context.put("sharedFields", sharedFieldMap.values());
		_context.put("beanDir", beanDir);
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/logconfig.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, "".replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist,
				"logconfig.xml")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		_readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/logtoolsSql.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		_fileWriter = new OutputStreamWriter(
				new FileOutputStream(new File(_srcDist, "logtoolsSql.xml")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		// File f = new
		// File("resource/logs/codetemplate/gmtools/logSqlMapConfig.xml");
		// System.out.println(f.getAbsolutePath());

		if (ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("logconfig.xml logtoolsSql.xml is generated at "
					+ _srcDist.getAbsolutePath());
		}
	}
	
	private static void gmtoolslogBean(String genDir, String beanDir , List<LogConfig> _logConfigs, Map<String, Field> sharedFieldMap) throws IOException{

		VelocityContext _context = new VelocityContext();
		_context.put("fields",sharedFieldMap.values());
		_context.put("beanDir", beanDir);
		_context.put("iscommon", true);
		_context.put("logtable", "common");
		_context.put("commonFieldPrefix", COMMON_FIELD_PREFIX);
		
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/LogBean.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		File _srcDist = new File(genDir, beanDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
		
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "AllLogsBean.java")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		for (LogConfig logConfig:_logConfigs){
			_context = new VelocityContext();
			_context.put("logFile", logConfig.getLogFile());
			_context.put("logname", logConfig.getLogInfo());
			_context.put("logtable", logConfig.getLogFileName());
			_context.put("fields", logConfig.getFields());
			_context.put("beanDir", beanDir);
			_context.put("iscommon", false);
			_readWriter = new StringWriter();
			try {
				Velocity.mergeTemplate("gmtools/LogBean.template", "UTF-8",	_context, _readWriter);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			_fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, logConfig.getLogFileName() + "Bean.java")), "UTF-8");
			_fileWriter.write(_readWriter.toString());
			_fileWriter.close();
		}

		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("AllLogsBean  is generated at " + _srcDist.getAbsolutePath());
		}
	}
	
	private static void gmtoolslogjsp(String genDir, String jspDir , List<LogConfig> _logConfigs, Map<String, Field> sharedFieldMap) throws IOException{

		VelocityContext _context = new VelocityContext();
		_context.put("sharedFields",sharedFieldMap.values());
		_context.put("fieldsAll",sharedFieldMap.values());
		_context.put("iscommon", true);
		_context.put("reasionInfoList", reasionmap.values());
		_context.put("commonFieldPrefix", COMMON_FIELD_PREFIX);
		StringWriter _readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/title_log.jsp.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		File _srcDist = new File(genDir, jspDir.replace('.', '/'));
		if (!_srcDist.exists()) {
			if (!_srcDist.mkdirs()) {
				throw new RuntimeException("Can't create dir " + _srcDist);
			}
		}
//		FileUtils.copyDirectory();
		FileUtils.copyDirectory(new File("resource/logs/codetemplate/gmtools/comlog"), new File(genDir), new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				if (arg0.isDirectory()&& ".svn".equals(arg0.getName())){
					return false;
				}
				return true;
			}
		});
		Writer _fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "title_common_log.jsp")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();

		_readWriter = new StringWriter();
		_context.put("logConfigs", _logConfigs);
		try {
			Velocity.mergeTemplate("gmtools/search_main.template", "UTF-8", _context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		_fileWriter = new OutputStreamWriter(new FileOutputStream(new File(genDir, "search_main.jsp")),
				"UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();

		_readWriter = new StringWriter();
		try {
			Velocity.mergeTemplate("gmtools/detail_log.jsp.template", "UTF-8",	_context, _readWriter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		_fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "detail_common_log.jsp")), "UTF-8");
		_fileWriter.write(_readWriter.toString());
		_fileWriter.close();
		for (LogConfig logConfig:_logConfigs){
			String key = StringUtils.capitalize(convert(logConfig.getLogFile()) + "Reason");
			_context = new VelocityContext();
			List<Field> list = new ArrayList<Field>();
			list.addAll(sharedFieldMap.values());
			list.addAll(logConfig.getFields());
			_context.put("iscommon", false);
			_context.put("fields", logConfig.getFields());
			_context.put("fieldsAll", list);
			_context.put("sharedFields", sharedFieldMap.values());
			_context.put("reasion", key);
			_context.put("logFile", logConfig.getLogFile());
			_context.put("reasionInfoList", reasionmap.get(key).getList());
			_context.put("commonFieldPrefix", COMMON_FIELD_PREFIX);
			_readWriter = new StringWriter();
			try {
				Velocity.mergeTemplate("gmtools/title_log.jsp.template", "UTF-8",	_context, _readWriter);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			_fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "title_"+logConfig.getLogFile()+".jsp")), "UTF-8");
			_fileWriter.write(_readWriter.toString());
			_fileWriter.close();
			_readWriter = new StringWriter();
			try {
				Velocity.mergeTemplate("gmtools/detail_log.jsp.template", "UTF-8",	_context, _readWriter);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			_fileWriter = new OutputStreamWriter(new FileOutputStream(new File(_srcDist, "detail_"+logConfig.getLogFile()+".jsp")), "UTF-8");
			_fileWriter.write(_readWriter.toString());
			_fileWriter.close();
		}

		if(ModelLogMsgGenerator.logger.isInfoEnabled()) {
			ModelLogMsgGenerator.logger.info("AllLogsBean  is generated at " + _srcDist.getAbsolutePath());
		}
	}
}
