package ipl.frj.launcher;

import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwb.util.ImplementationError;

/**
 * Describes the status of a test.
 * 
 * @author Mauro Ferrari
 *
 */
enum TestStatus {
  UNCHECKED, PASSED, FAILED;

  static TestStatus getFrjTestStatus(ProvabilityStatus problemStatus,
      ProofSearchResult proofSearchResult) {
    switch (problemStatus) {
    case UNKNOWN:
      return UNCHECKED;
    case PROVABLE:
      if (proofSearchResult == ProofSearchResult.FAILURE)
        return TestStatus.PASSED;
      else
        return TestStatus.FAILED;
    case UNPROVABLE:
      if (proofSearchResult == ProofSearchResult.SUCCESS)
        return TestStatus.PASSED;
      else
        return TestStatus.FAILED;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }
  
  static TestStatus getGbuTestStatus(ProvabilityStatus problemStatus,
      ProofSearchResult proofSearchResult) {
    switch (problemStatus) {
    case UNKNOWN:
      return UNCHECKED;
    case PROVABLE:
      if (proofSearchResult == ProofSearchResult.FAILURE)
        return TestStatus.FAILED;
      else
        return TestStatus.PASSED;
    case UNPROVABLE:
      if (proofSearchResult == ProofSearchResult.SUCCESS)
        return TestStatus.FAILED;
      else
        return TestStatus.PASSED;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }
}
