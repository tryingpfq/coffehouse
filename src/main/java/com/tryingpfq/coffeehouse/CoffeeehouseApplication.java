package com.tryingpfq.coffeehouse;

import com.tryingpfq.coffeehouse.conventer.BytesToMoneyConverter;
import com.tryingpfq.coffeehouse.conventer.MoneyToBytesConverter;
import com.tryingpfq.coffeehouse.enums.OrderStatus;
import com.tryingpfq.coffeehouse.model.Coffee;
import com.tryingpfq.coffeehouse.model.CoffeeOrder;
import com.tryingpfq.coffeehouse.repository.CoffeeOrderRepository;
import com.tryingpfq.coffeehouse.repository.CoffeeRepository;
import com.tryingpfq.coffeehouse.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableRedisRepositories
public class CoffeeehouseApplication implements ApplicationRunner {
	@Autowired
	private CoffeeRepository coffeeRepository;

	@Autowired
	private CoffeeOrderRepository coffeeOrderRepository;

	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private JedisPoolConfig jedisPoolConfig;

	@Autowired
	private CoffeeService coffeeService;

	public static void main(String[] args) {
		SpringApplication.run(CoffeeehouseApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("redis")
	public JedisPoolConfig jedisPoolConfig(){
		return new JedisPoolConfig();
	}

	@Bean(destroyMethod = "close")
	public JedisPool jedisPool(@Value("${redis.host}") String host){
		return new JedisPool(jedisPoolConfig(),host);
	}

	@Bean
	public RedisCustomConversions redisCustomConversions() {
		return new RedisCustomConversions(
				Arrays.asList(new MoneyToBytesConverter(), new BytesToMoneyConverter()));
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		//initOrders();
		//findOrders();
		//redisTest();
		//cacheTest();
		redisCacheRepositoryTest();
	}

	private void redisCacheRepositoryTest(){
		Optional<Coffee> c = coffeeService.findSimpleCoffeeFromCache("latte");
		log.info("Coffee {}", c);

		for (int i = 0; i < 5; i++) {
			c = coffeeService.findSimpleCoffeeFromCache("latte");
		}

		log.info("Value from Redis: {}", c);
	}

	private void cacheTest() throws InterruptedException {
		log.info("Count: {}", coffeeService.findAllCoffee().size());
		for (int i = 0; i < 5; i++) {
			log.info("Reading from cache.");
			coffeeService.findAllCoffee();
		}
		Thread.sleep(5_000);
		log.info("Reading after refresh.");
		coffeeService.findAllCoffee().forEach(c -> log.info("Coffee {}", c.getName()));
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

	private void redisTest(){
		log.info(jedisPoolConfig.toString());

		try(Jedis jedis = jedisPool.getResource()){
			coffeeRepository.findAll().forEach(c -> {
				jedis.hset("coffeeMenu",c.getName(),Long.toString(c.getPrice().getAmountMinorLong()));
			});

			Map<String,String> menu = jedis.hgetAll("coffeeMenu");
			log.info("Menu:{}",menu);

			String price = jedis.hget("coffeeMenu","latte");
			log.info("latte price {}",price);
		}


	}


	private String getJoinedOrderId(List<CoffeeOrder> list) {
		return list.stream().map(o -> o.getId().toString())
				.collect(Collectors.joining(","));
	}


}
