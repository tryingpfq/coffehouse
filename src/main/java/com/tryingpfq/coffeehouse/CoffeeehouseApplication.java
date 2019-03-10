package com.tryingpfq.coffeehouse;

import com.tryingpfq.coffeehouse.enums.OrderStatus;
import com.tryingpfq.coffeehouse.model.Coffee;
import com.tryingpfq.coffeehouse.model.CoffeeOrder;
import com.tryingpfq.coffeehouse.repository.CoffeeOrderRepository;
import com.tryingpfq.coffeehouse.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class CoffeeehouseApplication implements ApplicationRunner {
	@Autowired
	private CoffeeRepository coffeeRepository;

	@Autowired
	private CoffeeOrderRepository coffeeOrderRepository;

	public static void main(String[] args) {
		SpringApplication.run(CoffeeehouseApplication.class, args);
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		//initOrders();
		findOrders();
	}

	public void initOrders(){
		Coffee espresso = Coffee.builder().name("expresso")
							.price(Money.of(CurrencyUnit.of("CNY"),25)).build();

		//save a mune
		coffeeRepository.save(espresso);
		log.info("Coffee {}",espresso);

		//create a order with one item
		CoffeeOrder espressOder = CoffeeOrder.builder().customer("peng")
								.items(Collections.singletonList(espresso))
								.state(OrderStatus.INIT).build();
		//save a order
		coffeeOrderRepository.save(espressOder);
		log.info("one item order {}",espressOder);

		Coffee latte = Coffee.builder().name("latte")
					.price(Money.of(CurrencyUnit.of("CNY"),30)).build();
		// save the latte
		coffeeRepository.save(latte);
		log.info("latet {}",latte);

		CoffeeOrder oders = CoffeeOrder.builder().customer("peng1")
							.items(Arrays.asList(espresso,latte)).state(OrderStatus.BREWEND).build();
		// save the orders
		coffeeOrderRepository.save(oders);
		log.info("two items oders {}",oders);
	}

	private void findOrders(){
		coffeeOrderRepository.findAll(Sort.by(Sort.Direction.DESC,"id"))
							 .forEach(c -> log.info("loading all {}",c));

		List<CoffeeOrder> list = coffeeOrderRepository.findTop3ByOrderByUpdateTimeDescIdAsc();
		log.info("findTop3ByOder:{}",getJoinedOrderId(list));

		list = coffeeOrderRepository.findByCustomerOrderById("Li Lei");
		log.info("findByCustomerOrderById: {}", getJoinedOrderId(list));

		// 不开启事务会因为没Session而报LazyInitializationException
		list.forEach(o -> {
			log.info("Order {}", o.getId());
			o.getItems().forEach(i -> log.info("  Item {}", i));
		});

		list = coffeeOrderRepository.findByItems_Name("latte");
		log.info("findByItems_Name: {}", getJoinedOrderId(list));
	}

	private String getJoinedOrderId(List<CoffeeOrder> list) {
		return list.stream().map(o -> o.getId().toString())
				.collect(Collectors.joining(","));
	}
}