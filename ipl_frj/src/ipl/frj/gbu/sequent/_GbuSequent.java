package ipl.frj.gbu.sequent;

import ipl.frj.sequent.SequentType;
import jtabwbx.prop.formula._SingleSuccedentSequent;

public interface _GbuSequent extends _SingleSuccedentSequent {
  
  
  /**
   * Returns true iff this is an irregular sequent.
   * 
   * @return true iff this is an irregular sequent.
   */
  public boolean isIrregular();

  /**
   * Returns true iff this is n regular sequent.
   * 
   * @return true iff this is n regular sequent.
   */
  public boolean isRegular();

  
  /**
   * Returns the type of this sequent.
   * 
   * @return the type of this sequent.
   */
  public SequentType type();
  
  
}
