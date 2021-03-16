package factor;

import main.Term;

import java.math.BigInteger;
import java.util.Objects;

public class ConstantFactor extends PowerFactor {
    private BigInteger coefficient;

    public ConstantFactor(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    @Override
    public BigInteger getExpo() {
        return BigInteger.ZERO;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ConstantFactor) {
            ConstantFactor o = (ConstantFactor) other;
            return this.coefficient.equals(o.coefficient);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getExpo(), this.coefficient);
    }

    @Override
    public String toString() {
        return this.coefficient.toString();
    }

    @Override
    public Term derive() {
        Term derivation = new Term();
        derivation.setCoefficient(BigInteger.ZERO);
        return derivation;
    }
}
