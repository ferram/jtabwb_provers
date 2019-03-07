package ipl.frj.tp.basic;

import java.util.Collection;

import ipl.frj.sequent.FrjRegularSequent;
import jtabwb.engine.ProofSearchResult;
import jtabwbx.prop.formula.Formula;

public class FrjProofSearchDetails {

  private int iterations;
  private int numberOfAppliedRules;
  private _ProvedSequentsDB generatedSequents;
  private ProofSearchResult proofSearchResult;
  private Formula goal;

  /**
   * Returns the goal of the proof-search.
   * 
   * @return
   */
  public Formula getGoal() {
    return goal;
  }

  /**
   * 
   * @return the numperOfIterations
   */
  public int getNumberOfIterations() {
    return this.iterations;
  }

  /**
   * @return the numperOfProvedSequents
   */
  public int getNumberOfProvedSequents() {
    return generatedSequents.numberOfProvedSequents(ProvedSequentsTables.GLOBAL);
  }

  /**
   * @return the number of applied rules.
   */
  public int getNumberOAppliedRules() {
    return numberOfAppliedRules;
  }

  public Collection<FrjRegularSequent> getSuccessSequents() {
    return generatedSequents.getSuccessSequents();
  }

  /**
   * @return the proofSearchResult
   */
  public ProofSearchResult getProofSearchResult() {
    return this.proofSearchResult;
  }

  /**
   * Returns the table of proved sequents.
   * 
   * @return the table of proved sequents.
   */
  public _ProvedSequentsDB getProvedSequentsDB() {
    return generatedSequents;
  }

  /**
   * Returns the number of rules applied performing the proof-search.
   * 
   * @return the number of applied rules.
   */
  public int getNumberOfRuleApplications() {
    return numberOfAppliedRules;
  }

  /**
   * @param iterations the iterations to set
   */
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  /**
   * @param numberOfAppliedRules the numberOfAppliedRules to set
   */
  public void setNumberOfAppliedRules(int numberOfAppliedRules) {
    this.numberOfAppliedRules = numberOfAppliedRules;
  }

  /**
   * @param generatedSequents the generatedSequents to set
   */
  public void setGeneratedSequents(_ProvedSequentsDB generatedSequents) {
    this.generatedSequents = generatedSequents;
  }

  /**
   * @param proofSearchResult the proofSearchResult to set
   */
  public void setProofSearchResult(ProofSearchResult proofSearchResult) {
    this.proofSearchResult = proofSearchResult;
  }

  /**
   * @param goal the goal to set
   */
  public void setGoal(Formula goal) {
    this.goal = goal;
  }

  /**
   * Returns the array containing the lines of the description.
   * 
   * @return
   */
  public StringBuilder getDetailsDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format(MSG.PROOF_SEARCH_DETAILS.INFO_1, iterations, numberOfAppliedRules,
        getNumberOfProvedSequents()));
    sb.append("\n");
    return sb;
  }

}
