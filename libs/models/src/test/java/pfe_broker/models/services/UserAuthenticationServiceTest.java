package pfe_broker.models.services;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import pfe_broker.models.domains.Account;
import pfe_broker.models.domains.Scope;
import pfe_broker.models.repositories.AccountRepository;

@MicronautTest(
  rollback = false,
  transactional = false,
  transactionMode = TransactionMode.SINGLE_TRANSACTION
)
@Property(
  name = "datasources.default.driver-class-name",
  value = "org.testcontainers.jdbc.ContainerDatabaseDriver"
)
@Property(
  name = "datasources.default.url",
  value = "jdbc:tc:postgresql:16.1:///db"
)
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthenticationServiceTest {

  @Inject
  private UserAuthenticationService userAuthenticationService;

  @Inject
  private AccountRepository userRepository;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
  }

  @Test
  void testEncode() {
    String encodedPassword = userAuthenticationService.encode("testpassword");
    Assertions.assertTrue(
      userAuthenticationService.matches("testpassword", encodedPassword)
    );
  }

  @Test
  void testMatchesPythonEncoded() {
    String encodedPassword =
      "$2b$12$udYE34mAnL6z5LAWD75Poue65xyGzZjNnTCC963JQkoKKB/I7y1Vm";
    Assertions.assertTrue(
      userAuthenticationService.matches("testpassword", encodedPassword)
    );
  }

  @Test
  void testAuthenticateUser() {
    Account user = new Account(
      "testuser",
      userAuthenticationService.encode("testpassword"),
      Scope.USER,
      true,
      1000.0
    );
    userRepository.save(user);

    boolean authenticated = userAuthenticationService.userAuthentication(
      "testuser",
      "testpassword"
    );

    Assertions.assertTrue(authenticated);
  }

  @Test
  void testAuthenticateUserWrongPassword() {
    Account user = new Account(
      "testuser",
      userAuthenticationService.encode("testpassword"),
      Scope.USER,
      true,
      1000.0
    );
    userRepository.save(user);

    boolean authenticated = userAuthenticationService.userAuthentication(
      "testuser",
      "wrongpassword"
    );

    Assertions.assertFalse(authenticated);
  }
}
