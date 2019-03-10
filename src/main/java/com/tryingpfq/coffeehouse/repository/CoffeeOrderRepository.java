package com.tryingpfq.coffeehouse.repository;

import com.tryingpfq.coffeehouse.model.BaseEntity;
import com.tryingpfq.coffeehouse.model.CoffeeOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @Author Tryingpfq
 * @Time 2019/3/10 22:23
 */
public interface CoffeeOrderRepository extends BaseRepository<CoffeeOrder,Long> {
    List<CoffeeOrder> findByCustomerOrderById(String customer);

    List<CoffeeOrder> findByItems_Name(String iName);

}
