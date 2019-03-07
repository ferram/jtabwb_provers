package ipl.frj.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.util.MSGManager;

public class TestSetDetails {

  TestSetDetails(LauncherExecConfiguration configuration) {
    this.startTime = new Date();
    try {
      this.tempFile = File.createTempFile("tmp-testset-frj-", ".tmp", configuration.getLogDir());
      this.pw4TempFile = new PrintWriter(new BufferedWriter(new FileWriter(this.tempFile)));
    } catch (IOException e) {
      MSGManager.error(MSG.LAUNCHER.ERROR.TESTSET_TMP_FILE_CANNOT_BE_CREATEd, e.getMessage());
      System.exit(1);
    }

  }

  File tempFile = null;
  File logFile = null;
  PrintWriter pw4TempFile = null;
  PrintWriter pw4TestsetFile = null;
  Date startTime = null;
  int numberOfTests = 0;
  int provableProblems = 0;
  int unprovableProblems = 0;
  int unknownProblems = 0;
  int succesfullProofSearch = 0;
  int unsuccesfulProofSearch = 0;
  int successfulTests = 0;
  int failedTests = 0;
  int uncheckedTests = 0;
  long totalProofSearchTime = 0;
  long totalProblemParsingTime = 0;
  long totalInitalNodeSetConstructionTime = 0;
  LinkedList<FrjProofSearchDetails> listOfProofSearchDetails = new LinkedList<FrjProofSearchDetails>();

  void add(FrjProofSearchDetails psDetails){
    listOfProofSearchDetails.addLast(psDetails);
  }
  
  FrjProofSearchDetails getLastProofSearchDetails(){
    return listOfProofSearchDetails.getLast();
  }
  
  
  
}
