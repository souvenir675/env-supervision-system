package com.neusoft.nep.service;

import com.neusoft.nep.dto.GridMemberRegisterDTO;
import com.neusoft.nep.dto.GridMemberStateUpdateDTO;
import com.neusoft.nep.entity.GridMember;
import com.neusoft.nep.vo.GridMemberVO;

import java.util.List;

/**
 * 网格员服务接口
 */
public interface GridMemberService {

    /**
     * 根据登录编码查询
     */
    GridMember findByGmCode(String gmCode);

    /**
     * 根据手机号查询
     */
    GridMember findByGmId(String gmId);

    /**
     * 验证登录
     */
    GridMember login(String gmCode, String password);

    /**
     * 根据区域查询可工作的网格员
     */
    List<GridMember> findAvailableByArea(Integer provinceId, Integer cityId);

    /**
     * 根据省份查询可工作的网格员
     */
    List<GridMember> findAvailableByProvince(Integer provinceId);

    /**
     * 查询所有可工作的网格员
     */
    List<GridMember> findAllAvailable();

    /**
     * 新增：注册网格员（管理员操作）
     */
    boolean register(GridMemberRegisterDTO dto);

    /**
     * 新增：查询所有网格员
     */
    List<GridMember> findAll();

    /**
     * 根据省份查询所有网格员
     */
    List<GridMember> findByProvince(Integer provinceId);

    /**
     * 新增：删除网格员
     */
    boolean delete(String gmId);

    /**
     * 新增：检查登录编码是否存在
     */
    boolean isGmCodeExist(String gmCode);

    /**
     * 新增：修改网格员工作状态
     */
    boolean updateState(GridMemberStateUpdateDTO dto);

    /**
     * 转换为视图对象
     */
    GridMemberVO convertToVO(GridMember gridMember);
}