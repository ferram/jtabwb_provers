package ipl.frj.rules;

import jtabwbx.prop.formula.BitSetOfFormulas;

public class SigmaThetaUpsilon {

  public SigmaThetaUpsilon(BitSetOfFormulas sigma, BitSetOfFormulas theta,
      BitSetOfFormulas rightFormulas) {
    super();
    this.sigma = sigma;
    this.theta = theta;
    this.rightFormulas = rightFormulas;
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.rightFormulas == null) ? 0 : this.rightFormulas.hashCode());
      result = prime * result + ((this.sigma == null) ? 0 : this.sigma.hashCode());
      result = prime * result + ((this.theta == null) ? 0 : this.theta.hashCode());
      this.hashCode = result;
    }
  }

  BitSetOfFormulas sigma;
  BitSetOfFormulas theta;
  BitSetOfFormulas rightFormulas;
  private int hashCode;

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SigmaThetaUpsilon other = (SigmaThetaUpsilon) obj;
    if (this.rightFormulas == null) {
      if (other.rightFormulas != null)
        return false;
    } else if (!this.rightFormulas.equals(other.rightFormulas))
      return false;
    if (this.sigma == null) {
      if (other.sigma != null)
        return false;
    } else if (!this.sigma.equals(other.sigma))
      return false;
    if (this.theta == null) {
      if (other.theta != null)
        return false;
    } else if (!this.theta.equals(other.theta))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return "SigmaThetaUpsilon [hashCode=" + this.hashCode + ", sigma=" + this.sigma + ", theta="
        + this.theta + ", rightFormulas=" + this.rightFormulas + "]";
  }

}
