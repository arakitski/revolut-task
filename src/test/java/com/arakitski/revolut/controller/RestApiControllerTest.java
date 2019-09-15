package com.arakitski.revolut.controller;

import com.arakitski.revolut.model.Account;
import com.arakitski.revolut.service.AccountService;
import com.arakitski.revolut.service.MoneyTransferService;
import com.google.common.collect.ImmutableList;
import com.google.inject.util.Providers;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

/**
 * Test class for {@link RestApiController}.
 */
public class RestApiControllerTest {

    private static AccountService accountService;
    private static MoneyTransferService moneyTransferService;

    private CloseableHttpClient httpclient;
    private URIBuilder accountUriBuilder;

    @BeforeClass
    public static void beforeClass() {
        accountService = mock(AccountService.class);
        moneyTransferService = mock(MoneyTransferService.class);
        RestApiController restApi = new RestApiController(Providers.of(9876), accountService, moneyTransferService);
        restApi.start();

        awaitInitialization();
    }

    @Before
    public void setUp() {
        accountService = mock(AccountService.class);
        moneyTransferService = mock(MoneyTransferService.class);
        httpclient = HttpClients.createDefault();
        accountUriBuilder = new URIBuilder()
                .setScheme("http")
                .setHost("localhost:9876")
                .setPath("/accounts");
    }

    @AfterClass
    public static void afterClass() {
        stop();
    }

    @Test
    public void testGetAll() throws IOException, URISyntaxException {
        ImmutableList<Account> expectedList = ImmutableList.of(new Account(1, BigDecimal.ZERO), new Account(2, BigDecimal.ONE));
        when(accountService.getAll()).thenReturn(expectedList);

        CloseableHttpResponse response = httpclient.execute(new HttpGet(accountUriBuilder.build()));

        assertEquals(response.getStatusLine().getStatusCode(), 200);
        assertEquals(EntityUtils.toString(response.getEntity(), "UTF-8"), expectedList.toString());
    }

    @Test
    public void testGetAccountById() throws IOException, URISyntaxException {
        Account expectedAccount = new Account(1, BigDecimal.ZERO);
        int accountId = 100500;
        when(accountService.getById(accountId)).thenReturn(expectedAccount);

        CloseableHttpResponse response = httpclient.execute(
                new HttpGet(accountUriBuilder.setPath("accounts/" + accountId).build()));

        assertEquals(response.getStatusLine().getStatusCode(), 200);
        assertEquals(EntityUtils.toString(response.getEntity(), "UTF-8"), expectedAccount.toString());
    }

    @Test
    public void testCreateAccount() throws IOException, URISyntaxException {
        int amount = 555;
        Account expectedAccount = new Account(1, BigDecimal.valueOf(amount));
        when(accountService.create(BigDecimal.valueOf(amount))).thenReturn(expectedAccount);

        CloseableHttpResponse response = httpclient.execute(
                new HttpPost(accountUriBuilder.setParameter("balance", String.valueOf(amount)).build()));

        assertEquals(response.getStatusLine().getStatusCode(), 201);
        assertEquals(EntityUtils.toString(response.getEntity(), "UTF-8"), expectedAccount.toString());
    }

    @Test
    public void testDeleteAccount() throws URISyntaxException, IOException {
        int accountId = 101;

        CloseableHttpResponse response = httpclient.execute(
                new HttpDelete(accountUriBuilder.setPath("accounts/" + accountId).build()));

        verify(accountService).delete(accountId);
        assertEquals(response.getStatusLine().getStatusCode(), 204);
    }

    @Test
    public void testTransferMoney() throws URISyntaxException, IOException {
        long fromId = 200;
        long toId = 201;
        int amount = 1000;

        CloseableHttpResponse response = httpclient.execute(
                new HttpPost(
                        accountUriBuilder.setPath("/transfer")
                                .setParameter("fromId", String.valueOf(fromId))
                                .setParameter("toId", String.valueOf(toId))
                                .setParameter("amount", String.valueOf(amount))
                                .build()));

        verify(moneyTransferService).transfer(fromId, toId, BigDecimal.valueOf(amount));
        assertEquals(response.getStatusLine().getStatusCode(), 204);
    }
}