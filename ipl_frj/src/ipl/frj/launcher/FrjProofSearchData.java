package ipl.frj.launcher;

import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic._FrjProver;

public class FrjProofSearchData {

  public FrjProofSearchData(_FrjProver prover, long proof_search_start_time,
      long proof_search_end_time) {
    super();
    this.prover = prover;
    this.proof_search_start_time = proof_search_start_time;
    this.proof_search_end_time = proof_search_end_time;
  }

  private _FrjProver prover;
  private long proof_search_start_time = 0;
  private long proof_search_end_time = -1;

  /**
   * @return the prover
   */
  public _FrjProver getProver() {
    return this.prover;
  }

  /**
   * @return the proofSearchDetails
   */
  public FrjProofSearchDetails getProofSearchDetails() {
    return prover.getProofSearchDetails();
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
