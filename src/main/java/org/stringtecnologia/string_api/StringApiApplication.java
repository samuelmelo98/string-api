package org.stringtecnologia.string_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class StringApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StringApiApplication.class, args);
	}
	/*@Bean
	CommandLineRunner testConnection(DataSource dataSource) {
		return args -> {
			try (var conn = dataSource.getConnection()) {
				System.out.println("🔥 CONECTOU NO BANCO COM SUCESSO!");
			}
		};
	}*/
}
