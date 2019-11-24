package com.leyou.thy.web;

import com.leyou.item.pojo.*;
import com.leyou.thy.clients.*;
import com.leyou.thy.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ItemController {
    @Autowired
    private GoodsService goodsService;


    @GetMapping("item/{id}.html")
    public String itemPage(@PathVariable("id")Long id , Model model) {
        Map <String, Object> map = this.goodsService.loadMode(id);
        model.addAllAttributes(map);
        return " item";
    }
}
