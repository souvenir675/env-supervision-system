package com.neusoft.nep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neusoft.nep.dto.GridMemberRegisterDTO;
import com.neusoft.nep.dto.GridMemberStateUpdateDTO;
import com.neusoft.nep.entity.GridCity;
import com.neusoft.nep.entity.GridMember;
import com.neusoft.nep.entity.GridProvince;
import com.neusoft.nep.enums.GridMemberStateEnum;
import com.neusoft.nep.mapper.GridMemberMapper;
import com.neusoft.nep.mapper.GridProvinceMapper;
import com.neusoft.nep.mapper.GridCityMapper;
import com.neusoft.nep.service.GridMemberService;
import com.neusoft.nep.vo.GridMemberVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 网格员服务实现类
 */
@Service
public class GridMemberServiceImpl implements GridMemberService {

    @Autowired
    private GridMemberMapper gridMemberMapper;

    @Autowired
    private GridProvinceMapper gridProvinceMapper;

    @Autowired
    private GridCityMapper gridCityMapper;

    @Override
    public GridMember findByGmCode(String gmCode) {
        return gridMemberMapper.selectByGmCode(gmCode);
    }

    @Override
    public GridMember findByGmId(String gmId) {
        return gridMemberMapper.selectById(gmId);
    }

    @Override
    public GridMember login(String gmCode, String password) {
        GridMember gridMember = gridMemberMapper.selectByGmCode(gmCode);
        if (gridMember == null) {
            return null;
        }
        if (!gridMember.getPassword().equals(password)) {
            return null;
        }
        return gridMember;
    }

    @Override
    public List<GridMember> findAvailableByArea(Integer provinceId, Integer cityId) {
        return gridMemberMapper.selectAvailableByArea(provinceId, cityId);
    }

    @Override
    public List<GridMember> findAvailableByProvince(Integer provinceId) {
        return gridMemberMapper.selectAvailableByProvince(provinceId);
    }

    @Override
    public List<GridMember> findAllAvailable() {
        return gridMemberMapper.selectAllAvailable();
    }

    /**
     * 注册网格员（管理员操作）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(GridMemberRegisterDTO dto) {
        // 1. 检查手机号是否已存在
        GridMember existById = gridMemberMapper.selectById(dto.getGmId());
        if (existById != null) {
            throw new IllegalArgumentException("该手机号已被注册为网格员");
        }

        // 2. 检查登录编码是否已存在
        if (isGmCodeExist(dto.getGmCode())) {
            throw new IllegalArgumentException("该登录编码已存在，请更换");
        }

        // 3. 构建网格员对象
        GridMember gridMember = new GridMember();
        BeanUtils.copyProperties(dto, gridMember);
        if (gridMember.getState() == null) {
            gridMember.setState(0); // 默认可工作
        }

        int result = gridMemberMapper.insert(gridMember);
        System.out.println("网格员注册结果: " + (result > 0 ? "成功" : "失败") + ", gmId=" + dto.getGmId());
        return result > 0;
    }

    /**
     * 查询所有网格员
     */
    @Override
    public List<GridMember> findAll() {
        QueryWrapper<GridMember> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("gm_id");
        return gridMemberMapper.selectList(wrapper);
    }

    /**
     * 根据省份查询所有网格员
     */
    @Override
    public List<GridMember> findByProvince(Integer provinceId) {
        if (provinceId == null) {
            return findAll();
        }
        return gridMemberMapper.selectByProvince(provinceId);
    }

    /**
     * 删除网格员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String gmId) {
        if (gmId == null || gmId.isEmpty()) {
            return false;
        }
        int result = gridMemberMapper.deleteById(gmId);
        return result > 0;
    }

    /**
     * 检查登录编码是否存在
     */
    @Override
    public boolean isGmCodeExist(String gmCode) {
        GridMember member = gridMemberMapper.selectByGmCode(gmCode);
        return member != null;
    }

    /**
     * 修改网格员工作状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateState(GridMemberStateUpdateDTO dto) {
        if (dto.getGmId() == null || dto.getGmId().isEmpty()) {
            throw new IllegalArgumentException("网格员编号不能为空");
        }

        // 检查网格员是否存在
        GridMember member = gridMemberMapper.selectById(dto.getGmId());
        if (member == null) {
            throw new IllegalArgumentException("网格员不存在");
        }

        // 校验状态值
        if (dto.getState() == null || dto.getState() < 0 || dto.getState() > 3) {
            throw new IllegalArgumentException("状态值无效");
        }

        int result = gridMemberMapper.updateState(dto.getGmId(), dto.getState());
        System.out.println("更新网格员状态: gmId=" + dto.getGmId() +
                ", state=" + dto.getState() + ", 结果=" + (result > 0 ? "成功" : "失败"));
        return result > 0;
    }

    @Override
    public GridMemberVO convertToVO(GridMember gridMember) {
        if (gridMember == null) {
            return null;
        }
        GridMemberVO vo = new GridMemberVO();
        BeanUtils.copyProperties(gridMember, vo);

        // 设置状态描述
        GridMemberStateEnum stateEnum = GridMemberStateEnum.getByCode(gridMember.getState());
        if (stateEnum != null) {
            vo.setStateDesc(stateEnum.getDesc());
        }

        GridProvince p = gridProvinceMapper.selectById(gridMember.getProvinceId());
        if (p != null) {
            vo.setProvinceName(p.getProvinceName());
        }

        GridCity c = gridCityMapper.selectById(gridMember.getCityId());
        if (c != null) {
            vo.setCityName(c.getCityName());
        }

        return vo;
    }
}