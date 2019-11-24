package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")

public class  OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO ){
        Long id = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryStateById(@PathVariable("id")Long id){
        return ResponseEntity.ok(orderService.queryStateById(id));
    }

    @GetMapping("url/{id}")
    public ResponseEntity<String> queryUrlById(@PathVariable("id")Long id){
        return ResponseEntity.ok(orderService.queryUrlById(id));
    }
}
