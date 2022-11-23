package com.wjc.reggie_take_out.dto;

import com.wjc.reggie_take_out.entity.Dish;
import com.wjc.reggie_take_out.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
