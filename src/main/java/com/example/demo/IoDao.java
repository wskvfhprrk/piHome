package com.example.demo;

import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/29 11:12
 */
@Mapper
public interface IoDao {
    @Select("select id,openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex from piHome")
    List<IoEntity> selectAll();

    @Select("select id,openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex from piHome WHERE piHome.openInHex = #{openInHex}")
    IoEntity selectByOpenInHex(String openInHex);

    @Select("INSERT INTO piHome (openInHex, closeInHex, groupName, switchName, status, openOutHex, closeOutHex, openInHex) " +
            "VALUES (#{openInHex}, #{closeInHex}, #{groupName}, #{switchName}, #{status}, #{openOutHex}, #{closeOutHex}, #{openInHex})")
    void save(IoEntity entity);

    @Select("DELETE FROM piHome WHERE openInHex =#{openInHex}")
    void delete(String openInHex);

    @Select("DELETE FROM piHome")
    void deleteAll();

    @Select("UPDATE piHome SET status = #{status} WHERE piHome.openInHex = #{openInHex}")
    void updateStatus(IoEntity ioEntity);
}
