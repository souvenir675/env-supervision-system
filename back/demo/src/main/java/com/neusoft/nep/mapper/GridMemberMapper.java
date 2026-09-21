package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.GridMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 网格员Mapper
 */
@Mapper
public interface GridMemberMapper extends BaseMapper<GridMember> {

    /**
     * 根据登录编码查询
     */
    @Select("SELECT * FROM grid_member WHERE gm_code = #{gmCode}")
    GridMember selectByGmCode(@Param("gmCode") String gmCode);

    /**
     * 根据区域查询可工作的网格员
     */
    @Select("SELECT * FROM grid_member WHERE province_id = #{provinceId} AND city_id = #{cityId} AND state = 0")
    List<GridMember> selectAvailableByArea(@Param("provinceId") Integer provinceId,
                                           @Param("cityId") Integer cityId);

    /**
     * 根据省份查询可工作的网格员
     */
    @Select("SELECT * FROM grid_member WHERE province_id = #{provinceId} AND state = 0")
    List<GridMember> selectAvailableByProvince(@Param("provinceId") Integer provinceId);

    /**
     * 查询所有可工作的网格员
     */
    @Select("SELECT * FROM grid_member WHERE state = 0")
    List<GridMember> selectAllAvailable();

    /**
     * 根据省份查询所有网格员（不限状态）
     */
    @Select("SELECT * FROM grid_member WHERE province_id = #{provinceId}")
    List<GridMember> selectByProvince(@Param("provinceId") Integer provinceId);

    /**
     * 更新网格员工作状态
     */
    @Update("UPDATE grid_member SET state = #{state} WHERE gm_id = #{gmId}")
    int updateState(@Param("gmId") String gmId, @Param("state") Integer state);
}