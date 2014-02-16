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

/**
 * Date: 9/14/12 Time: 7:11 PM
 */
public class CliOptions {

    public static final String OPT_HELP = "help";
    public static final String OPT_DIRNAME = "dirname";
    public static final String OPT_FILENAME = "filename";
    public static final String OPT_THREADS = "threads";
    public static final String OPT_DICTBASE = "dicts";

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
                                  .withType(String.class)
                                  .withArgName("count")
                                  .withDescription("use this many threads (default = 1)")
                                  .withLongOpt(OPT_THREADS)
                                  .create("t"));


        options.addOption(OptionBuilder.hasArg()
                                  .withType(String.class)
                                  .withArgName("dir")
                                  .withDescription("load dictionaries from this directory")
                                  .withLongOpt(OPT_DICTBASE)
                                  .isRequired()
                                  .create("b"));

        return options;
    }

    protected CommandLine parseCommandLine(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    public void printHelpText() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(100, "ClassificationProcessor", "", options, "", true);
    }
}
