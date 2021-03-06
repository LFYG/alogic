package com.alogic.tracer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.alogic.tlog.TLogHandlerFactory;
import com.anysoft.stream.Handler;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 追踪器
 * 
 * @author duanyy
 * @since 1.6.5.3
 * 
 * @version 1.6.5.7 [20160525 duanyy] <br>
 * - 增加enable方法，以便可以选择关闭tracer <br>
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - trace日志的时长单位改为ns <br>
 * - 新增com.alogic.tlog，替代com.alogic.tracer.log包;
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.21 [20170303 duanyy] <br>
 * - TLog增加parameter字段，便于调用者记录个性化参数 <br>
 * 
 * @version 1.6.9.8 [20170821] <br>
 * - tlog增加keyword字段 <br>
 */
public interface Tracer extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 是否启用Tracer
	 * @return 是否
	 */
	public boolean enable();
	
	/**
	 * 开始过程
	 * 
	 * @return 上下文实例
	 */
	public TraceContext startProcedure();
	
	/**
	 * 以指定的序列号和顺序开始过程
	 * @param sn 序列号
	 * @param order 顺序
	 * 
	 * @return 上下文实例
	 */
	public TraceContext startProcedure(String sn,String order);
		
	/**
	 * 结束过程
	 * @param ctx 上下文实例
	 * @param type 过程类型
	 * @param name 过程名称
	 * @param result 结果
	 * @param note 说明
	 * @param parameter 参数，参数的内容和编码由type确定
	 * @param keyword 业务关键字
	 * @param contentLength 内容长度
	 */	
	public void endProcedure(TraceContext ctx,String type,String name,String result,String note,String parameter,String keyword,long contentLength);
	
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements Tracer{
		/**
		 * a trace logger
		 */
		protected Handler<TLog> logger=null;
		
		/**
		 * 是否开放
		 */
		protected boolean enable = false;
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(Tracer.class);
		
		/**
		 * 是否开放
		 */
		public boolean enable(){
			return enable;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}	
		
		@Override
		public void configure(Properties p){
			enable = PropertiesConstants.getBoolean(p, "tracer.enable", enable);
			logger = TLogHandlerFactory.getHandler();
		}
		
		public void log(TLog log){
			if (logger != null){
				logger.handle(log, System.currentTimeMillis());
			}
		}
	}
}
