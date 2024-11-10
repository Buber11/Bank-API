package main.BankApp.suite;

import main.BankApp.service.ContactsServiceTest;
import main.BankApp.service.HashingServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {ContactsServiceTest.class, HashingServiceImplTest.class, VaultServiceImplTest.class} )
public class AllServiceTest {
}
