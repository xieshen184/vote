package com.yc.us.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 读取属性文件db.properties处理类DBProperties.java
 * @author jp
 * @date   2016年03月29日 *
 */
public class DBProperties extends Properties{

	private static final long serialVersionUID = 7527422364622115641L;
	
	private static DBProperties myproperties;
	
	private static String propertyFileName="db.properties";
	
	private static final Logger LOG = LogManager.getLogger(); //创建日志记录器

	private DBProperties(){
		
		//FileInputStream fis=new FileINputStream(new File("db.properties"));
		//类加载器,是一个类,这个类用于处理类路径下的一些操作
		InputStream iis=DBProperties.class.getClassLoader().getResourceAsStream(propertyFileName);
		try {
			load(iis);
		} catch (IOException e) {
			//e.printStackTrace();
			LOG.error(e);
			throw new RuntimeException(e);
		}
	}
	
	//确保单例
	public static DBProperties getInstance(String pfn){
		if( myproperties==null){
			if( pfn!=null && !"".equals(pfn)){
				propertyFileName=pfn;
			}
			myproperties=new DBProperties();
		}
		return myproperties;
	}
	
}
