package com.hadala;

import com.hadala.core.AsyncAccountant;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;
import com.hadala.health.AccountMemoryStoreHealthCheck;
import com.hadala.resources.AccountResource;
import com.hadala.resources.TransferResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.util.concurrent.Executors;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    private final AccountResource accountResource;
    private final TransferResource transferResource;
    private final AccountMemoryStore accountMemoryStore;

    public MoneyTransferApplication() {
        TransferAuditMemoryStore transferAuditMemoryStore = new TransferAuditMemoryStore();
        accountMemoryStore = new AccountMemoryStore();
        AsyncAccountant accountant = new AsyncAccountant(transferAuditMemoryStore, accountMemoryStore);

        transferResource = new TransferResource(accountant, transferAuditMemoryStore, accountMemoryStore);
        accountResource = new AccountResource(accountMemoryStore);

        Executors.newFixedThreadPool(1).submit(accountant);
    }

    public static void main(final String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    @Override
    public void run(final MoneyTransferConfiguration configuration,
                    final Environment environment) {
        accountMemoryStore.addAll(configuration.getAccounts());

        environment.jersey().register(accountResource);
        environment.jersey().register(transferResource);

        environment.healthChecks().register("accountMemoryStore", new AccountMemoryStoreHealthCheck(accountMemoryStore));
    }

    @Override
    public String getName() {
        return "MoneyTransfer";
    }

}
