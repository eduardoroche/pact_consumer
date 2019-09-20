package de.kreuzwerker.cdc.messagingapp;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.amazonaws.services.kms.model.NotFoundException;
import io.pactfoundation.consumer.dsl.LambdaDsl;
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
import unfiltered.response.NotFound;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "user-service.base-url:http://localhost:${RANDOM_PORT}",
        classes = UserServiceClient.class)
public class UserServiceV1ContractTest {

    @ClassRule
    public static RandomPortRule randomPort = new RandomPortRule();

    @Rule
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2("user-service", null,
            randomPort.getPort(), this);

    @Autowired
    private UserServiceClient userServiceClient;

    @Rule
    public ExpectedException expandException = ExpectedException.none();


    @Pact(consumer = "messaging-app")
    public RequestResponsePact pactUserExists(PactDslWithProvider builder) {
        return builder.given(
                "User 3 exists")
                .uponReceiving("A request to /users/3")
                .path("/users/33")
                .method("GET")
                .willRespondWith()
                .status(200)
                 .status(404)
                 .toPact();
               // .body(LambdaDsl.newJsonBody((o) ->
               //         o.stringType("name", "user name for CDC")
               // ).build()).toPact();
    }

    @PactVerification(fragment = "pactUserExists")
    @Test
    public void userExists() {
        expandException.expect(HttpClientErrorException.class);
        expandException.expectMessage("404 Not Found");
        User user = userServiceClient.getUser("3");
    }

    @Pact(consumer = "messaging-app2")
    public RequestResponsePact pactUserExists2(PactDslWithProvider builder) {
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

    @PactVerification(fragment = "pactUserExists2")
    @Test
    public void userExists2() {
        User user = userServiceClient.getUser("1");

        assertThat(user.getName()).isEqualTo("user name for CDC");
    }

    @Pact(consumer = "messaging-app3")
    public RequestResponsePact pactUserExists3(PactDslWithProvider builder) {
        return builder.given(
                "Old User 1 exists")
                .uponReceiving("A request to /users/old/1")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
               .status(404)
               .toPact();
          //      .status(200)
            //    .body(LambdaDsl.newJsonBody((o) ->
              //          o.stringType("name", "user name for CDC")
                //).build()).toPact();
    }

    @PactVerification(fragment = "pactUserExists3")
    @Test
    public void userExists3() {
       expandException.expect(HttpClientErrorException.class);
       expandException.expectMessage("404 Not Found");
       User user = userServiceClient.getUserOldVersion("1");
        //assertThat(user.getName()).isEqualTo("user name for CDC");
    }
}