package com.example.demo;

import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/29 16:36
 * 为发射信号检验做的缓存
 */
@Mapper
public interface TempDao {
    @Select("insert into temp (outHex)values (#{outHex})")
    void save(String outHex);
    @Select("delete from temp where outHex=#{outHex}")
    void delete(String outHex);
}
