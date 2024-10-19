package main.BankApp.suite;


import main.BankApp.repository.ContactRepositoryTest;
import main.BankApp.service.ContactsServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {ContactRepositoryTest.class, ContactsServiceTest.class} )
public class AllContactTest {

}
