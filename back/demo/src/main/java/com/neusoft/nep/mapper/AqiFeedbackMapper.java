package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.AqiFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AqiFeedbackMapper extends BaseMapper<AqiFeedback> {

    @Select("SELECT * FROM aqi_feedback WHERE tel_id = #{telId} ORDER BY af_date DESC, af_time DESC")
    List<AqiFeedback> selectByTelId(@Param("telId") String telId);

    @Select("SELECT * FROM aqi_feedback WHERE state = #{state} ORDER BY af_date DESC, af_time DESC")
    List<AqiFeedback> selectByState(@Param("state") Integer state);

    @Update("UPDATE aqi_feedback SET state = #{state}, gm_id = #{gmId}, " +
            "assign_date = #{assignDate}, assign_time = #{assignTime} " +
            "WHERE af_id = #{afId}")
    int updateStateAndAssign(@Param("afId") Integer afId,
                             @Param("state") Integer state,
                             @Param("gmId") String gmId,
                             @Param("assignDate") String assignDate,
                             @Param("assignTime") String assignTime);

    @Select("SELECT * FROM aqi_feedback WHERE gm_id = #{gmId} AND state IN (1, 2) " +
            "ORDER BY state ASC, assign_date DESC, assign_time DESC")
    List<AqiFeedback> selectByGridMember(@Param("gmId") String gmId);

    @Select("SELECT * FROM aqi_feedback WHERE gm_id = #{gmId} AND state = 1 " +
            "ORDER BY assign_date DESC, assign_time DESC")
    List<AqiFeedback> selectPendingByGridMember(@Param("gmId") String gmId);

    @Update("UPDATE aqi_feedback SET province_id = #{provinceId}, city_id = #{cityId}, " +
            "address = #{address}, information = #{information}, estimated_grade = #{estimatedGrade} " +
            "WHERE af_id = #{afId} AND tel_id = #{telId} AND state = 0")
    int updateFeedback(@Param("afId") Integer afId,
                       @Param("telId") String telId,
                       @Param("provinceId") Integer provinceId,
                       @Param("cityId") Integer cityId,
                       @Param("address") String address,
                       @Param("information") String information,
                       @Param("estimatedGrade") Integer estimatedGrade);

    @org.apache.ibatis.annotations.Delete("DELETE FROM aqi_feedback WHERE af_id = #{afId} AND tel_id = #{telId} AND state = 0")
    int deleteFeedback(@Param("afId") Integer afId, @Param("telId") String telId);
}