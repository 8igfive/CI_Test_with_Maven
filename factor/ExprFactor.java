package factor;

import inter.Factor;
import main.Term;

import java.math.BigInteger;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ExprFactor implements Factor {
    private Set<Term> terms = new HashSet<>();
    private BigInteger expo;

    public ExprFactor() {
        expo = BigInteger.ONE;
    }

    public void setExpo(BigInteger expo) {
        this.expo = expo;
    }

    public Set<Term> getTerms() {
        return terms;
    }

    public int getTermNumber() {
        return this.terms.size();
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    public void addTerm(Term term) {
        if (term.getCoefficient().equals(BigInteger.ONE) && term.getFactorNumber() == 1) {
            for (Factor factor : term.getFactors()) {
                if (factor instanceof ExprFactor) {
                    ExprFactor exprFactor = (ExprFactor) factor;
                    for (Term termTemp : exprFactor.getTerms()) {
                        this.addTerm(termTemp);
                    }
                }
            }
        }
        boolean flag = false;
        for (Term term1 : terms) {
            if (term1.getFactors().equals(term.getFactors())) {
                flag = true;
                term.setCoefficient(term.getCoefficient().add(term1.getCoefficient()));
                terms.remove(term1);
                if (!term.getCoefficient().equals(BigInteger.ZERO)) {
                    terms.add(term);
                }
                break;
            }
        }
        if (!flag && !term.getCoefficient().equals(BigInteger.ZERO)) {
            terms.add(term);
        }
    }

    public void mergeExprFactor(ExprFactor other) { //实质是加法
        for (Term term : other.terms) {
            this.addTerm(term);
        }
    }

    @Override
    public Term derive() {
        Term derivation = new Term();
        derivation.setCoefficient(this.expo);
        if (this.expo.equals(BigInteger.ZERO)) {
            return derivation;
        }
        ExprFactor exprFactor = new ExprFactor();
        ExprFactor subDerivation = new ExprFactor();
        exprFactor.setTerms(this.terms);
        exprFactor.setExpo(this.expo.add(new BigInteger("-1")));
        derivation.addFactor(exprFactor);
        for (Term term : this.terms) {
            ExprFactor termDerivation = term.derive();
            subDerivation.mergeExprFactor(termDerivation);
        }
        derivation.addFactor(subDerivation);
        return derivation;
    }

    @Override
    public BigInteger getExpo() {
        return expo;
    }

    @Override
    public Type getType() {
        return Type.EXPR;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ExprFactor) {
            ExprFactor o = (ExprFactor) other;
            return this.terms.equals(o.terms);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getExpo(), this.terms);
    }

    //toString注意不能有指数
    @Override
    public String toString() {
        if (this.expo.equals(BigInteger.ZERO)) {
            return "1";
        }
        List<Term> sort = new ArrayList<>();
        for (Term term : this.terms) {
            sort.add(term);
        }
        Collections.sort(sort);
        boolean flag = false;
        String string = ""; //括号最后加
        for (Term term : sort) {
            if (term.getCoefficient().equals(BigInteger.ZERO)) {
                continue;
            }
            if (flag && term.getCoefficient().compareTo(BigInteger.ZERO) == 1) {
                string += "+";
            } else {
                flag = true;
            }
            string += term.toString();
        }
        if (string.equals("")) {
            string = "0";
        }
        string = "(" + string + ")";
        String result = string;
        for (BigInteger i = BigInteger.ONE;
             i.compareTo(this.getExpo()) == -1; i = i.add(BigInteger.ONE)) {
            result = result + "*" + string;
        }
        return result;
    }
}
