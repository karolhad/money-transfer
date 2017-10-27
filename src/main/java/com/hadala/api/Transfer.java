package com.hadala.api;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
public class Transfer {

    private final int sourceAccountId;
    private final int targetAccountId;

    @Digits(integer = 7, fraction = 2)
    @DecimalMin("0.01")
    private final BigDecimal amount;

}
