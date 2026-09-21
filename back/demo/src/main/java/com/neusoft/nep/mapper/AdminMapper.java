package com.neusoft.nep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.nep.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统管理员Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据登录编号查询
     */
    @Select("SELECT * FROM admins WHERE admin_code = #{adminCode}")
    Admin selectByAdminCode(@Param("adminCode") String adminCode);
}