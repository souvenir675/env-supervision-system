package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.DecisionMaker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 决策者Mapper
 */
@Mapper
public interface DecisionMakerMapper extends BaseMapper<DecisionMaker> {

    /**
     * 根据登录编号查询
     */
    @Select("SELECT * FROM decision_maker WHERE dm_code = #{dmCode}")
    DecisionMaker selectByDmCode(@Param("dmCode") String dmCode);

    /**
     * 更新最后登录时间
     */
    @Select("UPDATE decision_maker SET last_login_date = #{loginDate}, last_login_time = #{loginTime} WHERE dm_id = #{dmId}")
    Integer updateLoginTime(@Param("dmId") Integer dmId,
                        @Param("loginDate") String loginDate,
                        @Param("loginTime") String loginTime);
}