package com.hadala.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

import static com.hadala.api.RegisteredTransfer.Status.IN_PROGRESS;

@Data
@AllArgsConstructor
public class RegisteredTransfer {

    private final UUID id;
    private Status status;
    private final int sourceAccountId;
    private final int targetAccountId;
    private final BigDecimal amount;

    public RegisteredTransfer(Transfer transfer) {
        this.sourceAccountId = transfer.getSourceAccountId();
        this.targetAccountId = transfer.getTargetAccountId();
        this.amount = transfer.getAmount();
        this.id = UUID.randomUUID();
        this.status = IN_PROGRESS;
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, FAILED
    }
}
