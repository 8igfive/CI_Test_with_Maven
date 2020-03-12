package factor;

import inter.Factor;
import main.Term;

import java.math.BigInteger;
import java.util.Objects;

public class TriFactor implements Factor {
    private Type type;
    private BigInteger expo = BigInteger.ONE;
    private Factor subFactor;

    public Factor getSubFactor() {
        return subFactor;
    }

    public void setSubFactor(Factor subFactor) {
        this.subFactor = subFactor;
    }

    public void setExpo(BigInteger expo) {
        this.expo = expo;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Term derive() {
        Term derivation = new Term();
        TriFactor triFactor0 = new TriFactor();
        TriFactor triFactor1 = new TriFactor();
        if (this.type == Type.SIN) {
            derivation.setCoefficient(this.expo);
            triFactor0.setType(Type.SIN);
            triFactor1.setType(Type.COS);
        } else {
            derivation.setCoefficient(this.expo.multiply(new BigInteger("-1")));
            triFactor0.setType(Type.COS);
            triFactor1.setType(Type.SIN);
        }
        if (this.expo.equals(BigInteger.ZERO)) {
            return derivation;
        }
        triFactor0.setExpo(this.expo.add(new BigInteger("-1")));
        triFactor0.setSubFactor(this.subFactor);
        triFactor1.setSubFactor(this.subFactor);            //注意这里是浅拷贝
        derivation.addFactor(triFactor0);
        derivation.addFactor(triFactor1);
        Term subDerivation = this.subFactor.derive();
        derivation.mergeTerm(subDerivation);
        return derivation;
    }

    @Override
    public BigInteger getExpo() {
        return expo;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof TriFactor) {
            TriFactor o = (TriFactor) other;
            return this.type == o.type && this.expo.equals(o.expo) &&
                    this.subFactor.equals(o.getSubFactor());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.expo, this.subFactor);
    }

    @Override
    public String toString() {
        if (this.expo.equals(BigInteger.ZERO)) {
            return "1";
        }
        String string = "";
        if (this.type == Type.SIN) {
            string += "sin(";
        } else {
            string += "cos(";
        }
        string += this.subFactor.toString();
        string += ")";
        if (!this.expo.equals(BigInteger.ONE)) {
            string += "**";
            string += this.expo.toString();
        }
        return string;
    }
}
