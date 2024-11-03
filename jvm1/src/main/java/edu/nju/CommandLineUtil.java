package edu.nju;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Objects;

public class CommandLineUtil {
    private static CommandLine commandLine;
    private static CommandLineParser parser = new DefaultParser();
    private static Options options = new Options();
    private static boolean sideEffect;
    public static final String WRONG_MESSAGE = "Invalid input.";

    /**
     * you can define options here
     * or you can create a func such as [static void defineOptions()] and call it before parse input
     */
    static {
        options.addOption(Option.builder("h").longOpt("help").desc("Print help message").build());
        options.addOption(Option.builder("p").longOpt("print").hasArg().desc("Print argument value").build());
        options.addOption(Option.builder("s").longOpt("sideEffect").hasArg(false).desc("Set side effect").build());
    }

    public static void main(String[] args) {
        /*args = new String[2];
        args[0] = "-s";
        args[1] = "arg0";*/
        CommandLineUtil cli = new CommandLineUtil();
        cli.parseInput(args);
        cli.handleOptions();
        //System.out.println("Side effect flag: " + cli.getSideEffectFlag());
    }

    private void printHelpMessage() {
        /*HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CommandLineUtil", options);*/
        System.out.println("help");
    }

    public void parseInput(String[] args) {
        if(args.length == 0){
            System.exit(-1);
        }
        if(args.length == 1 && !Objects.equals(args[0], "-h") && !Objects.equals(args[0], "-s") && !Objects.equals(args[0], "-p")){
            System.exit(-1);
        }
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            if (e instanceof MissingArgumentException) {
                MissingArgumentException missingArgumentException = (MissingArgumentException) e;
                String option = missingArgumentException.getOption().getOpt();
                System.out.println("Missing argument for option: " + option);
                System.exit(-1);
            } else {
                System.out.println("Invalid input: " + e.getMessage());
               // System.exit(-1);
            }

        }
    }


    public void handleOptions() {

        if (commandLine.hasOption("h")) {
            sideEffect = false;
            printHelpMessage();
            return;
        }
        String[] userArgs = commandLine.getArgs();
        boolean arg0Present = false;
        for (String arg : userArgs) {
            if (arg.equals("arg0") || arg.equals("arg1")) {
                arg0Present = true;
                break;
            }
        }

        if (!arg0Present) {
            System.out.println(WRONG_MESSAGE);
            return;
        }

        if (commandLine.hasOption("p")) {
            String arg = commandLine.getOptionValue("p");
            String[] tokens = arg.split(" ");
            for (String token : tokens) {
                if (!token.equals("arg0")) {
                    System.out.println(token);
                }
            }
        }

        if (commandLine.hasOption("s")) {
            sideEffect = true;
        }
    }

    public boolean getSideEffectFlag() {
        return sideEffect;
    }
}