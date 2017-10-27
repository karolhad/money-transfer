package com.hadala.resources;

import com.hadala.api.Account;
import com.hadala.db.AccountMemoryStore;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {

    private static AccountMemoryStore accountMemoryStore = mock(AccountMemoryStore.class);
    private Account account1 = new Account(1, BigDecimal.ONE);
    private Account account2 = new Account(2, BigDecimal.TEN);

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountResource(accountMemoryStore))
            .build();

    @Test
    public void getAccounts() {
        List<Account> accounts = Arrays.asList(account1, account2);
        when(accountMemoryStore.getAccounts()).thenReturn(accounts);

        List<Account> response = resources.target("/accounts").request().get(new GenericType<List<Account>>(){});

        assertThat(response).isEqualTo(accounts);
    }

    @Test
    public void getAccountById() {
        when(accountMemoryStore.getAccountById(1)).thenReturn(account1);

        Account response = resources.target("/accounts/1").request().get(Account.class);

        assertThat(response).isEqualTo(account1);
    }

}