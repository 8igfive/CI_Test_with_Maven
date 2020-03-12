package main;

import factor.ExprFactor;
import factor.PowerFactor;
import factor.TriFactor;
import inter.Factor;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Term implements Comparable<Term> {
    private Set<Factor> factors = new HashSet<>();
    private BigInteger coefficient = BigInteger.ONE;

    public void addFactor(Factor factor) {
        boolean flag = false;
        for (Factor factor1 : factors) {
            if (factor1.getType() == factor.getType()) {
                switch (factor.getType()) {
                    case X:
                        flag = true;
                        PowerFactor pow0 = (PowerFactor) factor;
                        PowerFactor pow1 = (PowerFactor) factor1;
                        pow0.setExpo(pow1.getExpo().add(pow0.getExpo()));
                        factors.remove(pow1);
                        if (!pow0.getExpo().equals(BigInteger.ZERO)) {
                            factors.add(pow0);
                        }
                        break;
                    case SIN:
                    case COS:
                        TriFactor tri0 = (TriFactor) factor;
                        TriFactor tri1 = (TriFactor) factor1;
                        if (tri0.getSubFactor().equals(tri1.getSubFactor())) {
                            flag = true;
                            tri0.setExpo(tri0.getExpo().add(tri1.getExpo()));
                            factors.remove(tri1);
                            if (!tri0.getExpo().equals(BigInteger.ZERO)) {
                                factors.add(tri0);
                            }
                        }
                        break;
                    case EXPR:
                        ExprFactor expr0 = (ExprFactor) factor;
                        ExprFactor expr1 = (ExprFactor) factor1;
                        if (expr0.getTerms().equals(expr1.getTerms())) {
                            flag = true;
                            expr0.setExpo(expr0.getExpo().add(expr1.getExpo()));
                            factors.remove(expr1);
                            if (!expr0.getExpo().equals(BigInteger.ZERO)) {
                                factors.add(expr0);
                            }
                        }
                        break;
                    default:
                }
            }
            if (flag) {
                break;
            }
        }
        if (!flag && !factor.getExpo().equals(BigInteger.ZERO)) {
            addFactorAssist(factor);
        }
    }

    private void addFactorAssist(Factor factor) {
        if (factor instanceof ExprFactor && factor.getExpo().equals(
                BigInteger.ONE) && ((ExprFactor) factor).getTermNumber() == 1) {
            for (Term term : ((ExprFactor) factor).getTerms()) {
                this.mergeTerm(term);
            }
        } else if (factor instanceof ExprFactor &&
                ((ExprFactor) factor).getTermNumber() == 0) {
            this.setCoefficient(BigInteger.ZERO);
        } else {
            factors.add(factor);
        }
    }

    public int getFactorNumber() {
        return factors.size();
    }

    public Set<Factor> getFactors() {
        return this.factors;
    }

    public BigInteger getCoefficient() {
        return this.coefficient;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public void mergeTerm(Term other) {
        this.setCoefficient(this.getCoefficient().multiply(other.getCoefficient()));
        for (Factor factor : other.factors) {
            this.addFactor(factor);
        }
    }

    public ExprFactor derive() {
        //TODO
        ExprFactor derivation = new ExprFactor();
        for (Factor factor : this.factors) {
            Term term = new Term();
            term.setCoefficient(this.getCoefficient());
            for (Factor factor1 : this.factors) {
                if (factor1 == factor) {
                    term.mergeTerm(factor1.derive());
                } else {
                    term.addFactor(factor1);
                }
            }
            derivation.addTerm(term);
        }
        return derivation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Term) {
            Term o = (Term) other;
            return this.coefficient.equals(o.coefficient) &&
                    this.factors.equals(o.factors);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.factors, this.coefficient);
    }

    @Override
    public int compareTo(Term other) {
        int result = this.coefficient.compareTo(other.getCoefficient());
        switch (result) {
            case 1:
                return -1;
            case -1:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        if (this.getCoefficient().equals(BigInteger.ZERO)) {
            return "0";
        }
        if (this.getFactorNumber() == 0) {
            return this.coefficient.toString();
        }
        String string = "";
        for (Factor factor : this.factors) {
            String temp = factor.toString();
            if (temp.equals("1")) {
                continue;
            }
            if (!string.equals("")) {
                string += "*";
            }
            string += temp;
        }
        if (string.equals("")) {
            return this.coefficient.toString();
        }
        if (this.getCoefficient().equals(new BigInteger("-1"))) {
            string = "-" + string;
        } else if (!this.getCoefficient().equals(BigInteger.ONE)) {
            string = this.coefficient.toString() + "*" + string;
        }
        return string;
    }
}
