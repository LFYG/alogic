package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * throw语句
 * 
 * @author duanyy
 *
 */
public class Throw extends AbstractStatement {
	protected String id = STMT_EXCEPTION;
	protected String msg;
	
	public Throw(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p,"id",id,true);
		msg = PropertiesConstants.getString(p,"msg","",true);
	}

	int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		throw new BaseException(id,msg);
	}

}
