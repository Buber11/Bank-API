package main.BankApp;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {ContactRepositoryTest.class, ContactsServiceTest.class} )
public class AllContactTest {

}
