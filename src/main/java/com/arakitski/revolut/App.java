package com.arakitski.revolut;

import com.arakitski.revolut.controller.RestApiController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;

import javax.annotation.Nullable;

/**
 * Application main class.
 */
public class App {

    private static final Options OPTIONS = new Options().addOption(Option.builder("p")
            .required(false)
            .desc("Application api port")
            .longOpt("port")
            .build());
    private static final CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();

    public static void main(String[] args) throws ParseException {
        // configure logger
        BasicConfigurator.configure();
        Injector injector = Guice.createInjector(new AppModule(getApplicationPort(args)));
        injector.getBinding(RestApiController.class).getProvider().get().start();
    }

    @Nullable
    private static Integer getApplicationPort(String[] args) throws ParseException {
        CommandLine commandLine = COMMAND_LINE_PARSER.parse(OPTIONS, args);
        try {
            return Integer.parseInt(commandLine.getOptionValue("p"));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
