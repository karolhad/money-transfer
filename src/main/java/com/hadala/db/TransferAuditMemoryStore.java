package com.hadala.db;

import com.hadala.api.RegisteredTransfer;

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

public class TransferAuditMemoryStore {

    private final LinkedList<RegisteredTransfer> audit = new LinkedList<>();

    public void audit(RegisteredTransfer transfer) {
        audit.add(transfer);
    }

    public Optional<RegisteredTransfer> getTransferById(UUID transferId) {
        return audit.stream().filter(transfer -> transfer.getId().equals(transferId)).findFirst();
    }
}

