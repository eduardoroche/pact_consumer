package de.kreuzwerker.cdc.messagingapp;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import org.assertj.core.groups.Tuple;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = "user-service.base-url:http://localhost:${RANDOM_PORT}",
    classes = UserServiceClient.class)
public class UserServiceGenericStateWithParameterContractTest {

    private static final String NAME = "user name for CDC";
    private static final LocalDateTime LAST_LOGIN = LocalDateTime.of(2018, 10, 16, 12, 34, 12);

    @ClassRule
    public static RandomPortRule randomPort = new RandomPortRule();

    @Rule
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2("user-service", null,
        randomPort.getPort(), this);

    @Rule
    public ExpectedException expandException = ExpectedException.none();

    @Autowired
    private UserServiceClient userServiceClient;


    @Pact(consumer = "messaging-app")
    public RequestResponsePact pactUserExists(PactDslWithProvider builder) {
        System.out.println("kakaka");
        return builder.given(
                "User 1 exists")
                .uponReceiving("A request to /users/1")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonBody((o) ->
                        o.stringType("name", "user name for CDC")
                ).build()).toPact();
    }

    @PactVerification(fragment = "pactUserExists")
    @Test
    public void userExists() {
        System.out.println("abc");
        User user = userServiceClient.getUser("1");

        assertThat(user.getName()).isEqualTo("user name for CDC");
    }
}