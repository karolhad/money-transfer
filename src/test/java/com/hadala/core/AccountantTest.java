package com.hadala.core;

import com.hadala.api.Account;
import com.hadala.api.RegisteredTransfer;
import com.hadala.api.Transfer;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.hadala.api.RegisteredTransfer.Status.COMPLETED;
import static com.hadala.api.RegisteredTransfer.Status.FAILED;
import static java.math.BigDecimal.valueOf;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MockitoJUnitRunner.class)
public class AccountantTest {
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private static final int UNKNOWN_ACCOUNT_ID = 3;
    private Accountant accountant;
    @Mock
    private AccountMemoryStore accountMemoryStore;
    @Mock
    private TransferAuditMemoryStore auditStore;
    private Account sourceAccount;
    private Account targetAccount;

    @Before
    public void setUp() throws Exception {
        accountant = new Accountant(auditStore, accountMemoryStore);
        sourceAccount = new Account(SOURCE_ACCOUNT_ID, valueOf(100.75));
        targetAccount = new Account(TARGET_ACCOUNT_ID, valueOf(30.23));

        when(accountMemoryStore.getAccountById(SOURCE_ACCOUNT_ID)).thenReturn(sourceAccount);
        when(accountMemoryStore.getAccountById(TARGET_ACCOUNT_ID)).thenReturn(targetAccount);
    }

    @Test
    public void successTransfer() {
        RegisteredTransfer transfer = new RegisteredTransfer(new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, valueOf(10.5)));

        accountant.transfer(transfer);

        assertThat(transfer.getStatus()).isEqualTo(COMPLETED);
        assertThat(sourceAccount.getBalance()).isEqualTo(BigDecimal.valueOf(90.25));
        assertThat(targetAccount.getBalance()).isEqualTo(BigDecimal.valueOf(40.73));
    }

    @Test
    public void notEnoughMoney() {
        RegisteredTransfer transfer = new RegisteredTransfer(new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, valueOf(100.76)));

        accountant.transfer(transfer);

        assertThat(transfer.getStatus()).isEqualTo(FAILED);
        assertThatBalancesDoNotChanged();
    }

    @Test
    public void invalidSourceAccount() {
        RegisteredTransfer transfer = new RegisteredTransfer(new Transfer(UNKNOWN_ACCOUNT_ID, TARGET_ACCOUNT_ID, valueOf(100.76)));

        accountant.transfer(transfer);

        assertThat(transfer.getStatus()).isEqualTo(FAILED);
        assertThatBalancesDoNotChanged();
    }

    @Test
    public void invalidTargetAccount() {
        RegisteredTransfer transfer = new RegisteredTransfer(new Transfer(UNKNOWN_ACCOUNT_ID, TARGET_ACCOUNT_ID, valueOf(100.76)));

        accountant.transfer(transfer);

        assertThat(transfer.getStatus()).isEqualTo(FAILED);
        assertThatBalancesDoNotChanged();
    }

    private void assertThatBalancesDoNotChanged() {
        assertThat(sourceAccount.getBalance()).isEqualTo(BigDecimal.valueOf(100.75));
        assertThat(targetAccount.getBalance()).isEqualTo(BigDecimal.valueOf(30.23));
    }
}