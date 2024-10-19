package main.BankApp.suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {AllRepositoryTest.class, AllServiceTest.class} )
public class AllTest {
}
