package com.tryingpfq.coffeehouse.controller;

import com.tryingpfq.coffeehouse.controller.domain.NewOrderRequest;
import com.tryingpfq.coffeehouse.model.Coffee;
import com.tryingpfq.coffeehouse.model.CoffeeOrder;
import com.tryingpfq.coffeehouse.service.CoffeeOrderService;
import com.tryingpfq.coffeehouse.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Tryingpfq
 * @Time 2019/3/20 22:03
 */

@RestController
@RequestMapping("/order")
@Slf4j
public class CoffeeOrderController {

    @Autowired
    private CoffeeOrderService orderService;
    @Autowired
    private CoffeeService coffeeService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeOrder create(@RequestBody NewOrderRequest newOrder) {
        log.info("Receive new Order {}", newOrder);
        Coffee[] coffeeList = coffeeService.getCoffeeByName(newOrder.getItems())
                .toArray(new Coffee[] {});
        return orderService.createOrder(newOrder.getCustomer(), coffeeList);
    }
}
