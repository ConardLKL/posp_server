/**
 * 
 */
package com.bestpay.posp.spring;

import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;

/**
 * @author yihaijun
    	//创建一个数据源  
        DynamicLoadBean dynamicBeanLoad =(DynamicLoadBean)ctx.getBean("dynamicLoadBean");
        dataSourceFileName = "classpath:mybatis/posp-core-dataSource-config.xml";
        dynamicBeanLoad.loadBean(dataSourceFileName);   
        dataSource = ( com.jolbox.bonecp.BoneCPDataSource) ctx.getBean("pospDynamicdatasource");  

		log.info(dataSourceFileName + " dataSource.getDriverClass() = " +dataSource.getDriverClass());
		
		dataSourceFileName = "src/main/resources/mybatis/posp-core-dataSource-config.xml";
		//dataSourceFileName = "D:\\tisson\\yhj\\tmp\\posp\\bestpay-posp\\posp\\posp-core\\src\\main\\resources\\mybatis\\posp-core-dataSource-config.xml";
        dynamicBeanLoad.loadBean(dataSourceFileName);   
        dataSource = ( com.jolbox.bonecp.BoneCPDataSource) ctx.getBean("pospDynamicdatasource");  

		log.info(dataSourceFileName + " dataSource.getDriverClass() = " +dataSource.getDriverClass());
 */
public class DynamicDataSource  extends AbstractRoutingDataSource {  
   
    /*  
     * 该方法必须要重写  方法是为了根据数据库标示符取得当前的数据库 
     */  
    @Override  
    public Object determineCurrentLookupKey() {  
        return DataSourceContextHolder.getDataSourceName();  
    }  
   
    @Override  
    public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {  
        super.setDataSourceLookup(dataSourceLookup);  
    }  
   
    @Override  
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {  
        super.setDefaultTargetDataSource(defaultTargetDataSource);  
    }  
   
    @Override  
    public void setTargetDataSources(Map targetDataSources) {  
        super.setTargetDataSources(targetDataSources);  
        //重点  
        super.afterPropertiesSet();  
    }  
}
