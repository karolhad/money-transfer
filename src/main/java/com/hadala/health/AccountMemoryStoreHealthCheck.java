package com.hadala.health;

import com.codahale.metrics.health.HealthCheck;
import com.hadala.db.AccountMemoryStore;
import org.apache.commons.collections.CollectionUtils;

public class AccountMemoryStoreHealthCheck extends HealthCheck {

    private final AccountMemoryStore accountMemoryStore;

    public AccountMemoryStoreHealthCheck(AccountMemoryStore accountMemoryStore) {
        this.accountMemoryStore = accountMemoryStore;
    }

    @Override
    protected Result check() throws Exception {
        if (CollectionUtils.isEmpty(accountMemoryStore.getAccounts())) {
            return Result.unhealthy("No accounts, cannot do the transfer!");
        }

        return Result.healthy();
    }
}
