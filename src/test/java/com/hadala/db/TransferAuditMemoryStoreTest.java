package com.hadala.db;

import com.hadala.api.RegisteredTransfer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferAuditMemoryStoreTest {

    private static final UUID ID1 = UUID.randomUUID();
    private static final UUID ID2 = UUID.randomUUID();

    @Mock private RegisteredTransfer transfer1;

    private TransferAuditMemoryStore transferAuditMemoryStore;

    @Before
    public void setUp() throws Exception {
        transferAuditMemoryStore = new TransferAuditMemoryStore();
        when(transfer1.getId()).thenReturn(ID1);
        transferAuditMemoryStore.audit(transfer1);
    }

    @Test
    public void getTransferById() {
        Optional<RegisteredTransfer> maybeTransfer = transferAuditMemoryStore.getTransferById(ID1);

        assertThat(maybeTransfer.isPresent()).isTrue();

        maybeTransfer.map(transfer -> assertThat(transfer.getId().equals(ID1)));
    }

    @Test
    public void transferNotFound() {
        Optional<RegisteredTransfer> maybeTransfer = transferAuditMemoryStore.getTransferById(ID2);

        assertThat(maybeTransfer.isPresent()).isFalse();
    }

}