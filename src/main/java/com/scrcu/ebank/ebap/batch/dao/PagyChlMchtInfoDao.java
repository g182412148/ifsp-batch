package com.scrcu.ebank.ebap.batch.dao;


import java.util.Date;

/**
 * @author: ljy
 * @create: 2018-10-26 21:52
 */
public interface PagyChlMchtInfoDao {
    void updateStateByChlMchtNo(String chlMchtNo, Date date);
}
