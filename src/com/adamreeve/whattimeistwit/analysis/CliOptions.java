package com.adamreeve.whattimeistwit.analysis;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 9/14/12 Time: 7:11 PM
 */
public class CliOptions {

    private static Logger logger = LoggerFactory.getLogger(CliOptions.class);
    public static final String OPT_HELP = "help";
    public static final String OPT_DIRNAME = "dirname";
    public static final String OPT_FILENAME = "filename";
    public static final String OPT_PERIOD_SIZE = "periodsize";

    private Options options;

    public CliOptions() {
        options = BuildOptions();
    }

    private Options BuildOptions() {
        Options options = new Options();

        options.addOption(new Option("h", OPT_HELP, false, "display this message"));

        OptionGroup nameGroup = new OptionGroup();
        nameGroup.setRequired(true);
        nameGroup.addOption(OptionBuilder.hasArg()
                                    .withType(String.class)
                                    .withArgName("dir")
                                    .withDescription("parse all files in specified directory")
                                    .withLongOpt(OPT_DIRNAME)
                                    .create("d"));

        nameGroup.addOption(OptionBuilder.hasArg()
                                    .withType(String.class)
                                    .withArgName("file")
                                    .withDescription("input filename")
                                    .withLongOpt(OPT_FILENAME)
                                    .create("f"));
        options.addOptionGroup(nameGroup);

        options.addOption(OptionBuilder.hasArg()
                                  .withType(Number.class)
                                  .withArgName("secs")
                                  .withDescription("make periods this size")
                                  .withLongOpt(OPT_PERIOD_SIZE)
                                  .create("p"));

        return options;
    }

    protected CommandLine parseCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        CommandLine result = null;

        result = parser.parse(options, args);

        return result;
    }

    public void printHelpText() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ClassificationProcessor", options, true);
    }
}
