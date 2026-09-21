package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.Supervisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公众监督员Mapper
 */
@Mapper
public interface SupervisorMapper extends BaseMapper<Supervisor> {

    /**
     * 根据手机号查询
     */
    @Select("SELECT * FROM supervisor WHERE tel_id = #{telId}")
    Supervisor selectByTelId(@Param("telId") String telId);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM supervisor WHERE tel_id = #{telId}")
    int countByTelId(@Param("telId") String telId);
}