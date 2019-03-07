package ipl.frj.launcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.impl.SimpleLog;

import ferram.CLIOptionsSupport.CLIOptionsSupport;
import ipl.frj.util.MSGManager;

public class CmdLineOptions {

  public CmdLineOptions() {
    super();
    this.selectableProvers = new SelectableProvers();
    this.selectableProvers.setDefault(AvailableProvers.ONE_LEVEL_ITERATION);
    this.selectableSequentsDB = new SelectableSequentsDB();
    this.selectableSequentsDB.setDefault(AvailableSequentDB.SUBSUMPTION_BACWARD);
    this.options = new Options();
    buildCmdLineOptions();
    this.optionSupport = new CLIOptionsSupport();
    this.setIncompatibleOptions();
  }

  private static class CmdLineOptionsNames {

    static String HELP = "h";
    static String VERBOSE = "v";
    static String LATEX = "latex";
    static String LOG_DEBUG = "debug";
    static String PROVER = "tp";
    static String STDN_INPUT = "i";
    static String SEQUENTS_DB = "db";
    static String TESTSET = "testset";
    static String KRIPKE_STAT = "kripke-stat";
    static String GBU = "gbu";
  }

  private void setIncompatibleOptions() {
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.STDN_INPUT,
        CmdLineOptionsNames.TESTSET);
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.TESTSET, CmdLineOptionsNames.VERBOSE);
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.TESTSET, CmdLineOptionsNames.LATEX);
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.TESTSET,
        CmdLineOptionsNames.KRIPKE_STAT);
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.TESTSET, CmdLineOptionsNames.GBU);
    this.optionSupport.addIncompatibility(CmdLineOptionsNames.LATEX, CmdLineOptionsNames.VERBOSE);
  }

  private Options options;
  private CLIOptionsSupport optionSupport;
  private SelectableProvers selectableProvers;
  private SelectableSequentsDB selectableSequentsDB;

  private Options buildCmdLineOptions() {
    // HELP
    options.addOption(Option.builder(CmdLineOptionsNames.HELP).desc(MSG.OPTIONS.HELP).build());
    // VERBOSE OPTION
    options.addOption(Option.builder(CmdLineOptionsNames.VERBOSE).hasArg(false)
        .desc(String.format(MSG.OPTIONS.VERBOSE)).build());
    // TESTSET OPTION
    options.addOption(Option.builder(CmdLineOptionsNames.TESTSET).hasArg(false)
        .desc(String.format(MSG.OPTIONS.TESTSET)).build());
    // INPUT FROM STANDARD INPUT
    options.addOption(Option.builder(CmdLineOptionsNames.STDN_INPUT).hasArg(false)
        .desc(String.format(MSG.OPTIONS.STDN_INPUT)).build());
    // GBU
    options.addOption(Option.builder(CmdLineOptionsNames.GBU).hasArg(false)
        .desc(String.format(MSG.OPTIONS.GBU)).build());
    // PROVER OPTION
    options.addOption(Option.builder(CmdLineOptionsNames.PROVER).hasArg(true)
        .desc(String.format(MSG.OPTIONS.PROVER, selectableProvers.getNames())).build());
    // SEQUENT DB OPTION
    options.addOption(Option.builder(CmdLineOptionsNames.SEQUENTS_DB).hasArg(true)
        .desc(String.format(MSG.OPTIONS.SEQUENT_DB, selectableSequentsDB.getNames())).build());
    // LATEX_PROOF
    options.addOption(Option.builder(CmdLineOptionsNames.LATEX).hasArg(false)
        .desc(String.format(MSG.OPTIONS.LATEX_PROOF)).build());
    // LOG_DEBUG option
    options.addOption(Option.builder(CmdLineOptionsNames.LOG_DEBUG).hasArg(false)
        .desc(String.format(MSG.OPTIONS.LOG_DEBUG)).build());
    // KRIPKE STAT
    options.addOption(Option.builder().longOpt(CmdLineOptionsNames.KRIPKE_STAT).hasArg(false)
        .desc(String.format(MSG.OPTIONS.KRIPKE_STAT)).build());
    return options;
  }

  /**
   * Processes the command line options and returns a
   * 
   * @param args
   */
  public LauncherExecConfiguration processCommandLineOptions(String[] args) {
    CommandLine commandLine = null;
    try {
      CommandLineParser parser = new DefaultParser();
      // parse the command line arguments
      commandLine = parser.parse(options, args, false);

      // help optption
      if (commandLine.hasOption(CmdLineOptionsNames.HELP)) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getCanonicalName(),
            MSG.OPTIONS_MANAGMENT.INFO.HELP_HEADER, options, MSG.OPTIONS_MANAGMENT.INFO.HELP_FOOTER,
            true);
        System.exit(0);
      }

      // Check options compatibility
      if (optionSupport.hasIncompatibleOptions(commandLine)) {
        MSGManager.error(optionSupport.firstIncompatibilityDescription(commandLine));
        System.exit(1);
      }

      LauncherExecConfiguration configuration = new LauncherExecConfiguration();

      // log mode
      if (commandLine.hasOption(CmdLineOptionsNames.LOG_DEBUG)) {
        configuration.setLogMode(SimpleLog.LOG_LEVEL_DEBUG);
        configuration.setExecutuionMode(ExecutionMode.VERBOSE);
      } else
        configuration.setLogMode(SimpleLog.LOG_LEVEL_OFF);

      // verbose mode
      if (commandLine.hasOption(CmdLineOptionsNames.VERBOSE))
        configuration.setExecutuionMode(ExecutionMode.VERBOSE);

      if (commandLine.hasOption(CmdLineOptionsNames.STDN_INPUT))
        configuration.setReadFromStandardInput(true);

      // testset mode
      if (commandLine.hasOption(CmdLineOptionsNames.TESTSET))
        configuration.setExecutuionMode(ExecutionMode.TESTSET);

      // prover
      if (commandLine.hasOption(CmdLineOptionsNames.PROVER)) {
        String val = commandLine.getOptionValue(CmdLineOptionsNames.PROVER);
        AvailableProvers selectedProver = selectableProvers.searchByName(val);
        if (selectedProver != null)
          configuration.setSelectedProver(selectedProver);
        else {
          MSGManager.error(MSG.OPTIONS_MANAGMENT.ERRORS.OPTION_WRONG_ARGUMENT, val,
              CmdLineOptionsNames.PROVER);
          System.exit(1);
        }
      } else
        configuration.setSelectedProver(selectableProvers.getDefault().getValue());

      // sequent db 
      if (commandLine.hasOption(CmdLineOptionsNames.SEQUENTS_DB)) {
        String val = commandLine.getOptionValue(CmdLineOptionsNames.SEQUENTS_DB);
        AvailableSequentDB selectedDB = selectableSequentsDB.searchByName(val);
        if (selectedDB != null)
          configuration.setSelectedSequentDB(selectedDB);
        else {
          MSGManager.error(MSG.OPTIONS_MANAGMENT.ERRORS.OPTION_WRONG_ARGUMENT, val,
              CmdLineOptionsNames.SEQUENTS_DB);
          System.exit(1);
        }
      } else
        configuration.setSelectedSequentDB(selectableSequentsDB.getDefault().getValue());

      // generate latex 
      if (commandLine.hasOption(CmdLineOptionsNames.LATEX))
        configuration.setGenerateLatexOfProof(true);

      // generate kripke model stat
      if (commandLine.hasOption(CmdLineOptionsNames.KRIPKE_STAT))
        configuration.setGenerateKripkeStat(true);

      if (commandLine.hasOption(CmdLineOptionsNames.GBU))
        configuration.setExecGbu(true);

      // extract intput file names
      configuration.setInputFileNames(commandLine.getArgs());
      return configuration;
    } catch (ParseException exp) {
      // oops, something went wrong
      MSGManager.error(MSG.OPTIONS_MANAGMENT.ERRORS.INVOCATION_ERROR, exp.getMessage());
      System.exit(1);
      return null;
    }
  }

}
