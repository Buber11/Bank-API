package main.BankApp;

import main.BankApp.suite.AllTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses(AllTest.class)
class BankAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
