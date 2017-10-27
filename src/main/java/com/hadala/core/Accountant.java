package com.hadala.core;

import com.hadala.api.Account;
import com.hadala.api.RegisteredTransfer;
import com.hadala.api.RegisteredTransfer.Status;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

class Accountant {

    private final static Logger logger = LoggerFactory.getLogger(AsyncAccountant.class);

    private final TransferAuditMemoryStore auditStore;
    private final AccountMemoryStore accountMemoryStore;

    Accountant(TransferAuditMemoryStore auditStore, AccountMemoryStore accountMemoryStore) {
        this.auditStore = auditStore;
        this.accountMemoryStore = accountMemoryStore;
    }

    void transfer(RegisteredTransfer transfer) {

        Account source = accountMemoryStore.getAccountById(transfer.getSourceAccountId());
        Account target = accountMemoryStore.getAccountById(transfer.getTargetAccountId());

        if (source == null || target == null) {
            transfer.setStatus(Status.FAILED);
            logger.info("Transfer id={} failed - source({}) or target({}) account doesn't exist", transfer.getId(),
                    transfer.getSourceAccountId(), transfer.getTargetAccountId());
        } else {
            BigDecimal sourceNewBalance = source.getBalance().subtract(transfer.getAmount());

            if (hasEnoughMoney(sourceNewBalance)) {
                source.setBalance(sourceNewBalance);
                target.setBalance(target.getBalance().add(transfer.getAmount()));
                transfer.setStatus(Status.COMPLETED);
                logger.info("Transfer id={} completed", transfer.getId());
            } else {
                transfer.setStatus(Status.FAILED);
                logger.info("Transfer id={} failed - not enough money on source account", transfer.getId());
            }
        }

        auditStore.audit(transfer);
    }

    private boolean hasEnoughMoney(BigDecimal sourceNewBalance) {
        return sourceNewBalance.signum() >= 0;
    }
}