package com.wjc.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjc.reggie_take_out.dto.SetmealDto;
import com.wjc.reggie_take_out.entity.Setmeal;
import com.wjc.reggie_take_out.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询套餐的基本信息及其对应的菜品
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDishes(Long id);

    /**
     * 根据页面传入的setmealDto值来更新数据库，包括setmeal表和setmeal_dish表
     * @param setmealDto
     */
    public void updateWithDishes(SetmealDto setmealDto);

    /**
     * 根据传入的status和ids实现单个或批量的套餐停售起售
     * @param status
     * @param ids
     */
    public void changeSale(int status, List<Long> ids);

}
