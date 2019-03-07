package ipl.frj.launcher;

import ipl.frj.gbu.tp.GbuProver;
import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic._FrjProver;
import jtabwb.engine.Engine;

public class GbuProofSearchData {

  public GbuProofSearchData(GbuProver prover, Engine engine, long proof_search_start_time,
      long proof_search_end_time) {
    super();
    this.gbuProver = prover;
    this.engine = engine;
    this.proof_search_start_time = proof_search_start_time;
    this.proof_search_end_time = proof_search_end_time;
  }

  private GbuProver gbuProver;
  private Engine engine;
  private long proof_search_start_time = 0;
  private long proof_search_end_time = -1;

  /**
   * @return the prover
   */
  public Engine getEngine() {
    return this.engine;
  }

  
  public long getNumberOfGeneratedNodes(){
    return this.engine.getLastIterationInfo().getNumberOfGeneratedNodes();
  }
  
  public long getNumberOfRestoredBacktrackPoints(){
    return this.engine.getLastIterationInfo().getNumberOfRestoredBacktrackPoints();
  }
  
  public long getNumberOfRestoredBrenchPoints(){
    return this.engine.getLastIterationInfo().getNumberOfRestoredBranchPoints();
  }
  
  
  /**
   * @return the prover
   */
  public GbuProver getGbuProver() {
    return this.gbuProver;
  }
  
  /**
   * @return the proof_search_start_time
   */
  public long getProof_search_start_time() {
    return this.proof_search_start_time;
  }

  /**
   * @return the proof_search_end_time
   */
  public long getProof_search_end_time() {
    return this.proof_search_end_time;
  }

  /**
   * Returns the time required by the proof-search in milliseconds.
   * 
   * @return the time required by the proof-search.
   */
  public long proofSearchTime() {
    return proof_search_end_time - proof_search_start_time;
  }

}
