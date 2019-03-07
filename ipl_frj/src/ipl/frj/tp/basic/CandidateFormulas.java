package ipl.frj.tp.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class CandidateFormulas {

  // index for treated formula types
  private static final int TYPES = 4;
  private static final int TYPE_ATOMIC = 0;
  private static final int TYPE_AND = 1;
  private static final int TYPE_OR = 2;
  private static final int TYPE_IMPLIES = 3;

  private static final String[] TYPES_DESC =
      new String[] { "ATOMIC_WFF", "AND_WFF", "OR_WFF", "IMPLIES_WFF" };

  public CandidateFormulas(Formula goal) {
    super();
    this.goal = goal;
    this.maxSize = goal.size();
    this.formulaFactory = (FrjFormulaFactory) goal.getFactory();
    this.left_atomic = new BitSetOfFormulas(formulaFactory);
    this.left_implicative = new BitSetOfFormulas(formulaFactory);
    this.right = new BitSetOfFormulas[TYPES];
    for (int i = 0; i < TYPES; i++)
      this.right[i] = new BitSetOfFormulas(formulaFactory);
    this.buildCandidateRight(goal);
    this.left_atomic_union_implicative =
        BitSetOfFormulas.orNullAsEmpty(left_atomic, left_implicative);
  }

  private final FrjFormulaFactory formulaFactory;
  private final BitSetOfFormulas left_atomic_union_implicative;
  private final BitSetOfFormulas left_implicative;
  private final BitSetOfFormulas left_atomic;
  private final BitSetOfFormulas[] right;
  private final Formula goal; // the goal of proof-search
  private final int maxSize; // size of the greatest formula

  private void addRight(Formula wff) {
    if (wff.isAtomic())
      right[TYPE_ATOMIC].add(wff);
    else {
      switch (wff.mainConnective()) {
      case AND:
        right[TYPE_AND].add(wff);
        break;
      case OR:
        right[TYPE_OR].add(wff);
        break;
      case IMPLIES:
        right[TYPE_IMPLIES].add(wff);
        break;
      default:
        throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
            "Formula type " + wff.mainConnective());
      }
    }
  }

  /**
   * Builds the set of candidate right formulas, which are the negative (or
   * F-signed) sub-formulas of the goal.
   * 
   * @param wff the formula to treat.
   */
  private void buildCandidateRight(Formula wff) {
    if (wff.isAtomic()) {
      addRight(wff);
      return;
    }
    switch (wff.mainConnective()) {
    case AND:
    case OR: {
      addRight(wff);
      buildCandidateRight(wff.immediateSubformulas()[0]);
      buildCandidateRight(wff.immediateSubformulas()[1]);
      return;
    }
    case IMPLIES: {
      addRight(wff);
      buildCandidateLeft(wff.immediateSubformulas()[0]);
      buildCandidateRight(wff.immediateSubformulas()[1]);
      return;
    }
    default:
      throw new ImplementationError(
          wff.mainConnective() + " - " + ImplementationError.CASE_NOT_IMPLEMENTED);
    }

  }

  /**
   * Builds the set of candidate left formulas, which are the positive (or
   * T-signed) sub-formulas of the goal.
   * 
   * @param wff the formula to treat.
   */
  private void buildCandidateLeft(Formula wff) {
    if (wff.isAtomic()) {
      if (!wff.isFalse() && !wff.isTrue())
        left_atomic.add(wff);
      return;
    }
    switch (wff.mainConnective()) {
    case AND:
    case OR: {
      buildCandidateLeft(wff.immediateSubformulas()[0]);
      buildCandidateLeft(wff.immediateSubformulas()[1]);
      return;
    }
    case IMPLIES: {
      this.left_implicative.add(wff);
      buildCandidateRight(wff.immediateSubformulas()[0]);
      buildCandidateLeft(wff.immediateSubformulas()[1]);
      return;
    }
    default:
      throw new ImplementationError(
          wff.mainConnective() + " - " + ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  public boolean contains(Formula wff) {
    if (wff.isAtomic())
      return left_atomic.contains(wff);
    else
      switch (wff.mainConnective()) {
      case IMPLIES:
        return left_implicative.contains(wff);
      default:
        throw new ImplementationError(ImplementationError.CONTRACT_VIOLATION_arg,
            "connective " + wff.mainConnective().toString());
      }
  }

  /**
   * Returns the goal formula.
   * 
   * @return returns the goal formula.
   */
  public Formula getGoal() {
    return goal;
  }

  /**
   * Returns the formulas (atomic and implicative) that can occur in the left
   * side of a sequent.
   * 
   * @return the candidate left formulas.
   */
  public BitSetOfFormulas getLeftFormulas() {
    return this.left_atomic_union_implicative;
  }

  /**
   * Returns the atomic formulas that can occur in the left side of a sequent.
   * 
   * @return the candidate left atomic formulas.
   */
  public BitSetOfFormulas getLeftAtomic() {
    return this.left_atomic;
  }

  /**
   * Returns the implicative formulas that can occur in the left side of a
   * sequent.
   * 
   * @return the candidate left implicative formulas.
   */
  public BitSetOfFormulas getLeftImplicative() {
    return this.left_implicative;
  }

  /**
   * Returns the right candidates of the spedified type.
   * 
   * @return the right candidates of the specified type.
   */
  public BitSetOfFormulas getRight(FormulaType type) {
    switch (type) {
    case ATOMIC_WFF:
      return right[TYPE_ATOMIC];
    case AND_WFF:
      return right[TYPE_AND];
    case OR_WFF:
      return right[TYPE_OR];
    case IMPLIES_WFF:
      return right[TYPE_IMPLIES];
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg, "Type " + type);
    }
  }

  // CACHE FOR CLOSURE
  private HashMap<BitSetOfFormulas, HashSet<Formula>> cache_closure_IN =
      new HashMap<BitSetOfFormulas, HashSet<Formula>>();
  private HashMap<BitSetOfFormulas, HashSet<Formula>> cache_closure_NOT_IN =
      new HashMap<BitSetOfFormulas, HashSet<Formula>>();

  /**
   * Return <code>true</code> iff <code>wff</code> belong to the closure of
   * <code>set</code>.
   * 
   * @param wff
   * @param set
   * @return <code>true</code> iff <code>wff</code> belong to the closure of
   * <code>set</code>.
   */
  public boolean isInClosure(Formula wff, BitSetOfFormulas set) {
    // check for cached values
    HashSet<Formula> cahed_in = cache_closure_IN.get(set);
    if (cahed_in != null && cahed_in.contains(wff))
      return true;
    HashSet<Formula> cahed_not_in = cache_closure_NOT_IN.get(set);
    if (cahed_not_in != null && cahed_not_in.contains(wff))
      return false;

    // NOT in cache
    boolean result = false;
    if (set.contains(wff))
      result = true;
    else {
      if (wff.isAtomic())
        result = false;
      else {
        switch (wff.mainConnective()) {
        case AND:
          result = isInClosure(wff.immediateSubformulas()[0], set)
              && isInClosure(wff.immediateSubformulas()[1], set);
          break;
        case OR:
          result = isInClosure(wff.immediateSubformulas()[0], set)
              || isInClosure(wff.immediateSubformulas()[1], set);
          break;
        case IMPLIES:
          result = isInClosure(wff.immediateSubformulas()[1], set);
          break;
        default:
          throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
        }
      }
    }
    if (result) {
      if (cahed_in == null) {
        cahed_in = new HashSet<Formula>();
        cache_closure_IN.put(set, cahed_in);
      }
      cahed_in.add(wff);
    } else {
      if (cahed_not_in == null) {
        cahed_not_in = new HashSet<Formula>();
        cache_closure_NOT_IN.put(set, cahed_not_in);
      }
      cahed_not_in.add(wff);
    }

    return result;
  }

  /**
   * TODO: ottimizzare (cache ??)
   * Return <code>true</code> iff <code>set</code> is a subset of the closure of
   * <code>other</code>.
   * 
   * @param set
   * @param other
   * @return <code>true</code> iff <code>set</code> is included in the closure
   * of <code>otehr</code>.
   */
  public boolean isInClosure(BitSetOfFormulas set, BitSetOfFormulas other) {
    for(Formula wff: set)
      if (!isInClosure(wff, other))
        return false;
    
    return true;
  }

  // CACHE FOR minimal coverings
  HashMap<Formula, LinkedList<BitSetOfFormulas>> cached_minimalCoverings =
      new HashMap<Formula, LinkedList<BitSetOfFormulas>>();

  /**
   * TODO: doc
   * 
   * @param wff
   * @return
   */
  public Collection<BitSetOfFormulas> buildMinimalClosureCoveringSets(FrjIrregularSequent prem,
      Formula wff) {
    BitSetOfFormulas candidateCovering =
        BitSetOfFormulas.orNullAsEmpty(prem.stable(), prem.nonStable());

    if (candidateCovering == null)
      return null;

    return _buildMinimalCovering(candidateCovering, wff);
  }

  private LinkedList<BitSetOfFormulas> _buildMinimalCovering(BitSetOfFormulas set, Formula wff) {
    if (set.contains(wff)) {
      BitSetOfFormulas bitset = new BitSetOfFormulas(formulaFactory);
      bitset.add(wff);
      LinkedList<BitSetOfFormulas> result = new LinkedList<BitSetOfFormulas>();
      result.add(bitset);
      if (set.cardinality() == 1)
        return result;
      else {
        BitSetOfFormulas newCandidateCovering = set.clone();
        newCandidateCovering.remove(wff);
        LinkedList<BitSetOfFormulas> partial = _buildMinimalCovering(newCandidateCovering, wff);
        if (partial != null)
          result.addAll(partial);
        return result;
      }
    }
    if (wff.isAtomic())
      return null;
    else {
      switch (wff.mainConnective()) {
      case AND: {
        LinkedList<BitSetOfFormulas> res0 =
            _buildMinimalCovering(set, wff.immediateSubformulas()[0]);
        LinkedList<BitSetOfFormulas> res1 =
            _buildMinimalCovering(set, wff.immediateSubformulas()[1]);
        if (res0 == null || res1 == null)
          return null;
        else {
          LinkedList<BitSetOfFormulas> result = new LinkedList<BitSetOfFormulas>();
          for (BitSetOfFormulas bs0 : res0)
            for (BitSetOfFormulas bs1 : res1)
              result.add(BitSetOfFormulas.orNullAsEmpty(bs0, bs1));
          return result;
        }
      }
      case OR:
        // generate the alternative covering
        LinkedList<BitSetOfFormulas> res0 =
            _buildMinimalCovering(set, wff.immediateSubformulas()[0]);
        LinkedList<BitSetOfFormulas> res1 =
            _buildMinimalCovering(set, wff.immediateSubformulas()[1]);
        if (res0 == null)
          return res1;
        else if (res1 == null)
          return res0;
        else {
          res0.addAll(res1);
          return res0;
        }
      case IMPLIES:
        return _buildMinimalCovering(set, wff.immediateSubformulas()[1]);
      default:
        throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
      }
    }
  }

  public Collection<BitSetOfFormulas> buildMaximalClosureNotCoveringSets(FrjRegularSequent prem,
      Formula wff) {
    BitSetOfFormulas candidateInTheta = new BitSetOfFormulas(formulaFactory);
    for (Formula candidate : left_atomic_union_implicative) {
      if (isInClosure(candidate, prem.left()))
        candidateInTheta.add(candidate);
    }

    LinkedList<BitSetOfFormulas> result = _buildMaximalNotCovering(candidateInTheta, wff);
    return result;
  }

  private LinkedList<BitSetOfFormulas> _buildMaximalNotCovering(BitSetOfFormulas theta,
      Formula wff) {
    LinkedList<BitSetOfFormulas> result = new LinkedList<BitSetOfFormulas>();
    if (theta.contains(wff)) {
      BitSetOfFormulas bs = theta.clone();
      bs.remove(wff);
      result.add(bs);
      return result;
    } else {
      if (wff.isAtomic()) {
        result.add(theta.clone());
        return result;
      } else {
        switch (wff.mainConnective()) {
        case AND: {
          LinkedList<BitSetOfFormulas> res0 =
              _buildMaximalNotCovering(theta, wff.immediateSubformulas()[0]);
          LinkedList<BitSetOfFormulas> res1 =
              _buildMaximalNotCovering(theta, wff.immediateSubformulas()[1]);
          if (res0 == null)
            return res1;
          else if (res1 == null)
            return res0;
          else {
            res0.addAll(res1);
            return res0;
          }
        }
        case OR:
          // generate the alternative covering
          LinkedList<BitSetOfFormulas> res0 =
              _buildMaximalNotCovering(theta, wff.immediateSubformulas()[0]);
          LinkedList<BitSetOfFormulas> res1 =
              _buildMaximalNotCovering(theta, wff.immediateSubformulas()[1]);
          if (res0 == null || res1 == null)
            return null;
          else {
            for (BitSetOfFormulas one : res0)
              for (BitSetOfFormulas two : res1) {
                BitSetOfFormulas intersection = BitSetOfFormulas.andNullAsEmpty(one, two);
                if (intersection != null)
                  result.add(intersection);
              }
            return (result.isEmpty() ? null : result);
          }
        case IMPLIES:
          return _buildMaximalNotCovering(theta, wff.immediateSubformulas()[1]);
        default:
          throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
        }
      }
    }

  }

  public int getJoinATMaxDegree() {
    return left_implicative == null ? 0 : left_implicative.cardinality();
  }

  public int getJoinORMaxDegree() {
    return left_implicative == null ? 2 : left_implicative.cardinality() + 2;
  }

  @Override
  public String toString() {

    StringBuffer sb_right = new StringBuffer();
    for (int i = 0; i < TYPES; i++)
      sb_right.append("-- " + TYPES_DESC[i] + ": " + right[i] + (i < TYPES - 1 ? "\n" : ""));

    return "-- >>>>>>>>>>> LEFT CANDIDATES --\n" + //
        "-- " + TYPES_DESC[TYPE_ATOMIC] + ": " + this.left_atomic + "\n" + // 
        "-- " + TYPES_DESC[TYPE_IMPLIES] + ": " + this.left_implicative + //
        "\n-- >>>>>>>>>>> RIGHT CANDIDATES (by type) --\n" + //
        sb_right.toString() //
    ;
  }

}
