package main.BankApp.suite;

import main.BankApp.repository.AcitivityLogRepositoryTest;
import main.BankApp.repository.ContactRepositoryTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {AcitivityLogRepositoryTest.class, ContactRepositoryTest.class})
public class AllRepositoryTest {
}
