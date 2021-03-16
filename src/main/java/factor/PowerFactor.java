package factor;

import inter.Factor;
import main.Term;

import java.math.BigInteger;
import java.util.Objects;

public class PowerFactor implements Factor {
    private BigInteger expo = BigInteger.ONE;

    public void setExpo(BigInteger expo) {
        this.expo = expo;
    }

    @Override
    public Term derive() {
        Term derivation = new Term();
        derivation.setCoefficient(this.expo);
        if (this.expo.equals(BigInteger.ZERO)) {
            return derivation;
        }
        PowerFactor powerFactor = new PowerFactor();
        powerFactor.setExpo(this.expo.add(new BigInteger("-1")));
        derivation.addFactor(powerFactor);
        return derivation;
    }

    @Override
    public BigInteger getExpo() {
        return expo;
    }

    @Override
    public Type getType() {
        return Type.X;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() == PowerFactor.class) {
            PowerFactor o = (PowerFactor) other;
            return this.expo.equals(o.getExpo());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), expo, getClass());
    }

    @Override
    public String toString() {
        if (this.expo.equals(BigInteger.ZERO)) {
            return "1";
        }
        String string = "x";
        if (!this.expo.equals(BigInteger.ONE)) {
            string += "**";
            string += this.expo.toString();
        }
        return string;
    }

}
