package ipl.frj.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import ipl.frj.gbu.sequent.GbuRegularSequent;
import ipl.frj.gbu.tp.GbuProver;
import ipl.frj.kripke.KripkeModel;
import ipl.frj.kripke.KripkeModelBuilder;
import ipl.frj.latex.GbuCTreeLatexFormatter;
import ipl.frj.latex.LatexGenerator;
import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.tp.basic._FrjProver;
import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import ipl.frj.util.MSGManager;
import jtabwb.engine.Engine;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.Trace;
import jtabwb.launcher.InitialGoalBuilderException;
import jtabwb.tracesupport.LatexTranslator;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.JTabWbSimpleProblemReader;
import jtabwbx.problems.PlainProblemReader;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.problems.ProblemDescriptionException;
import jtabwbx.prop.formula.Formula;

/**
 * Launcher for Nbu calculus.
 * 
 * @author Mauro Ferrari
 */
public class Main {

  private static String FORMULA_SYNTAX_DESCRIPTION = //
      "Syntax of formulas\n" + "  atoms: Java identifiers\n" + //
          "logical: false, & (and), | (or), ~ (not), => (implies), <=> (iff)\n" + //
          "  notes: (~ A) is translated as (A => false)\n" + //
          "         (A <=> B) is translated as ((A => B) & (B => A))";

  // TESTSET LOG FILE NAME
  final static String DEFAULT_TESTSET_FILE_PREFIX = "testset-";
  final static String DEFAULT_TESTSET_FILE_SUFFIX = ".log";

  private final Log LOG;
  private ThreadMXBean bean;

  private Main() {
    this.LOG = new SimpleLog(this.getClass().getCanonicalName());
    this.bean = ManagementFactory.getThreadMXBean();
  }

  /*
   * Read the specified problem in jtabwb format and returns the problem data.
   */
  private ProblemReaderData readFromFile(LauncherExecConfiguration launcherExecConf,
      String inputFilename) {
    try {
      // get the file
      File inputFile = new File(inputFilename);
      if (!inputFile.exists()) {
        LOG.error(String.format(MSG.LAUNCHER.ERROR.NO_SUCH_FILE, inputFilename));
        System.exit(1);
      }
      if (!launcherExecConf.testSetMode()) {
        info_preReaderExecutionDetails(launcherExecConf);
        MSGManager.infoNoLn(MSG.LAUNCHER.INFO.PROBLEM_DESCRIPTION_PARSING_BEGIN,
            inputFile.getPath());
      }

      // get the reader
      FileReader fir = new FileReader(inputFile);
      // build the reader
      JTabWbSimpleProblemReader problemReader = new JTabWbSimpleProblemReader(inputFile.getName());
      // read problem description
      long parsing_problem_start_time = getCurrentTimeMilleseconds();
      ProblemDescription problem = problemReader.read(fir);
      long parsing_problem_end_time = getCurrentTimeMilleseconds();

      // build data bundle
      ProblemReaderData problemParsingData = new ProblemReaderData(problemReader, problem,
          parsing_problem_start_time, parsing_problem_end_time);

      if (launcherExecConf.testSetMode())
        MSGManager.infoNoLn(MSG.LAUNCHER.TESTSET_INFO.PROBLEM_1, problem.getProblemName());
      else {
        MSGManager.info(MSG.LAUNCHER.INFO.PROBLEM_DESCRIPTION_PARSING_END,
            problemParsingData.problemParsingTime());
        print_problemDetails(launcherExecConf, problem);
      }
      return problemParsingData;

    } catch (IOException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } catch (ProblemDescriptionException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.PROBLEM_WRONG_FORMAT, e.getMessage());
      System.exit(1);
    }
    return null;
  }

  /*
   * Read the specified problem from std input and returns the problem data.
   */
  private ProblemReaderData readFromStandardInput() {
    MSGManager.info(FORMULA_SYNTAX_DESCRIPTION);
    MSGManager.info(MSG.LAUNCHER.INFO.STDIN_INPUT_TYPE_A_FORMULA);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    try {
      String strFormula = in.readLine();
      BufferedReader reader = new BufferedReader(new StringReader(strFormula));
      // build the reader
      PlainProblemReader problemReader = new PlainProblemReader();
      // read problem description
      long parsing_problem_start_time = getCurrentTimeMilleseconds();
      ProblemDescription problem = problemReader.read(reader);
      problem.setName("input");
      long parsing_problem_end_time = getCurrentTimeMilleseconds();

      // build data bundle
      ProblemReaderData problemParsingData = new ProblemReaderData(problemReader, problem,
          parsing_problem_start_time, parsing_problem_end_time);

      MSGManager.info(MSG.LAUNCHER.INFO.PROBLEM_DESCRIPTION_PARSING_END,
          problemParsingData.problemParsingTime());
      return problemParsingData;
    } catch (ProblemDescriptionException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.IO_EXCEPTION, e.getMessage());
      System.exit(1);
    } catch (IOException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.PROBLEM_WRONG_FORMAT, e.getMessage());
      System.exit(1);
    }
    return null;
  }

  private long getCurrentTimeMilleseconds() {
    return TimeUnit.MILLISECONDS.convert(bean.getCurrentThreadCpuTime(), TimeUnit.NANOSECONDS);
  }

  /**
   * The method launching the prover from main.
   * 
   * @param args the command line arguments.
   */
  private void start(String[] args) {
    LauncherExecConfiguration launcherExecConf;
    launcherExecConf = (new CmdLineOptions()).processCommandLineOptions(args);
    // configure
    ((SimpleLog) LOG).setLevel(launcherExecConf.logMode());
    try {
      if (launcherExecConf.readFromStandardInput()) {
        ProblemReaderData problemReaderData = readFromStandardInput();
        singleProblemExecution(launcherExecConf, problemReaderData);
      } else if (launcherExecConf.testSetMode()) { // TESTSET MODE
        TestSetDetails testsetDetails = new TestSetDetails(launcherExecConf);
        for (String inputFilename : launcherExecConf.getInputFileNames()) {
          // read the problem     
          ProblemReaderData problemParsingData = readFromFile(launcherExecConf, inputFilename);
          // build initial node set;
          InitialNodeSetData initialNodeSetCostructionData =
              buildInitialNodeSet(launcherExecConf, problemParsingData.getProblemDescription());
          // search for proof
          FrjProofSearchData proofSearchData =
              searchProof(launcherExecConf, initialNodeSetCostructionData.getGoal());

          FrjSingleProblemExecutionData executionData = new FrjSingleProblemExecutionData(
              problemParsingData, initialNodeSetCostructionData, proofSearchData);

          testset_printSingleTestInfo(executionData);
          testset_updateTestSetDetails(testsetDetails, executionData);
        }
        testset_printReport(launcherExecConf, testsetDetails);
      } else // SIMPLE MODE
        for (String inputFilename : launcherExecConf.getInputFileNames()) {
          // read problem
          ProblemReaderData problemReaderData = readFromFile(launcherExecConf, inputFilename);
          singleProblemExecution(launcherExecConf, problemReaderData);
        }
    } catch (InitialGoalBuilderException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.INITIAL_NODE_BUILDER_ERROR, e.getMessage());
      System.exit(1);
    }

  }

  private void singleProblemExecution(LauncherExecConfiguration launcherExecConf,
      ProblemReaderData problemReaderData) throws InitialGoalBuilderException {

    FrjSingleProblemExecutionData frjExecData = null;
    {  // FRJ proof-search
      // build intial node set
      InitialNodeSetData frjInitialNodeSetConstructionData =
          buildInitialNodeSet(launcherExecConf, problemReaderData.getProblemDescription());
      // start proofsearch
      FrjProofSearchData proofSearchData =
          searchProof(launcherExecConf, frjInitialNodeSetConstructionData.getGoal());

      frjExecData = new FrjSingleProblemExecutionData(problemReaderData,
          frjInitialNodeSetConstructionData, proofSearchData);

      // generate kripke stat if required and proof-search is succesfull
      if (launcherExecConf.generateKripkeStat()
          && frjExecData.proofSearchResult() == ProofSearchResult.SUCCESS) {

        KripkeModelBuilder modelBduilder =
            new KripkeModelBuilder(proofSearchData.getProofSearchDetails().getProvedSequentsDB());
        frjExecData.setKripkeModel(modelBduilder.build());
      }
    }

    if (launcherExecConf.verboseMode())
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_END_VERBOSE, frjExecData.proofSearchTime());
    else
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_END, frjExecData.proofSearchTime());
    print_postFrjProofSearchDetails(frjExecData, launcherExecConf);

    GbuSingleProblemExecutionData gbuExecData = null;
    // activate gbu if required
    if (frjExecData.proofSearchResult() == ProofSearchResult.FAILURE
        && launcherExecConf.execGbu()) {
      gbuExecData = gbu_proofSearch(launcherExecConf, frjExecData);
      print_postGbuProofSearchDetails(gbuExecData, launcherExecConf);
    }

    // generate latex proof if required
    if (launcherExecConf.generateLatex()) {
      try {
        LatexGenerator latexGen = new LatexGenerator(launcherExecConf,frjExecData,gbuExecData);
        latexGen.generateLatex();
      } catch (IOException e) {
        MSGManager.error(MSG.LAUNCHER.ERROR.LATEX_CANNOT_BE_GENERATED, e.getMessage());
      }
    }

  }

  private GbuSingleProblemExecutionData gbu_proofSearch(LauncherExecConfiguration launcherExecConf,
      FrjSingleProblemExecutionData frjExecutionData) {

    MSGManager.info(MSG.GBU_EXECUTION.STARTING_GBU);

    Formula goal = frjExecutionData.proofSearchDetails().getGoal();

    // initial node set construction
    MSGManager.infoNoLn(MSG.LAUNCHER.INFO.INITIAL_NODE_SET_BUILDING_BEGIN);
    long initial_node_set_construction_start_time = getCurrentTimeMilleseconds();
    GbuRegularSequent initialGoal = new GbuRegularSequent((FrjFormulaFactory) goal.getFactory());
    initialGoal.addRight(goal);
    long initial_node_set_construction_end_time = getCurrentTimeMilleseconds();

    InitialNodeSetData initialNodeSetData = new InitialNodeSetData(goal,
        initial_node_set_construction_start_time, initial_node_set_construction_end_time);

    MSGManager.info(MSG.LAUNCHER.INFO.INITIAL_NODE_SET_BUILDING_END,
        initialNodeSetData.initialNodeSetConstructionTime());

    // build the prover
    _ProvedSequentsDBWithBwSubsumption db = (_ProvedSequentsDBWithBwSubsumption) frjExecutionData
        .proofSearchDetails().getProvedSequentsDB();

    GbuProver gbuProver = new GbuProver(db);

    MSGManager.info(MSG.PROOF_SEARCH_INFO.PROVER_DETAILS,
        gbuProver.getProverName().getDetailedName());
    if (launcherExecConf.verboseMode())
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_BEGIN);
    else
      MSGManager.infoNoLn(MSG.LAUNCHER.INFO.PROVING_BEGIN);

    // proof-search
    long proof_search_start_time = getCurrentTimeMilleseconds();
    Engine engine = null;
    if (launcherExecConf.verboseMode())
      engine = new Engine(gbuProver, initialGoal, Engine.ExecutionMode.ENGINE_VERBOSE);
    else if (launcherExecConf.generateLatex())
      engine = new Engine(gbuProver, initialGoal, Engine.ExecutionMode.ENGINE_TRACE);
    else
      engine = new Engine(gbuProver, initialGoal, Engine.ExecutionMode.ENGINE_PLAIN);
    engine.searchProof();
    // end of proof search, set info values
    long proof_search_end_time = getCurrentTimeMilleseconds();

    GbuProofSearchData gbuProofSearchData =
        new GbuProofSearchData(gbuProver, engine, proof_search_start_time, proof_search_end_time);

    if (launcherExecConf.verboseMode())
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_END_VERBOSE, gbuProofSearchData.proofSearchTime());
    else
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_END, gbuProofSearchData.proofSearchTime());

    return new GbuSingleProblemExecutionData(frjExecutionData.getProblemParsingData(),
        initialNodeSetData, gbuProofSearchData);

  }

  private FrjProofSearchData searchProof(LauncherExecConfiguration launcherExecConf, Formula goal) {

    _FrjProver prover =
        launcherExecConf.getSelectedProver().getProverInstance(launcherExecConf, goal);

    switch (launcherExecConf.executionMode()) {
    case VERBOSE:
      MSGManager.info(MSG.PROOF_SEARCH_INFO.PROVER_DETAILS, prover.getProverName());
      MSGManager.info(MSG.LAUNCHER.INFO.PROVING_BEGIN);
      break;
    case PLAIN:
      MSGManager.info(MSG.PROOF_SEARCH_INFO.PROVER_DETAILS, prover.getProverName());
      MSGManager.infoNoLn(MSG.LAUNCHER.INFO.PROVING_BEGIN);
      break;
    case TESTSET:
      break;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }

    // start the proof search
    long proof_search_start_time = getCurrentTimeMilleseconds();

    prover.prove();
    // end of proof search, set info values
    long proof_search_end_time = getCurrentTimeMilleseconds();

    return new FrjProofSearchData(prover, proof_search_start_time, proof_search_end_time);
  }

  private InitialNodeSetData buildInitialNodeSet(LauncherExecConfiguration configuration,
      ProblemDescription problemDescription) throws InitialGoalBuilderException {
    // build the initial node set
    try {
      if (!configuration.testSetMode())
        MSGManager.infoNoLn(MSG.LAUNCHER.INFO.INITIAL_NODE_SET_BUILDING_BEGIN);
      // build initial node set
      InitialGoalBuilder i = new InitialGoalBuilder();
      long initial_node_set_construction_start_time = getCurrentTimeMilleseconds();
      Formula initialNodeSet = i.buildInitialNodeSet(problemDescription);
      long initial_node_set_construction_end_time = getCurrentTimeMilleseconds();

      InitialNodeSetData initialNodeSetConstructionData = new InitialNodeSetData(initialNodeSet,
          initial_node_set_construction_start_time, initial_node_set_construction_end_time);

      if (!configuration.testSetMode())
        MSGManager.info(MSG.LAUNCHER.INFO.INITIAL_NODE_SET_BUILDING_END,
            initialNodeSetConstructionData.initialNodeSetConstructionTime());

      return initialNodeSetConstructionData;
    } catch (InitialGoalBuilderException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.INITIAL_NODE_BUILDER_ERROR, e.getMessage());
      System.exit(1);
    }
    return null;
  }

  private void info_preReaderExecutionDetails(LauncherExecConfiguration conf) {
    if (conf.readFromStandardInput())
      MSGManager.infoNoLn(MSG.PROOF_SEARCH_INFO.STDIN_READER_DETAILS, "plain reader");
    else
      MSGManager.info(MSG.PROOF_SEARCH_INFO.READER_DETAILS, "jtabwb");
  }

  private void print_problemDetails(LauncherExecConfiguration conf,
      ProblemDescription problemDescription) {
    // build the initial node set
    if (conf.testSetMode()) {
      MSGManager.infoNoLn(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROBLEM_INFO,
          problemDescription.getProblemName(), problemDescription.getProblemStatus().toString()));
    } else {
      MSGManager.info(String.format(MSG.PROOF_SEARCH_INFO.PROBLEM_DETAILS,
          problemDescription.getProblemName(), problemDescription.getProblemStatus().toString()));
    }

  }

  private void print_postFrjProofSearchDetails(FrjSingleProblemExecutionData proofSearchData,
      LauncherExecConfiguration launcherConfiguration) {
    StringBuilder sb = new StringBuilder();

    sb.append(MSG.PROOF_SEARCH_INFO.INFO_SEPARATOR);
    sb.append("\n");
    sb.append(String.format(MSG.PROOF_SEARCH_INFO.PROVER_DETAILS,
        proofSearchData.getProver().getProverName()));
    sb.append("\n");

    sb.append(String.format(MSG.PROOF_SEARCH_INFO.TEST_DETAILS,
        proofSearchData.problemDescription().getProblemName(),
        proofSearchData.problemDescription().getProblemStatus().name(), //
        proofSearchData.proofSearchResult(), // 
        proofSearchData.testStatus()));
    sb.append("\n");

    // ADD proof search details
    {
      StringBuilder psdsb = proofSearchData.proofSearchDetails().getDetailsDescription();
      String[] lines = psdsb.toString().split("\\n");
      for (String line : lines)
        sb.append(
            String.format(MSG.PROOF_SEARCH_INFO.PROOF_SEARCH_DETAILS_LINE_PREFIX, line) + "\n");
    }

    long proofSearch_time = proofSearchData.proofSearchTime();
    long buildInitialNodeSet_time = proofSearchData.initialNodeSetConstructionTime();
    long problemReading_time = proofSearchData.problemParsingTime();
    long totalProof_time = buildInitialNodeSet_time + proofSearch_time + problemReading_time;
    sb.append(String.format(MSG.PROOF_SEARCH_INFO.TIMINGS_DETAILS, proofSearch_time,
        buildInitialNodeSet_time, problemReading_time));
    sb.append("\n");
    String convertedTime = buildTimeString(totalProof_time);
    if (convertedTime == null)
      sb.append(String.format(MSG.PROOF_SEARCH_INFO.TOTAL_TIME_1, totalProof_time));
    else
      sb.append(String.format(MSG.PROOF_SEARCH_INFO.TOTAL_TIME_2, totalProof_time,
          convertedTime.toString()));
    sb.append("\n");

    sb.append(MSG.PROOF_SEARCH_INFO.INFO_SEPARATOR);

    if (launcherConfiguration.generateKripkeStat()
        && proofSearchData.proofSearchResult() == ProofSearchResult.SUCCESS) {
      sb.append("\n");
      KripkeModel km = proofSearchData.getKripkeModel();
      sb.append(String.format(MSG.PROOF_SEARCH_INFO.KRIPKE_STAT,
          proofSearchData.problemDescription().getProblemName(), km.numberOfWorlds(), km.depth()));
      sb.append("\n");
    }

    MSGManager.info(sb.toString());
  }

  private void print_postGbuProofSearchDetails(GbuSingleProblemExecutionData proofSearchData,
      LauncherExecConfiguration launcherConfiguration) {
    StringBuilder sb = new StringBuilder();

    sb.append(MSG.PROOF_SEARCH_INFO.INFO_SEPARATOR);
    sb.append("\n");
    sb.append(String.format(MSG.PROOF_SEARCH_INFO.PROVER_DETAILS,
        proofSearchData.getProofSearchData().getGbuProver().getProverName().getDetailedName()));
    sb.append("\n");

    sb.append(String.format(MSG.PROOF_SEARCH_INFO.TEST_DETAILS,
        proofSearchData.problemDescription().getProblemName(), //
        proofSearchData.problemDescription().getProblemStatus().name(), //
        proofSearchData.proofSearchResult(), // 
        proofSearchData.testStatus()));
    sb.append("\n");

    sb.append(String.format(MSG.PROOF_SEARCH_INFO.STAT_STRING, //
        proofSearchData.getProofSearchData().getNumberOfGeneratedNodes(), //
        proofSearchData.getProofSearchData().getNumberOfRestoredBacktrackPoints(), //
        proofSearchData.getProofSearchData().getNumberOfRestoredBrenchPoints()));
    sb.append("\n");

    // ADD proof search details
    //    {
    //      StringBuilder psdsb = proofSearchData.proofSearchDetails().getDetailsDescription();
    //      String[] lines = psdsb.toString().split("\\n");
    //      for (String line : lines)
    //        sb.append(
    //            String.format(MSG.PROOF_SEARCH_INFO.PROOF_SEARCH_DETAILS_LINE_PREFIX, line) + "\n");
    //    }

    long proofSearch_time = proofSearchData.proofSearchTime();
    long buildInitialNodeSet_time = proofSearchData.initialNodeSetConstructionTime();
    long problemReading_time = proofSearchData.problemParsingTime();
    long totalProof_time = buildInitialNodeSet_time + proofSearch_time + problemReading_time;
    sb.append(String.format(MSG.PROOF_SEARCH_INFO.TIMINGS_DETAILS, proofSearch_time,
        buildInitialNodeSet_time, problemReading_time));
    sb.append("\n");
    String convertedTime = buildTimeString(totalProof_time);
    if (convertedTime == null)
      sb.append(String.format(MSG.PROOF_SEARCH_INFO.TOTAL_TIME_1, totalProof_time));
    else
      sb.append(String.format(MSG.PROOF_SEARCH_INFO.TOTAL_TIME_2, totalProof_time,
          convertedTime.toString()));
    sb.append("\n");

    sb.append(MSG.PROOF_SEARCH_INFO.INFO_SEPARATOR);

    MSGManager.info(sb.toString());
  }

  private void testset_updateTestSetDetails(TestSetDetails testsetDetails,
      FrjSingleProblemExecutionData singeExecutionData) {

    testsetDetails.numberOfTests++;

    // timings
    testsetDetails.totalProblemParsingTime += singeExecutionData.problemParsingTime();
    testsetDetails.totalInitalNodeSetConstructionTime +=
        singeExecutionData.initialNodeSetConstructionTime();
    testsetDetails.totalProofSearchTime += singeExecutionData.proofSearchTime();

    // proof search result
    switch (singeExecutionData.proofSearchResult()) {
    case FAILURE:
      testsetDetails.unsuccesfulProofSearch++;
      break;
    case SUCCESS:
      testsetDetails.succesfullProofSearch++;
      break;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }

    // problem status
    switch (singeExecutionData.problemProvabilityStatus()) {
    case PROVABLE:
      testsetDetails.provableProblems++;
      break;
    case UNKNOWN:
      testsetDetails.unknownProblems++;
      break;
    case UNPROVABLE:
      testsetDetails.unprovableProblems++;
      break;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }

    // test
    TestStatus testStatus = singeExecutionData.testStatus();
    switch (testStatus) {
    case UNCHECKED:
      testsetDetails.uncheckedTests++;
      break;
    case PASSED:
      testsetDetails.successfulTests++;
      break;
    case FAILED:
      testsetDetails.failedTests++;
      break;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  private void testset_printSingleTestInfo(FrjSingleProblemExecutionData proofSearchData) {
    // print result on output
    MSGManager.info(testset_buildConciseTimingDescription(proofSearchData));
  }

  private String testset_buildConciseTimingDescription(
      FrjSingleProblemExecutionData proofSearchData) {
    long proofSearch_time = proofSearchData.proofSearchTime();
    long buildInitialNodeSet_time = proofSearchData.initialNodeSetConstructionTime();
    long problemReading_time = proofSearchData.problemParsingTime();
    long totalProof_time = buildInitialNodeSet_time + proofSearch_time;
    return String.format(MSG.LAUNCHER.TESTSET_INFO.SINGLE_OUTPUT_RESULT, //
        proofSearchData.problemProvabilityStatus().name(), //
        proofSearchData.proofSearchResult().name(), //
        proofSearchData.testStatus().name(), // test result
        proofSearchData.testStatus() == TestStatus.FAILED ? "<<==================" : "",
        // 2nd LINE PROOF-SEARCH DETAILS
        proofSearchData.proofSearchDetails().getNumberOfIterations(), // iterations,
        proofSearchData.proofSearchDetails().getNumberOAppliedRules(), // num. of applied rules
        proofSearchData.proofSearchDetails().getNumberOfProvedSequents(), // num. of proved sequents,
        // 3rd LINE TIMES
        buildSecondBasedString(totalProof_time + problemReading_time), // proof search time + initial node set construction
        buildSecondBasedString(proofSearch_time), // execution time,
        buildSecondBasedString(buildInitialNodeSet_time), // initial node set construction
        buildSecondBasedString(problemReading_time) // parsing problem time
    );
  }

  /**
   * Prints the report of a testset execution on the standard output and in the
   * testset logfile
   */
  private void testset_printReport(LauncherExecConfiguration currentConfiguration,
      TestSetDetails testsetDetails) {
    // close the temp file
    testsetDetails.pw4TempFile.flush();
    testsetDetails.pw4TempFile.close();

    // build the log file name
    String logFileName = LauncherExecConfiguration.TESTSET_FILE_PREFIX
        + (new SimpleDateFormat("yyMMdd_HHmmss")).format(testsetDetails.startTime)
        + LauncherExecConfiguration.TESTSET_FILE_SUFFIX;

    // create report file
    testsetDetails.logFile = new File(currentConfiguration.getLogDir(), logFileName);
    try {
      testsetDetails.pw4TestsetFile =
          new PrintWriter(new BufferedWriter(new FileWriter(testsetDetails.logFile)));
    } catch (IOException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.TESTSET_FILE_CANNOT_BE_CREATED,
          testsetDetails.logFile.getAbsolutePath(), e.getMessage());
      System.exit(1);
    }

    // WRITE PREAMBLE
    StringBuffer filePreamble = new StringBuffer();
    {
      filePreamble.append(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROVER_NAME));
      filePreamble.append("\n");
    }

    StringBuffer detailsStr = new StringBuffer();
    {
      detailsStr.append(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROBLEMS,
          testsetDetails.numberOfTests, testsetDetails.provableProblems,
          testsetDetails.unprovableProblems, testsetDetails.unknownProblems));
      detailsStr.append("\n");
      detailsStr
          .append(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_TEST, testsetDetails.failedTests,
              testsetDetails.successfulTests, testsetDetails.uncheckedTests));
      detailsStr.append("\n");

      detailsStr.append(String.format(MSG.LAUNCHER.TESTSET_INFO.TIMINGS_DETAILS,
          buildSecondBasedString(testsetDetails.totalProofSearchTime), //
          buildSecondBasedString(testsetDetails.totalInitalNodeSetConstructionTime), //
          buildSecondBasedString(testsetDetails.totalProblemParsingTime)));
      detailsStr.append("\n");
    }

    { // PROOF TIME STRING
      long totalProofTime =
          testsetDetails.totalInitalNodeSetConstructionTime + testsetDetails.totalProofSearchTime;
      String convertedProofTime = buildTimeString(totalProofTime);
      String strTime = "";
      if (convertedProofTime == null)
        strTime = String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROOF_TIME_1,
            buildSecondBasedString(totalProofTime));
      else
        strTime = String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROOF_TIME_2,
            buildSecondBasedString(totalProofTime), convertedProofTime);
      detailsStr.append(strTime);
      detailsStr.append("\n");
    }

    { // TOTAL TIME STRINGa
      long totaltime = testsetDetails.totalInitalNodeSetConstructionTime
          + testsetDetails.totalProofSearchTime + testsetDetails.totalProblemParsingTime;
      String convertedTotalTime = buildTimeString(totaltime);
      String strTime = "";
      if (convertedTotalTime == null)
        strTime = String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_TOTAL_TIME_1,
            buildSecondBasedString(totaltime));
      else
        strTime = String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_TOTAL_TIME_2,
            buildSecondBasedString(totaltime), convertedTotalTime);
      detailsStr.append(strTime);
    }

    // WRITE FILE
    {
      testsetDetails.pw4TestsetFile.println(filePreamble.toString());
      testsetDetails.pw4TestsetFile.println(detailsStr.toString());
      StringBuffer toTest = new StringBuffer();
      toTest.append(MSG.LAUNCHER.TESTSET_INFO.TESTSET_LOGFILE_SEPARATOR);
      toTest.append("\n");
      toTest.append(MSG.LAUNCHER.TESTSET_INFO.PROBLEMS_PREAMBLE);
      toTest.append("\n");
      toTest.append(MSG.LAUNCHER.TESTSET_INFO.TESTSET_LOGFILE_ROW_SEPARATOR);
      testsetDetails.pw4TestsetFile.println(toTest.toString());
      // Copy from tmpfile
      try {
        BufferedReader br = new BufferedReader(new FileReader(testsetDetails.tempFile));
        String line;
        while ((line = br.readLine()) != null)
          testsetDetails.pw4TestsetFile.println(line);
        br.close();
        testsetDetails.tempFile.delete();
      } catch (IOException e) {
        LOG.error(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_LOGFILE_CANNOT_BE_OPENED,
            e.getMessage()));
        System.exit(1);
      }
    }
    testsetDetails.pw4TestsetFile.flush();
    testsetDetails.pw4TestsetFile.close();

    StringBuffer outPreamble = new StringBuffer();
    outPreamble.append(MSG.LAUNCHER.INFO.INFO_SEPARATOR);
    outPreamble.append("\n");
    outPreamble.append(String.format(MSG.LAUNCHER.TESTSET_INFO.TESTSET_PROVER_NAME));

    MSGManager.info(outPreamble.toString());
    MSGManager.info(detailsStr.toString());
    MSGManager.info(MSG.LAUNCHER.INFO.INFO_SEPARATOR);
  }

  private String buildSecondBasedString(long miliSeconds) {
    int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds);
    int mil = (int) miliSeconds % 1000;

    return String.format(MSG.LAUNCHER.TIME_STR.JTABWB_TIME_STRING, sec, mil);
  }

  /**
   * Launch the application with the arguments specified on the command line.
   * 
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    Main main = new Main();
    main.start(args);
  }

  private String buildTimeString(long miliSeconds) {
    int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds);
    int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
    int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
    int mil = (int) miliSeconds % 1000;

    if (hrs == 0 && min == 0 && sec == 0)
      return null;
    else
      return String.format("%02d:%02d:%02d + %d", hrs, min, sec, mil);
  }

}
