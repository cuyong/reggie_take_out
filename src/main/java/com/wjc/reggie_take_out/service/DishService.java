package com.wjc.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjc.reggie_take_out.dto.DishDto;
import com.wjc.reggie_take_out.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品信息，同时删除对应的口味信息
    public void removeWithFlavors(List<Long> ids);

    //根据传入的status和ids起售或停售菜品
    public void changeSale(int status, List<Long> ids);
}
