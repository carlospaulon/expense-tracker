package com.carlos.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpensetrackerApplication {
    //TODO: cuidar gitignore
    //TODO: applicaiton.properties (melhor sintaxe - para projetos pequenos) ou YAML?
	public static void main(String[] args) {
		SpringApplication.run(ExpensetrackerApplication.class, args);
	}

}
