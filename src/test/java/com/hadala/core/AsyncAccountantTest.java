package com.hadala.core;

import com.hadala.api.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AsyncAccountantTest {

    private AsyncAccountant asyncAccountant;

    private ExecutorService executorService;
    @Mock private Accountant accountant;
    @Mock private Transfer transfer;


    @Before
    public void setUp() {
        asyncAccountant = new AsyncAccountant(accountant);
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(asyncAccountant);
    }

    @After
    public void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    public void twoTransfersProcessed() {
        asyncAccountant.registerTransfer(transfer);
        asyncAccountant.registerTransfer(transfer);

        verify(accountant, times(2)).transfer(any());
    }



}