package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.Aqi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AQI等级标准Mapper
 */
@Mapper
public interface AqiMapper extends BaseMapper<Aqi> {

    /**
     * 查询所有AQI等级
     */
    @Select("SELECT * FROM aqi ORDER BY aqi_id")
    List<Aqi> selectAllList();
}