package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.GridProvince;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 网格省份Mapper
 */
@Mapper
public interface GridProvinceMapper extends BaseMapper<GridProvince> {

    /**
     * 查询所有省份
     */
    @Select("SELECT * FROM grid_province ORDER BY province_id")
    List<GridProvince> selectAllList();

    /**
     * 查询已覆盖省份数量
     */
    @Select("SELECT COUNT(*) FROM grid_province WHERE covered = 1")
    int countCovered();

    /**
     * 查询总省份数量
     */
    @Select("SELECT COUNT(*) FROM grid_province")
    int countTotal();
}