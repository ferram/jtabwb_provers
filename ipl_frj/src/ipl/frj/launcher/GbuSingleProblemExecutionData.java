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

import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwb.engine.Trace;
import jtabwb.engine._Prover;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.Formula;

/**
 * A bunch of data about a proof-search execution.
 * 
 * @author Mauro Ferrari
 */
public class GbuSingleProblemExecutionData {

  public GbuSingleProblemExecutionData(ProblemReaderData problemParsingData,
      InitialNodeSetData initialNodeSetConstructionData, GbuProofSearchData proofSearchData) {
    super();
    this.problemParsingData = problemParsingData;
    this.initialNodeSetConstructionData = initialNodeSetConstructionData;
    this.proofSearchData = proofSearchData;
  }

  private ProblemReaderData problemParsingData;
  private InitialNodeSetData initialNodeSetConstructionData;
  private GbuProofSearchData proofSearchData;
  private boolean isSubsumptionApplied = false;

  /**
   * @return the proof-search data
   */
  public GbuProofSearchData getProofSearchData() {
    return this.proofSearchData;
  }

  /**
   * @return the proof-search data
   */
  public _Prover getProver() {
    return this.proofSearchData.getGbuProver();
  }
  
  /**
   * @return the trace of proof-search or <code>null</code> if the engine is
   * note executed in TRACE mode.
   */
  public Trace getTrace() {
    return this.getProofSearchData().getEngine().getTrace();
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
    return initialNodeSetConstructionData.initialNodeSetConstructionTime();
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
    return initialNodeSetConstructionData.getGoal();
  }

  public ProvabilityStatus problemProvabilityStatus() {
    return problemParsingData.getProblemDescription().getProblemStatus();
  }

  public ProofSearchResult proofSearchResult() {
    return proofSearchData.getEngine().getResult();
  }

  public TestStatus testStatus() {
    return TestStatus.getGbuTestStatus(problemProvabilityStatus(), proofSearchResult());
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
    return this.initialNodeSetConstructionData;
  }

  /**
   * @param initialNodeSetConstructionData the initialNodeSetConstructionData to
   * set
   */
  public void setInitialNodeSetConstructionData(InitialNodeSetData initialNodeSetConstructionData) {
    this.initialNodeSetConstructionData = initialNodeSetConstructionData;
  }

}
