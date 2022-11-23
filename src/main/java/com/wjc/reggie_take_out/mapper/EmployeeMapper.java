package com.wjc.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjc.reggie_take_out.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {



}
