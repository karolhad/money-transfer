package com.hadala.resources;

import com.hadala.api.Account;
import com.hadala.api.RegisteredTransfer;
import com.hadala.api.Transfer;
import com.hadala.core.AsyncAccountant;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransferResourceTest {

    private static final UUID ID1 = UUID.randomUUID();
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private Account account1 = new Account(SOURCE_ACCOUNT_ID, BigDecimal.ONE);
    private Account account2 = new Account(TARGET_ACCOUNT_ID, BigDecimal.TEN);
    private AsyncAccountant accountant = mock(AsyncAccountant.class);
    private TransferAuditMemoryStore transferAuditMemoryStore = mock(TransferAuditMemoryStore.class);
    private AccountMemoryStore accountMemoryStore = mock(AccountMemoryStore.class);

    @Captor
    private ArgumentCaptor<Transfer> transferCaptor;

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransferResource(accountant, transferAuditMemoryStore, accountMemoryStore))
            .build();
    private Transfer transfer;
    private RegisteredTransfer registeredTransfer;

    @Before
    public void setup() {
        transfer = new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.TEN);
        registeredTransfer = new RegisteredTransfer(transfer);
    }

    @Test
    public void addTransfer() throws Exception {

        when(accountant.registerTransfer(transfer)).thenReturn(registeredTransfer);

        Response response = resources.target("/transfers").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(accountant).registerTransfer(transferCaptor.capture());
        assertThat(transferCaptor.getValue()).isEqualTo(transfer);
    }

    @Test
    public void negativeAmount() throws Exception {
        transfer = new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.valueOf(-1.01));
        registeredTransfer = new RegisteredTransfer(transfer);
        when(accountant.registerTransfer(transfer)).thenReturn(registeredTransfer);

        Response response = resources.target("/transfers").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(422);
        verifyZeroInteractions(accountant);
    }

    @Test
    public void zeroAmount() throws Exception {
        transfer = new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.valueOf(0));
        registeredTransfer = new RegisteredTransfer(transfer);
        when(accountant.registerTransfer(transfer)).thenReturn(registeredTransfer);

        Response response = resources.target("/transfers").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(422);
        verifyZeroInteractions(accountant);
    }

    @Test
    public void tooSmallFraction() throws Exception {
        transfer = new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, BigDecimal.valueOf(1.009));
        registeredTransfer = new RegisteredTransfer(transfer);
        when(accountant.registerTransfer(transfer)).thenReturn(registeredTransfer);

        Response response = resources.target("/transfers").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(422);
        verifyZeroInteractions(accountant);
    }

    @Test
    public void getTransferFound() throws Exception {
        when(transferAuditMemoryStore.getTransferById(ID1)).thenReturn(Optional.of(registeredTransfer));

        RegisteredTransfer response = resources.target("/transfers/" + ID1).request().get(RegisteredTransfer.class);

        assertThat(response).isEqualTo(registeredTransfer);
    }

    @Test
    public void getTransferNotFound() throws Exception {
        when(transferAuditMemoryStore.getTransferById(ID1)).thenReturn(Optional.empty());

        Response.StatusType status = resources.target("/transfers/" + ID1).request().get().getStatusInfo();

        assertThat(status).isEqualTo(Response.Status.NOT_FOUND);
    }

    @Test
    public void getSourceAccount() throws Exception {
        when(transferAuditMemoryStore.getTransferById(ID1)).thenReturn(Optional.of(registeredTransfer));
        when(accountMemoryStore.getAccountById(SOURCE_ACCOUNT_ID)).thenReturn(account1);

        Account response = resources.target("/transfers/" + ID1 + "/source-account").request().get(Account.class);

        assertThat(response).isEqualTo(account1);
    }

    @Test
    public void getTargetAccount() throws Exception {
        when(transferAuditMemoryStore.getTransferById(ID1)).thenReturn(Optional.of(registeredTransfer));
        when(accountMemoryStore.getAccountById(TARGET_ACCOUNT_ID)).thenReturn(account2);

        Account response = resources.target("/transfers/" + ID1 + "/target-account").request().get(Account.class);

        assertThat(response).isEqualTo(account2);
    }

}