package com.bestpay.posp.system.dao;

import java.util.List;

import com.bestpay.posp.system.entity.TPospPktDef;
import com.bestpay.posp.system.entity.TPospPktDefExample;
import org.apache.ibatis.annotations.Param;

public interface TPospPktDefDao {
	
    int countByExample(TPospPktDefExample example);

    int deleteByExample(TPospPktDefExample example);

    int insert(TPospPktDef record);

    int insertSelective(TPospPktDef record);

    List<TPospPktDef> selectByExample(TPospPktDefExample example);

    int updateByExampleSelective(@Param("record") TPospPktDef record, @Param("example") TPospPktDefExample example);

    int updateByExample(@Param("record") TPospPktDef record, @Param("example") TPospPktDefExample example);
    
    List<TPospPktDef> selectExampleGroupBy(TPospPktDefExample example);
    
}