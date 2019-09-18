package com.arakitski.revolut.controller;

import com.arakitski.revolut.annotation.ApplicationPort;
import com.arakitski.revolut.exception.NotEnoughMoneyException;
import com.arakitski.revolut.service.AccountService;
import com.arakitski.revolut.service.MoneyTransferService;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;

import static spark.Spark.*;

/**
 * Application rest controller.
 */
public class RestApiController {

    private static Logger logger = LoggerFactory.getLogger(RestApiController.class);

    private final Provider<Integer> portProvider;
    private final AccountService accountService;
    private final MoneyTransferService moneyTransferService;

    @Inject
    RestApiController(
            @ApplicationPort Provider<Integer> portProvider,
            AccountService accountService,
            MoneyTransferService moneyTransferService) {
        this.portProvider = portProvider;
        this.accountService = accountService;
        this.moneyTransferService = moneyTransferService;
    }

    public void start() {
        port(portProvider.get());

        before("/*", (req, res) -> logger.info("Request to : {}, type: {}", req.uri(), req.requestMethod()));

        path("/accounts", () -> {
            get("", (request, response) -> {
                logger.info("Get all account call");
                return accountService.getAll();
            });
            post("", (request, response) -> {
                BigDecimal balance = new BigDecimal(request.queryParams("balance"));
                accountService.create(balance);
                response.status(201);
                logger.info("create account with balance: {}", balance);
                return "OK";
            });
            get("/:id", (request, response) -> {
                Long id = Long.parseLong(request.params("id"));
                logger.info("get account by id {}", id);
                return accountService.getById(id);
            });
            delete("/:id", (request, response) -> {
                Long id = Long.parseLong(request.params("id"));
                logger.info("get account by id {}", id);
                accountService.delete(id);
                response.status(204);
                return "OK";
            });
        });

        path("/transfer", () -> post("", (request, response) -> {
            logger.info("transfer money request {}", request);
            moneyTransferService.transfer(
                    Long.parseLong(request.queryParams("fromId")),
                    Long.parseLong(request.queryParams("toId")),
                    new BigDecimal(request.queryParams("amount")));
            response.status(204);
            return "OK";
        }));

        exception(NotEnoughMoneyException.class, (exception, request, response) -> {
            response.status(400);
            response.body(exception.getMessage());
        });
        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.status(400);
            response.body(exception.getMessage());
        });

    }
}
