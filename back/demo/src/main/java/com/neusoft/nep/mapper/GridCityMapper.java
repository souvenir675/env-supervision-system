package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.GridCity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 网格城市Mapper
 */
@Mapper
public interface GridCityMapper extends BaseMapper<GridCity> {

    /**
     * 根据省份ID查询城市列表
     */
    @Select("SELECT * FROM grid_city WHERE province_id = #{provinceId} ORDER BY city_id")
    List<GridCity> selectByProvinceId(@Param("provinceId") Integer provinceId);

    /**
     * 查询已覆盖城市数量
     */
    @Select("SELECT COUNT(*) FROM grid_city WHERE covered = 1")
    int countCovered();

    /**
     * 查询总城市数量
     */
    @Select("SELECT COUNT(*) FROM grid_city")
    int countTotal();

    /**
     * 根据省份ID查询已覆盖城市数量
     */
    @Select("SELECT COUNT(*) FROM grid_city WHERE province_id = #{provinceId} AND covered = 1")
    int countCoveredByProvince(@Param("provinceId") Integer provinceId);
}