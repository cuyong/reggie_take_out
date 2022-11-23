package com.wjc.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjc.reggie_take_out.common.CustomException;
import com.wjc.reggie_take_out.dto.SetmealDto;
import com.wjc.reggie_take_out.entity.Dish;
import com.wjc.reggie_take_out.entity.Setmeal;
import com.wjc.reggie_take_out.entity.SetmealDish;
import com.wjc.reggie_take_out.mapper.SetmealMapper;
import com.wjc.reggie_take_out.service.SetmealDishService;
import com.wjc.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
       //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes =setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

       //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids){
        //查询套餐状态，确定是否可以删除
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        //如果不能删除，抛出一个业务异常
        if(count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        //删除关系表中的数据--setmeal_dish
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        setmealDishService.remove(lambdaQueryWrapper);

        //不能调用byids方法，因为传入的ids并不是setmeal_dish的主键值，可以采用queryWropper查询删除
        //setmealDishService.removeByIds()
    }

    /**
     * 根据id查询套餐的基本信息及其对应的菜品
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDishes(Long id){
        //查询套餐的基本信息，从setmeal表中查询
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询套餐的菜品信息，从setmeal_dish表中查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * 根据页面传入的setmealDto值来更新数据库，包括setmeal表和setmeal_dish表
     * @param setmealDto
     */
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto){
        //首先更新setmeal表中的基本信息
        this.updateById(setmealDto);

        //先清除setmeal_dish表中原有对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //再把新设置好的对应菜品添加进去
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }

    /**
     * 根据传入的status和ids实现单个或批量的套餐停售起售
     * @param status
     * @param ids
     */
    public void changeSale(int status, List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = this.list(queryWrapper);

        for(Setmeal setmeal : setmeals){
            if(status == 1)
                setmeal.setStatus(1);
            else
                setmeal.setStatus(0);
            this.updateById(setmeal);
        }
    }

}
