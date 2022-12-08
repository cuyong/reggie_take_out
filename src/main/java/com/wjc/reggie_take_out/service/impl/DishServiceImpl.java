package com.wjc.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjc.reggie_take_out.common.CustomException;
import com.wjc.reggie_take_out.dto.DishDto;
import com.wjc.reggie_take_out.entity.Dish;
import com.wjc.reggie_take_out.entity.DishFlavor;
import com.wjc.reggie_take_out.mapper.DishMapper;
import com.wjc.reggie_take_out.service.DishFlavorService;
import com.wjc.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品的口味信息到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * //根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper); //删除应该来说必须是通过主键删除~

        //添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    //删除菜品信息，同时删除对应的口味信息，
    // *******且应该实现，删除后对应包含该菜品的套餐也不能起售
    public void removeWithFlavors(List<Long> ids){
        //查询菜品状态，如果正在售卖不能删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);

        if(count > 0){
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        //如果可以删除，先删除菜品表中的数据--dish
        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId, ids);

        //在删除菜品对应的口味--dish_flavor
        dishFlavorService.remove(lambdaQueryWrapper);
    }

    /**
     * 根据传入的ids起售菜品
     */
    public void changeSale(int status, List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        List<Dish> dishes = this.list(queryWrapper);

        //Lambda表达式不行 不知道为啥
//        dishes.stream().map((item)->{
//            item.setStatus(1);
//            this.updateById(item);
//            return item;
//        });

        for(Dish dish : dishes){
            if(status == 1)
                dish.setStatus(1);
            else
                dish.setStatus(0);
            this.updateById(dish);
        }

    }
}
