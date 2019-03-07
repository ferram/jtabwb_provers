/*******************************************************************************
 * Copyright (C) 2013, 2016 Mauro Ferrari This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ipl.frj.launcher;

import ipl.frj.kripke.KripkeModel;
import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic._FrjProver;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.Formula;

/**
 * A bunch of data about a proof-search execution.
 * 
 * @author Mauro Ferrari
 */
public class FrjSingleProblemExecutionData {

  public FrjSingleProblemExecutionData(ProblemReaderData problemParsingdata,
      InitialNodeSetData initialNodeSetConstructionData, FrjProofSearchData proofSearchData) {
    super();
    this.problemParsingData = problemParsingdata;
    this.frjInitialNodeSetConstructionData = initialNodeSetConstructionData;
    this.proofSearchData = proofSearchData;
    this.sequentsDB = proofSearchData.getProver().getProofSearchDetails().getProvedSequentsDB();
    if (this.sequentsDB instanceof _ProvedSequentsDBWithBwSubsumption)
      isSubsumptionApplied = true;
  }

  private ProblemReaderData problemParsingData;
  private InitialNodeSetData frjInitialNodeSetConstructionData;
  private FrjProofSearchData proofSearchData;
  private _ProvedSequentsDB sequentsDB;
  private KripkeModel kripkeModel = null;
  private boolean isSubsumptionApplied = false;

  /**
   * @return the prover
   */
  public _FrjProver getProver() {
    return this.proofSearchData.getProver();
  }

  /**
   * @return the sequentsDB
   */
  public _ProvedSequentsDB getSequentsDB() {
    return this.sequentsDB;
  }

  /**
   * @return the isSubsumptionApplied
   */
  public boolean isSubsumptionApplied() {
    return this.isSubsumptionApplied;
  }

  /**
   * Returns the problem description.
   * 
   * @return the problem description.
   */
  public ProblemDescription problemDescription() {
    return problemParsingData.getProblemDescription();
  }

  /**
   * Returns the time required by the proof-search in milliseconds.
   * 
   * @return the time required by the proof-search.
   */
  public long proofSearchTime() {
    return proofSearchData.proofSearchTime();
  }

  /**
   * Returns the time required to build the initial node set.
   * 
   * @return the time needed to build the initial node set.
   */
  public long initialNodeSetConstructionTime() {
    return frjInitialNodeSetConstructionData.initialNodeSetConstructionTime();
  }

  /**
   * Returns the time required to parse the problem description in milliseconds.
   * 
   * @return the time needed to parse the input problem.
   */
  public long problemParsingTime() {
    return problemParsingData.problemParsingTime();
  }

  public Formula goal() {
    return frjInitialNodeSetConstructionData.getGoal();
  }

  public ProvabilityStatus problemProvabilityStatus() {
    return problemParsingData.getProblemDescription().getProblemStatus();
  }

  public ProofSearchResult proofSearchResult() {
    return proofSearchData.getProofSearchDetails().getProofSearchResult();
  }

  public FrjProofSearchDetails proofSearchDetails() {
    return proofSearchData.getProofSearchDetails();
  }

  public TestStatus testStatus() {
    return TestStatus.getFrjTestStatus(problemProvabilityStatus(), proofSearchResult());
  }

  /**
   * 
   * @return the Kripke Model
   */
  public KripkeModel getKripkeModel() {
    return this.kripkeModel;
  }

  /**
   * @param kripkeModel the Kripke Model to set
   */
  public void setKripkeModel(KripkeModel kripkeModel) {
    this.kripkeModel = kripkeModel;
  }

  /**
   * @return the problemParsingData
   */
  public ProblemReaderData getProblemParsingData() {
    return this.problemParsingData;
  }

  /**
   * @param problemParsingData the problemParsingData to set
   */
  public void setProblemParsingData(ProblemReaderData problemParsingData) {
    this.problemParsingData = problemParsingData;
  }

  /**
   * @return the initialNodeSetConstructionData
   */
  public InitialNodeSetData getInitialNodeSetConstructionData() {
    return this.frjInitialNodeSetConstructionData;
  }

  /**
   * @param initialNodeSetConstructionData the initialNodeSetConstructionData to
   * set
   */
  public void setInitialNodeSetConstructionData(InitialNodeSetData initialNodeSetConstructionData) {
    this.frjInitialNodeSetConstructionData = initialNodeSetConstructionData;
  }

  /**
   * @return the proofSearchData
   */
  public FrjProofSearchData getProofSearchData() {
    return this.proofSearchData;
  }

  /**
   * @param proofSearchData the proofSearchData to set
   */
  public void setProofSearchData(FrjProofSearchData proofSearchData) {
    this.proofSearchData = proofSearchData;
  }

}
