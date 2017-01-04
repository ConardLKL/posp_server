package com.bestpay.posp.system.base;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * 
 * @author DengPengHai
 * @date 2014-7-24
 * @param <T>
 * @param <PK>
 */
public interface BaseDao <T, PK extends Serializable> {
	
    int insert(final T entity) throws DataAccessException;
    
    int update(final T entity) throws DataAccessException;

    T findById(final PK id) throws DataAccessException;
    
    T findUnique(final T entity) throws DataAccessException;
    
    List<T> find(final T entity) throws DataAccessException;
    
    List<T> findAll() throws DataAccessException;
    
    int countBy(final T entity) throws DataAccessException;

}
