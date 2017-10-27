package com.hadala.integration;

import com.hadala.MoneyTransferApplication;
import com.hadala.MoneyTransferConfiguration;
import com.hadala.api.Account;
import com.hadala.api.RegisteredTransfer;
import com.hadala.api.Transfer;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MoneyTransferApplicationIntegrationTest {

    @Rule
    public final DropwizardAppRule<MoneyTransferConfiguration> RULE =
            new DropwizardAppRule<>(MoneyTransferApplication.class, ResourceHelpers.resourceFilePath("test-config.yml"));
    private static final String URL_TEMPLATE = "http://localhost:%d";
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private JerseyClient client;

    @Before
    public void setUp() {
        client = new JerseyClientBuilder().build();
    }

    @Test
    public void transferMoney() throws InterruptedException {
        RegisteredTransfer addTransferResponse = addTransfer(client, new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.valueOf(14.50)));

        RegisteredTransfer getTransferResponse = getTransferById(client, addTransferResponse.getId());

        while (getTransferResponse.getStatus() == RegisteredTransfer.Status.IN_PROGRESS) {
            Thread.sleep(100);
            getTransferResponse = getTransferById(client, addTransferResponse.getId());
        }

        assertThat(getTransferResponse.getStatus()).isEqualTo(RegisteredTransfer.Status.COMPLETED);

        Account sourceAccount = getAccount(client, SOURCE_ACCOUNT_ID);

        assertThat(sourceAccount.getBalance()).isEqualTo(BigDecimal.valueOf(9985.50));

        Account targetAccount = getAccount(client, TARGET_ACCOUNT_ID);

        assertThat(targetAccount.getBalance()).isEqualTo(BigDecimal.valueOf(264.5));
    }

    @Test
    public void transferMoney10KTimes() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        Transfer transfer = new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.valueOf(1.0));
        List<Callable<RegisteredTransfer>> callableList = new LinkedList<>();
        for (int i = 0; i < 10000; i++) {
            callableList.add(() -> addTransfer(client, transfer));
        }
        executorService.invokeAll(callableList);

        assertThat(getAccount(client, SOURCE_ACCOUNT_ID).getBalance()).isEqualTo(BigDecimal.valueOf(0.0));
        assertThat(getAccount(client, TARGET_ACCOUNT_ID).getBalance()).isEqualTo(BigDecimal.valueOf(10250.0));
    }

    private Account getAccount(Client client, int accountId) {
        Response response = client.target(String.format(URL_TEMPLATE + "/accounts/" + accountId, RULE.getLocalPort())).request().get();

        assertThat(response.getStatus()).isEqualTo(200);

        return response.readEntity(Account.class);
    }

    private RegisteredTransfer addTransfer(Client client, Transfer transfer) {
        Response createResponse = client.target(String.format(URL_TEMPLATE + "/transfers", RULE.getLocalPort())).
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        assertThat(createResponse.getStatus()).isEqualTo(200);

        return createResponse.readEntity(RegisteredTransfer.class);
    }

    private RegisteredTransfer getTransferById(Client client, UUID transferId) {
        Response response = client.target(String.format(URL_TEMPLATE + "/transfers/" + transferId, RULE.getLocalPort())).request().get();

        assertThat(response.getStatus()).isEqualTo(200);

        return response.readEntity(RegisteredTransfer.class);
    }
}
