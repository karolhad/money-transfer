package com.hadala.resources;

import com.codahale.metrics.annotation.Timed;
import com.hadala.api.Account;
import com.hadala.db.AccountMemoryStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private AccountMemoryStore accountMemoryStore;

    public AccountResource(AccountMemoryStore accountMemoryStore) {
        this.accountMemoryStore = accountMemoryStore;
    }

    @GET
    @Path("/{accountId}")
    @Timed
    public Account getAccount(@PathParam("accountId") int accountId) {
        return accountMemoryStore.getAccountById(accountId);
    }

    @GET
    @Timed
    public Collection<Account> getAccounts() {
        return accountMemoryStore.getAccounts();
    }
}