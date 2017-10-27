package com.hadala.core;

import com.hadala.api.RegisteredTransfer;
import com.hadala.api.Transfer;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class AsyncAccountant implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(AsyncAccountant.class);

    private final BlockingQueue<RegisteredTransfer> queue;

    private final Accountant accountant;

    public AsyncAccountant(TransferAuditMemoryStore auditStore, AccountMemoryStore accountMemoryStore) {
        this(new Accountant(auditStore, accountMemoryStore));
    }

    AsyncAccountant(Accountant accountant) {
        this.queue = new BlockingArrayQueue<>();
        this.accountant = accountant;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                accountant.transfer(queue.take());
            } catch (InterruptedException ex) {
                logger.error("Execution interrupted", ex);
            }
        }
    }

    public RegisteredTransfer registerTransfer(Transfer transfer) {
        RegisteredTransfer registeredTransfer = new RegisteredTransfer(transfer);
        queue.offer(registeredTransfer);
        logger.info("Transaction submitted transactionId={}", registeredTransfer.getId());
        return registeredTransfer;
    }
}
