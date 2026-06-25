package org.stringtecnologia.string_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class StringApiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Bean
	CommandLineRunner testeRedis(StringRedisTemplate redisTemplate) {
		return args -> {
			redisTemplate.opsForValue().set("teste", "ok");
			System.out.println(redisTemplate.opsForValue().get("teste"));
		};
	}

}
