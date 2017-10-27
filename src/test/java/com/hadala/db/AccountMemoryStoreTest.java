package com.hadala.db;

import com.hadala.api.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class AccountMemoryStoreTest {

    private AccountMemoryStore accountMemoryStore;
    private Account account1;
    private Account account2;
    private List<Account> accounts;

    @Before
    public void setUp() throws Exception {
        accountMemoryStore = new AccountMemoryStore();
        account1 = new Account(1, BigDecimal.valueOf(100));
        account2 = new Account(2, BigDecimal.valueOf(200));
        accounts = Arrays.asList(account1, account2);
    }

    @Test
    public void getAccounts() {
        accountMemoryStore.addAll(accounts);

        assertThat(accountMemoryStore.getAccounts()).contains(account1, account2);
        assertThat(accountMemoryStore.getAccounts()).hasSize(2);
    }

    @Test
    public void getAccountById() {
        accountMemoryStore.addAll(accounts);

        assertThat(accountMemoryStore.getAccountById(2)).isEqualTo(account2);
    }

}