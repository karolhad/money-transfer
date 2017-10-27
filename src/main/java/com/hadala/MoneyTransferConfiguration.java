package com.hadala;

import com.hadala.api.Account;
import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class MoneyTransferConfiguration extends Configuration {
    private List<Account> accounts;
}
