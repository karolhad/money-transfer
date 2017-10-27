package com.hadala.db;

import com.hadala.api.Account;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountMemoryStore {

    private Map<Integer, Account> accountById = new HashMap<>();

    public void addAll(List<Account> accounts) {
        accounts.forEach(account -> accountById.put(account.getId(), account));
    }

    public Account getAccountById(int accountId) {
        return accountById.get(accountId);
    }

    public Collection<Account> getAccounts() {
        return accountById.values();
    }
}
