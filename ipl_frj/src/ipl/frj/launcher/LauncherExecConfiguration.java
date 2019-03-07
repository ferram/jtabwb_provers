package ipl.frj.launcher;

import java.io.File;

import org.apache.commons.logging.impl.SimpleLog;

import ipl.frj.util.MSGManager;

public class LauncherExecConfiguration {

  public static final String LOG_DIR_NAME = "log";
  public static final String TESTSET_FILE_PREFIX = "testset-frj-";
  public static final String TESTSET_FILE_SUFFIX = ".log";

  private int log_mode = SimpleLog.LOG_LEVEL_OFF;
  private ExecutionMode executionMode = ExecutionMode.PLAIN;
  private AvailableProvers selectedProver;
  private AvailableSequentDB selectedSequentDB;
  private boolean generateLatex = false;
  private boolean generateKripkeStat = false;
  private boolean readFromStandardInput = false;
  private boolean execGbu = false;
  private String[] inputFileNames = null;
  private File logDir = null;

  /**
   * @return the lOG_MODE
   */
  public int logMode() {
    return this.log_mode;
  }

  public File getLogDir() {
    if (logDir == null)
      logDir = createLogDir();
    return logDir;
  }

  
  
  
  /**
   * @return the execGbu
   */
  public boolean execGbu() {
    return this.execGbu;
  }

  
  /**
   * @param execGbu the execGbu to set
   */
  public void setExecGbu(boolean execGbu) {
    this.execGbu = execGbu;
  }

  /**
   * @param log_mode the lOG_MODE to set
   */
  void setLogMode(int logMode) {
    this.log_mode = logMode;
  }

  public ExecutionMode executionMode() {
    return executionMode;
  }

  void setExecutuionMode(ExecutionMode mode) {
    this.executionMode = mode;
  }

  public boolean testSetMode() {
    return this.executionMode == ExecutionMode.TESTSET;
  }

  public boolean verboseMode() {
    return this.executionMode == ExecutionMode.VERBOSE;
  }

  public boolean debugMode() {
    return this.log_mode == SimpleLog.LOG_LEVEL_DEBUG;
  }

  /**
   * @return the selectedProver
   */
  public AvailableProvers getSelectedProver() {
    return this.selectedProver;
  }

  /**
   * @param selectedProver the selectedProver to set
   */
  public void setSelectedProver(AvailableProvers selectedProver) {
    this.selectedProver = selectedProver;
  }

  public AvailableSequentDB getSelectedSequentDB() {
    return this.selectedSequentDB;
  }

  /**
   * @param selectedProver the selectedProver to set
   */
  public void setSelectedSequentDB(AvailableSequentDB selectedDB) {
    this.selectedSequentDB = selectedDB;
  }

  /**
   * 
   * @return the generateLatex
   */
  public boolean generateLatex() {
    return this.generateLatex;
  }

  /**
   * @param generateLatex the generateLatex to set
   */
  public void setGenerateLatexOfProof(boolean generateLatex) {
    this.generateLatex = generateLatex;
  }

  /**
   * @return the generateKripkeStat
   */
  public boolean generateKripkeStat() {
    return this.generateKripkeStat;
  }

  /**
   * @return the readFromStandardInput
   */
  public boolean readFromStandardInput() {
    return this.readFromStandardInput;
  }

  /**
   * @param readFromStandardInput the readFromStandardInput to set
   */
  public void setReadFromStandardInput(boolean readFromStandardInput) {
    this.readFromStandardInput = readFromStandardInput;
  }

  /**
   * @param generateKripkeStat the generateKripkeStat to set
   */
  public void setGenerateKripkeStat(boolean generateKripkeStat) {
    this.generateKripkeStat = generateKripkeStat;
  }

  /**
   * @return the inputFileNames
   */
  public String[] getInputFileNames() {
    return this.inputFileNames;
  }

  /**
   * @param inputFileNames the inputFileNames to set
   */
  public void setInputFileNames(String[] inputFileNames) {
    this.inputFileNames = inputFileNames;
  }

  private File createLogDir() {
    File logDir = new File(LOG_DIR_NAME);
    if (!logDir.exists()) {
      try {
        logDir.mkdir();
        return logDir;
      } catch (SecurityException e) {
        MSGManager.error(MSG.LAUNCHER.ERROR.LOG_DIR_CANNOT_BE_CREATED, logDir.getAbsolutePath(),
            e.getMessage());
        return null;
      }
    } else {
      if (!logDir.isDirectory()) {
        MSGManager.error(MSG.LAUNCHER.ERROR.LOG_DIR_IS_NOT_A_DIR, logDir.getAbsolutePath());
        return null;
      }
    }
    return logDir;
  }

}
