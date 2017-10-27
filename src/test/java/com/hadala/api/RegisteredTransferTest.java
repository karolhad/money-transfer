package com.hadala.api;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.hadala.api.RegisteredTransfer.Status.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisteredTransferTest {

    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private RegisteredTransfer transfer;

    @Before
    public void setUp() {
        transfer = new RegisteredTransfer(new Transfer(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, AMOUNT));
    }

    @Test
    public void idIsNotEmpty() throws Exception {
        assertThat(transfer.getId()).isNotNull();
    }

    @Test
    public void getStatusAfterCreation() throws Exception {
        assertThat(transfer.getStatus()).isEqualTo(IN_PROGRESS);
    }
}