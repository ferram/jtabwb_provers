package ipl.frj.gbu.tp;

import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwb.engine.ProverName;
import jtabwb.engine._Prover;
import jtabwb.engine._Strategy;
import jtabwb.util.ImplementationError;

/**
 * Prover for Gbu with saturated DB.
 * 
 * @author Mauro Ferrari
 *
 */
public class GbuProver implements _Prover {

  private static final String PROVER_NAME = "gbu-frj";
  private static final String PROVER_VERSION = "0.1";
  private static final String PROVER_VARIANT = null;
  private static final String PROVER_DESCRIPTION = "Gbu for FRJ-saturated DB.";

  public GbuProver(_ProvedSequentsDBWithBwSubsumption db) {
    super();
    this.db = db;
    this.proverName =
        new ProverName(PROVER_NAME, PROVER_VERSION, PROVER_VARIANT, PROVER_DESCRIPTION);
  }

  private final _ProvedSequentsDBWithBwSubsumption db;
  private final ProverName proverName;

  @Override
  public ProverName getProverName() {
    return proverName;
  }

  @Override
  public _Strategy getStrategy() {
    return new Strategy(db);
  }

  @Override
  public ProvabilityStatus statusFor(ProofSearchResult result) {
    switch (result) {
    case FAILURE:
      return ProvabilityStatus.UNPROVABLE;
    case SUCCESS:
      return ProvabilityStatus.PROVABLE;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

}
