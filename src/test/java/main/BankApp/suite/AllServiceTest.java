package main.BankApp.suite;

import main.BankApp.service.ContactsServiceTest;
import main.BankApp.service.HashingServiceImplTest;
import main.BankApp.service.RSAServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {ContactsServiceTest.class, HashingServiceImplTest.class, RSAServiceImplTest.class} )
public class AllServiceTest {
}
