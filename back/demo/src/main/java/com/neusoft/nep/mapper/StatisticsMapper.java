package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.entity.Statistics;
import com.neusoft.nep.vo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatisticsMapper extends BaseMapper<Statistics> {

    /**
     * 根据反馈ID查询统计数据
     */
    @Select("SELECT * FROM statistics WHERE af_id = #{afId} LIMIT 1")
    Statistics selectByAfId(@Param("afId") Integer afId);

    /**
     * 分页查询统计数据（带关联信息，支持条件筛选）
     */
    @Select("<script>" +
            "SELECT " +
            "    s.id AS id, " +
            "    s.af_id AS af_id, " +
            "    s.province_id AS province_id, " +
            "    gp.province_name AS province_name, " +
            "    s.city_id AS city_id, " +
            "    gc.city_name AS city_name, " +
            "    s.address AS address, " +
            "    s.aqi_id AS aqi_id, " +
            "    a.aqi_explain AS aqi_level_desc, " +
            "    a.color AS aqi_color, " +
            "    s.confirm_date AS confirm_date, " +
            "    s.confirm_time AS confirm_time, " +
            "    s.gm_id AS gm_id, " +
            "    gm.gm_name AS gm_name, " +
            "    gm.tel AS gm_tel, " +
            "    s.fd_id AS fd_id, " +
            "    sup.real_name AS fd_name, " +
            "    sup.tel_id AS fd_tel, " +
            "    s.information AS information, " +
            "    s.so2_value AS so2_value, " +
            "    s.so2_level AS so2_level, " +
            "    s.co_value AS co_value, " +
            "    s.co_level AS co_level, " +
            "    s.spm_value AS spm_value, " +
            "    s.spm_level AS spm_level, " +
            "    s.remarks AS remarks " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "LEFT JOIN grid_city gc ON s.city_id = gc.city_id " +
            "LEFT JOIN aqi a ON s.aqi_id = a.aqi_id " +
            "LEFT JOIN grid_member gm ON s.gm_id = gm.gm_id " +
            "LEFT JOIN supervisor sup ON s.fd_id = sup.tel_id " +
            "<where>" +
            "    <if test='provinceId != null'> AND s.province_id = #{provinceId} </if>" +
            "    <if test='cityId != null'> AND s.city_id = #{cityId} </if>" +
            "    <if test='aqiId != null'> AND s.aqi_id = #{aqiId} </if>" +
            "    <if test='startDate != null and startDate != \"\"'> AND s.confirm_date &gt;= #{startDate} </if>" +
            "    <if test='endDate != null and endDate != \"\"'> AND s.confirm_date &lt;= #{endDate} </if>" +
            "</where>" +
            "ORDER BY s.id ASC" +
            "</script>")
    @Results(id = "statisticsVOMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "af_id", property = "afId"),
            @Result(column = "province_id", property = "provinceId"),
            @Result(column = "province_name", property = "provinceName"),
            @Result(column = "city_id", property = "cityId"),
            @Result(column = "city_name", property = "cityName"),
            @Result(column = "address", property = "address"),
            @Result(column = "aqi_id", property = "aqiId"),
            @Result(column = "aqi_level_desc", property = "aqiLevelDesc"),
            @Result(column = "aqi_color", property = "aqiColor"),
            @Result(column = "confirm_date", property = "confirmDate"),
            @Result(column = "confirm_time", property = "confirmTime"),
            @Result(column = "gm_id", property = "gmId"),
            @Result(column = "gm_name", property = "gmName"),
            @Result(column = "gm_tel", property = "gmTel"),
            @Result(column = "fd_id", property = "fdId"),
            @Result(column = "fd_name", property = "fdName"),
            @Result(column = "fd_tel", property = "fdTel"),
            @Result(column = "information", property = "information"),
            @Result(column = "so2_value", property = "so2Value"),
            @Result(column = "so2_level", property = "so2Level"),
            @Result(column = "co_value", property = "coValue"),
            @Result(column = "co_level", property = "coLevel"),
            @Result(column = "spm_value", property = "spmValue"),
            @Result(column = "spm_level", property = "spmLevel"),
            @Result(column = "remarks", property = "remarks")
    })
    Page<StatisticsVO> selectPageWithDetail(
            Page<StatisticsVO> page,
            @Param("provinceId") Integer provinceId,
            @Param("cityId") Integer cityId,
            @Param("aqiId") Integer aqiId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 查询所有统计数据（带关联信息）
     */
    @Select("SELECT " +
            "    s.id AS id, " +
            "    s.af_id AS af_id, " +
            "    s.province_id AS province_id, " +
            "    gp.province_name AS province_name, " +
            "    s.city_id AS city_id, " +
            "    gc.city_name AS city_name, " +
            "    s.address AS address, " +
            "    s.aqi_id AS aqi_id, " +
            "    a.aqi_explain AS aqi_level_desc, " +
            "    a.color AS aqi_color, " +
            "    s.confirm_date AS confirm_date, " +
            "    s.confirm_time AS confirm_time, " +
            "    s.gm_id AS gm_id, " +
            "    gm.gm_name AS gm_name, " +
            "    gm.tel AS gm_tel, " +
            "    s.fd_id AS fd_id, " +
            "    sup.real_name AS fd_name, " +
            "    sup.tel_id AS fd_tel, " +
            "    s.information AS information, " +
            "    s.so2_value AS so2_value, " +
            "    s.so2_level AS so2_level, " +
            "    s.co_value AS co_value, " +
            "    s.co_level AS co_level, " +
            "    s.spm_value AS spm_value, " +
            "    s.spm_level AS spm_level, " +
            "    s.remarks AS remarks " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "LEFT JOIN grid_city gc ON s.city_id = gc.city_id " +
            "LEFT JOIN aqi a ON s.aqi_id = a.aqi_id " +
            "LEFT JOIN grid_member gm ON s.gm_id = gm.gm_id " +
            "LEFT JOIN supervisor sup ON s.fd_id = sup.tel_id " +
            "ORDER BY s.id ASC")
    @ResultMap("statisticsVOMap")
    List<StatisticsVO> selectAllWithDetail();

    /**
     * 根据ID查询统计数据（带关联信息）
     */
    @Select("SELECT " +
            "    s.id AS id, " +
            "    s.af_id AS af_id, " +
            "    s.province_id AS province_id, " +
            "    gp.province_name AS province_name, " +
            "    s.city_id AS city_id, " +
            "    gc.city_name AS city_name, " +
            "    s.address AS address, " +
            "    s.aqi_id AS aqi_id, " +
            "    a.aqi_explain AS aqi_level_desc, " +
            "    a.color AS aqi_color, " +
            "    s.confirm_date AS confirm_date, " +
            "    s.confirm_time AS confirm_time, " +
            "    s.gm_id AS gm_id, " +
            "    gm.gm_name AS gm_name, " +
            "    gm.tel AS gm_tel, " +
            "    s.fd_id AS fd_id, " +
            "    sup.real_name AS fd_name, " +
            "    sup.tel_id AS fd_tel, " +
            "    s.information AS information, " +
            "    s.so2_value AS so2_value, " +
            "    s.so2_level AS so2_level, " +
            "    s.co_value AS co_value, " +
            "    s.co_level AS co_level, " +
            "    s.spm_value AS spm_value, " +
            "    s.spm_level AS spm_level, " +
            "    s.remarks AS remarks " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "LEFT JOIN grid_city gc ON s.city_id = gc.city_id " +
            "LEFT JOIN aqi a ON s.aqi_id = a.aqi_id " +
            "LEFT JOIN grid_member gm ON s.gm_id = gm.gm_id " +
            "LEFT JOIN supervisor sup ON s.fd_id = sup.tel_id " +
            "WHERE s.id = #{id}")
    @ResultMap("statisticsVOMap")
    StatisticsVO selectDetailById(@Param("id") Integer id);

    // ==================== 统计方法 ====================

    @Select("SELECT " +
            "    s.province_id, " +
            "    gp.province_name, " +
            "    SUM(CASE WHEN s.so2_level >= 3 THEN 1 ELSE 0 END) AS so2_exceed_count, " +
            "    SUM(CASE WHEN s.co_level >= 3 THEN 1 ELSE 0 END) AS co_exceed_count, " +
            "    SUM(CASE WHEN s.spm_level >= 3 THEN 1 ELSE 0 END) AS spm_exceed_count, " +
            "    SUM(CASE WHEN s.aqi_id >= 3 THEN 1 ELSE 0 END) AS aqi_exceed_count, " +
            "    COUNT(*) AS total_count " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "GROUP BY s.province_id, gp.province_name " +
            "ORDER BY aqi_exceed_count DESC")
    List<ProvinceStatisticsVO> selectProvinceStatistics();

    @Select("SELECT " +
            "    s.province_id, " +
            "    gp.province_name, " +
            "    SUM(CASE WHEN s.so2_level >= 3 THEN 1 ELSE 0 END) AS so2_exceed_count, " +
            "    SUM(CASE WHEN s.co_level >= 3 THEN 1 ELSE 0 END) AS co_exceed_count, " +
            "    SUM(CASE WHEN s.spm_level >= 3 THEN 1 ELSE 0 END) AS spm_exceed_count, " +
            "    SUM(CASE WHEN s.aqi_id >= 3 THEN 1 ELSE 0 END) AS aqi_exceed_count, " +
            "    COUNT(*) AS total_count " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "WHERE s.confirm_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY s.province_id, gp.province_name " +
            "ORDER BY aqi_exceed_count DESC")
    List<ProvinceStatisticsVO> selectProvinceStatisticsByDate(@Param("startDate") String startDate,
                                                              @Param("endDate") String endDate);

    @Select("SELECT " +
            "    s.aqi_id, " +
            "    a.aqi_explain, " +
            "    a.color, " +
            "    COUNT(*) AS count, " +
            "    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM statistics), 2) AS percentage " +
            "FROM statistics s " +
            "LEFT JOIN aqi a ON s.aqi_id = a.aqi_id " +
            "GROUP BY s.aqi_id, a.aqi_explain, a.color " +
            "ORDER BY s.aqi_id")
    List<AqiDistributionVO> selectAqiDistribution();

    @Select("SELECT " +
            "    DATE_FORMAT(STR_TO_DATE(confirm_date, '%Y-%m-%d'), '%Y-%m') AS month, " +
            "    SUM(CASE WHEN aqi_id >= 3 THEN 1 ELSE 0 END) AS exceed_count " +
            "FROM statistics " +
            "GROUP BY DATE_FORMAT(STR_TO_DATE(confirm_date, '%Y-%m-%d'), '%Y-%m') " +
            "ORDER BY month ASC")
    List<TrendStatisticsVO> selectTrendStatistics();

    @Select("SELECT " +
            "    COUNT(*) AS total_count, " +
            "    SUM(CASE WHEN aqi_id <= 2 THEN 1 ELSE 0 END) AS good_count, " +
            "    SUM(CASE WHEN aqi_id >= 3 THEN 1 ELSE 0 END) AS exceed_count " +
            "FROM statistics")
    List<RealTimeStatisticsVO> selectRealTimeStatistics();

    /**
     * 城市分组统计（按省份筛选）
     */
    @Select("SELECT " +
            "    s.city_id, " +
            "    gc.city_name, " +
            "    s.province_id, " +
            "    SUM(CASE WHEN s.so2_level >= 3 THEN 1 ELSE 0 END) AS so2_exceed_count, " +
            "    SUM(CASE WHEN s.co_level >= 3 THEN 1 ELSE 0 END) AS co_exceed_count, " +
            "    SUM(CASE WHEN s.spm_level >= 3 THEN 1 ELSE 0 END) AS spm_exceed_count, " +
            "    SUM(CASE WHEN s.aqi_id >= 3 THEN 1 ELSE 0 END) AS aqi_exceed_count, " +
            "    COUNT(*) AS total_count " +
            "FROM statistics s " +
            "LEFT JOIN grid_city gc ON s.city_id = gc.city_id " +
            "WHERE s.province_id = #{provinceId} " +
            "GROUP BY s.city_id, gc.city_name, s.province_id " +
            "ORDER BY aqi_exceed_count DESC")
    List<CityStatisticsVO> selectCityStatisticsByProvince(@Param("provinceId") Integer provinceId);

    /**
     * 根据省份和城市查询监测点明细
     */
    @Select("SELECT " +
            "    s.id AS id, " +
            "    s.af_id AS af_id, " +
            "    s.province_id AS province_id, " +
            "    gp.province_name AS province_name, " +
            "    s.city_id AS city_id, " +
            "    gc.city_name AS city_name, " +
            "    s.address AS address, " +
            "    s.aqi_id AS aqi_id, " +
            "    a.aqi_explain AS aqi_level_desc, " +
            "    a.color AS aqi_color, " +
            "    s.confirm_date AS confirm_date, " +
            "    s.confirm_time AS confirm_time, " +
            "    s.gm_id AS gm_id, " +
            "    gm.gm_name AS gm_name, " +
            "    gm.tel AS gm_tel, " +
            "    s.fd_id AS fd_id, " +
            "    sup.real_name AS fd_name, " +
            "    sup.tel_id AS fd_tel, " +
            "    s.information AS information, " +
            "    s.so2_value AS so2_value, " +
            "    s.so2_level AS so2_level, " +
            "    s.co_value AS co_value, " +
            "    s.co_level AS co_level, " +
            "    s.spm_value AS spm_value, " +
            "    s.spm_level AS spm_level, " +
            "    s.remarks AS remarks " +
            "FROM statistics s " +
            "LEFT JOIN grid_province gp ON s.province_id = gp.province_id " +
            "LEFT JOIN grid_city gc ON s.city_id = gc.city_id " +
            "LEFT JOIN aqi a ON s.aqi_id = a.aqi_id " +
            "LEFT JOIN grid_member gm ON s.gm_id = gm.gm_id " +
            "LEFT JOIN supervisor sup ON s.fd_id = sup.tel_id " +
            "WHERE s.province_id = #{provinceId} AND s.city_id = #{cityId} " +
            "ORDER BY s.aqi_id DESC, s.confirm_date DESC")
    @ResultMap("statisticsVOMap")
    List<StatisticsVO> selectMonitorPointsByCity(@Param("provinceId") Integer provinceId,
                                                 @Param("cityId") Integer cityId);
}