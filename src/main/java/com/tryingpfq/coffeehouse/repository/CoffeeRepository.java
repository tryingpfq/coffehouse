package com.tryingpfq.coffeehouse.repository;

import com.tryingpfq.coffeehouse.model.BaseEntity;
import com.tryingpfq.coffeehouse.model.Coffee;
import org.springframework.data.repository.CrudRepository;

/**
 * @Author Tryingpfq
 * @Time 2019/3/10 22:22
 */
public interface CoffeeRepository extends BaseRepository<Coffee,Long> {
}
