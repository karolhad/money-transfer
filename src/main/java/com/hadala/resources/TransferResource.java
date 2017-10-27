package com.hadala.resources;

import com.codahale.metrics.annotation.Timed;
import com.hadala.api.Account;
import com.hadala.api.RegisteredTransfer;
import com.hadala.api.Transfer;
import com.hadala.core.AsyncAccountant;
import com.hadala.db.AccountMemoryStore;
import com.hadala.db.TransferAuditMemoryStore;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.UUID;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

    private final AsyncAccountant accountant;

    private final TransferAuditMemoryStore transferAuditMemoryStore;

    private final AccountMemoryStore accountMemoryStore;

    public TransferResource(AsyncAccountant accountant, TransferAuditMemoryStore transferAuditMemoryStore, AccountMemoryStore accountMemoryStore) {
        this.accountant = accountant;
        this.transferAuditMemoryStore = transferAuditMemoryStore;
        this.accountMemoryStore = accountMemoryStore;
    }

    @POST
    @Timed
    public RegisteredTransfer addTransfer(@Valid Transfer transfer) {
        return accountant.registerTransfer(transfer);
    }

    @GET
    @Path("/{transferId}")
    @Timed
    public Optional<RegisteredTransfer> getTransfer(@PathParam("transferId") UUID transferId) {
        return transferAuditMemoryStore.getTransferById(transferId);
    }

    @GET
    @Path("/{transferId}/source-account")
    @Timed
    public Optional<Account> getSourceAccount(@PathParam("transferId") UUID transferId) {
        return transferAuditMemoryStore.getTransferById(transferId).
                map(RegisteredTransfer::getSourceAccountId).
                map(accountMemoryStore::getAccountById);
    }

    @GET
    @Path("/{transferId}/target-account")
    @Timed
    public Optional<Account> getTargetAccount(@PathParam("transferId") UUID transferId) {
        return transferAuditMemoryStore.getTransferById(transferId).
                map(RegisteredTransfer::getTargetAccountId).
                map(accountMemoryStore::getAccountById);
    }
}