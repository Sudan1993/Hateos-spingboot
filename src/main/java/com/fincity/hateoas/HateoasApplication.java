package com.fincity.hateoas;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;

@SpringBootApplication
public class HateoasApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HateoasApplication.class, args);
	}

	@Autowired
	private DataSource dataSource;

	/**
	 * Execute the sql script
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {

		String script = new ClassPathResource("data.sql").getPath();
		ScriptRunner scriptRunner = new ScriptRunner(dataSource.getConnection());
		scriptRunner.runScript(new BufferedReader(new FileReader(script)));
	}
}
