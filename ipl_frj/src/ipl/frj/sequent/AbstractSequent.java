package ipl.frj.sequent;

import java.util.HashSet;

import ipl.frj.rules._FrjRule;

public abstract class AbstractSequent implements _FrjSequent {

  public AbstractSequent(SequentType type, int iteration, _FrjRule generatingRule) {
    super();
    this.sequentType = type;
    this.iteration = iteration;
    this.generatingRule = generatingRule;
  }
  
  //abstract void updatePremiseDependencies(_FrjSequent seq);

  
  HashSet<_FrjSequent> dependencies; 
  final SequentType sequentType;
  final int iteration;
  final _FrjRule generatingRule;
  private int index = -1;

  /**
   * @return the dependencies
   */
  public HashSet<_FrjSequent> getDependencies() {
    return this.dependencies;
  }

  @Override
  public int iteration() {
    return iteration;
  }

  @Override
  public _FrjRule generatingRule() {
    return generatingRule;
  }

  @Override
  public int getSequentProgessiveNumber() {
    return index;
  }

  @Override
  public void setSequentProgressiveNumber(int index) {
    this.index = index;
  }

  @Override
  public boolean isIrregular() {
    return sequentType == SequentType.IRREGULAR;
  }

  @Override
  public boolean isRegular() {
    return sequentType == SequentType.REGULAR;
  }

  @Override
  public SequentType type() {
    return sequentType;
  }

}
