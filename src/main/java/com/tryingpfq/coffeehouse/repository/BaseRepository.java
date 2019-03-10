package com.tryingpfq.coffeehouse.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @Author Tryingpfq
 * @Time 2019/3/10 23:19
 */
@NoRepositoryBean
public interface BaseRepository<T,Long> extends PagingAndSortingRepository<T,Long> {
    List<T> findTop3ByOrderByUpdateTimeDescIdAsc();
}
