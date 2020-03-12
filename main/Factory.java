package main;

import factor.ExprFactor;
import factor.ConstantFactor;
import factor.TriFactor;
import factor.PowerFactor;
import factor.Type;
import inter.Factor;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Factory {
    private static String plusSub = "[+\\-]";
    private static String blank = "[ \\t]*";
    private static String integerForConstant =
            "0*(?<integerForConstant>\\d+)";
    private static String signedIntegerForConstant =
            "(?<signForConstant>" + plusSub + ")?" + integerForConstant;
    private static String integerForExpo =
            "0*(?<integerForExpo>\\d+)";
    private static String signedIntegerForExpo =
            "(?<signForExpo>" + plusSub + ")?" + integerForExpo;
    private static String Exponent =
            "\\*\\*" + blank + signedIntegerForExpo;
    private static String powerFunction = "x";
    private static String triFunction =
            "((?<sin>sin)|(?<cos>cos))" + blank + "\\(" + blank
                    + "(?<subFactor>[^()]+?)" + blank + "\\)";
    private static String variable =
            "(?<variable>((?<power>" + powerFunction +
                    ")|(?<tri>" + triFunction + "))(?<exponent>" +
                    blank + Exponent + ")?)";
    private static String constant =
            "(?<constant>" + signedIntegerForConstant + ")";
    private static String exprFactor = "\\((?<exprFactor>[^()]+?)\\)";
    private static String factor =
            "(?:" + variable + "|" + constant + "|" + exprFactor + ")";
    private static String term =
            "(?<term>((?<signForTerm>" + plusSub + blank +
                    ")?|(?<subTerm>.*?)(?<continue>" +
                    blank + "\\*" + blank + "))" + factor + ")";
    /*private static String term0 = "(?<term>((?<subTerm>.*?)(?<continue>" + blank +
            "\\*" + blank + ")|(?<signForTerm>" + plusSub + blank + ")?)" + factor + ")";*/
    private static String factorTem =
            "(?:(?:(?:x|(?:sin|cos)[ \\t]*\\([ \\t]*[^()]+?[ \\t]*\\))" +
                    "(?:[ \\t]*\\*\\*[ \\t]*[+\\-]?\\d+)?|[+\\-]?\\d+)|\\([^()]+?\\))";
    private static String termTem =
            "(?<term>([+\\-][ \\t]*)?" + factorTem +
                    "([ \\t]*\\*[ \\t]*" + factorTem + ")*)";
    private static String expression0 = blank + "(?<operator>[+\\-][ \\t]*)?" + termTem + blank;
    private static String expression1 = "(?<operator>[+\\-][ \\t]*)" + termTem + blank;

    public static Factor parseFunction(String expression) {       //匹配失败放回null
        if (expression.indexOf('{') != -1 || expression.indexOf('}') != -1) {
            //System.out.println("0");        //TODO
            return null;
        }
        String exprModified = expression.replaceAll("\\(", "{");
        exprModified = exprModified.replaceAll("\\)", "}");
        return parseExprFactor(exprModified);
    }

    private static Term parseTerm(String term0) {
        Pattern pattern = Pattern.compile(Factory.term);
        Matcher matcher = pattern.matcher(term0);
        BigInteger coefficient = BigInteger.ONE;
        Term term = new Term();
        while (matcher.matches()) {
            String sign = matcher.group("signForTerm");
            String subTerm;
            subTerm = matcher.group("subTerm");
            if (sign != null && sign.indexOf('-') != -1) {
                coefficient = coefficient.multiply(new BigInteger("-1"));
            }
            Factor factor;
            factor = parseFactor(matcher);
            if (factor == null) {
                //System.out.println("1");        //TODO
                return null;
            }
            if (factor.getClass() == ConstantFactor.class) {
                ConstantFactor constantFactor = (ConstantFactor) factor;
                coefficient = coefficient.multiply(constantFactor.getCoefficient());
            } else {
                term.addFactor(factor);
            }
            if (subTerm != null) {
                matcher = pattern.matcher(subTerm);
            } else if (matcher.group("continue") != null) {
                //System.out.println("2");        //TODO
                return null;
            } else {
                break;
            }
        }
        if (!matcher.matches()) {
            //System.out.println("3");            //TODO
            return null;
        }
        term.setCoefficient(coefficient);
        return term;
    }

    private static Factor parseFactor(Matcher matcher) {
        Factor factor;
        String factor0 = matcher.group("variable");
        if (factor0 != null) {
            BigInteger expo = BigInteger.ONE;
            String exponent = matcher.group("exponent");
            if (exponent != null) {
                String signForExpo = matcher.group("signForExpo");
                String intForExpo = matcher.group("integerForExpo");
                if (signForExpo != null && signForExpo.indexOf('-') != -1) {
                    intForExpo = '-' + intForExpo;
                }
                expo = new BigInteger(intForExpo);
                if (expo.abs().compareTo(new BigInteger("50")) == 1) {
                    //System.out.println("4");            //TODO
                    return null;
                }
            }
            factor0 = matcher.group("power");
            if (factor0 != null) {
                PowerFactor powerFactor = new PowerFactor();
                powerFactor.setExpo(expo);
                factor = powerFactor;
            } else {
                TriFactor triFactor = parseTriFactor(matcher);
                if (triFactor == null) {
                    //System.out.println("5");            //TODO
                    return null;
                }
                triFactor.setExpo(expo);
                factor = triFactor;
            }
        } else {
            factor0 = matcher.group("constant");
            if (factor0 != null) {
                String signForConstant = matcher.group("signForConstant");
                String intForConstant = matcher.group("integerForConstant");
                if (signForConstant != null && signForConstant.indexOf('-') != -1) {
                    intForConstant = "-" + intForConstant;
                }
                factor = new ConstantFactor(new BigInteger(intForConstant));
            } else {
                factor0 = matcher.group("exprFactor");
                if (factor0 != null) {
                    Factor exprFactor = parseExprFactor(factor0);
                    if (exprFactor == null) {
                        //System.out.println("6");            //TODO
                        return null;
                    }
                    factor = exprFactor;
                } else {
                    //System.out.println("7");            //TODO
                    return null;
                }
            }
        }
        return factor;
    }

    private static TriFactor parseTriFactor(Matcher matcher) {
        TriFactor triFactor = new TriFactor();
        String type = matcher.group("sin");
        if (type != null) {
            triFactor.setType(Type.SIN);
        } else {
            triFactor.setType(Type.COS);
        }
        String subFactor = matcher.group("subFactor");
        char[] charExpr = subFactor.toCharArray();
        Unlock(charExpr);
        Pattern pattern = Pattern.compile(factor);
        Matcher matcher1 = pattern.matcher(new String(charExpr));
        Factor factor;
        if (matcher1.matches()) {
            factor = parseFactor(matcher1);
        } else {
            //System.out.println(new String(charExpr));
            //System.out.println("8");            //TODO
            return null;
        }
        if (factor == null) {
            //System.out.println("9");            //TODO
            return null;
        }
        triFactor.setSubFactor(factor);
        return triFactor;
    }

    private static Factor parseExprFactor(String expression) {
        char[] charExpr = expression.toCharArray();
        Unlock(charExpr);
        ExprFactor exprFactor = new ExprFactor();
        Pattern pattern0 = Pattern.compile(expression0);
        Pattern pattern1 = Pattern.compile(expression1);
        String expr = new String(charExpr);
        Matcher matcher = pattern0.matcher(expr);
        boolean flag = false;
        while (matcher.find()) {
            if (matcher.start() != 0) {
                //System.out.println("10");            //TODO
                return null;
            }
            int sign = 1;
            String operator = matcher.group("operator");
            if (operator != null && operator.indexOf('-') != -1) {
                sign *= -1;
            }
            String term0 = matcher.group("term");
            Term term = parseTerm(term0);
            if (term == null) {
                //System.out.println("11");            //TODO
                return null;
            }
            BigInteger coefficient = term.getCoefficient();
            coefficient = coefficient.multiply(
                    new BigInteger(String.valueOf(sign)));
            term.setCoefficient(coefficient);
            exprFactor.addTerm(term);
            if (matcher.end() == expr.length()) {
                flag = true;
                break;
            }
            expr = expr.substring(matcher.end());
            matcher = pattern1.matcher(expr);
        }
        if (!flag) {
            //System.out.println("12");            //TODO
            return null;
        }
        if (exprFactor.getTermNumber() == 1) {
            for (Term term : exprFactor.getTerms()) {
                if (term.getFactorNumber() == 1 &&
                        term.getCoefficient().equals(BigInteger.ONE)) {
                    for (Factor factor : term.getFactors()) {
                        return factor;
                    }
                }
                if (term.getFactorNumber() == 0) {
                    return new ConstantFactor(term.getCoefficient());
                }
            }
        }
        return exprFactor;
    }

    private static void Unlock(char[] charExpr) {
        int level = 0;
        for (int i = 0; i < charExpr.length; i++) {
            if (charExpr[i] == '{') {
                if (level == 0) {
                    charExpr[i] = '(';
                }
                level++;
            } else if (charExpr[i] == '}') {
                level--;
                if (level == 0) {
                    charExpr[i] = ')';
                }
            }
        }
    }

    public static Factor getDerivation(Factor function) {
        Term derivation0 = function.derive();
        Factor result = new ConstantFactor(BigInteger.ZERO);
        if (derivation0.getFactorNumber() == 0) {
            result = new ConstantFactor(derivation0.getCoefficient());
        } else if (derivation0.getCoefficient().equals(BigInteger.ONE) &&
                derivation0.getFactorNumber() == 1) {
            for (Factor factor : derivation0.getFactors()) {
                result = factor;
            }
        } else {
            ExprFactor derivation1 = new ExprFactor();
            derivation1.addTerm(derivation0);
            result = derivation1;
        }
        return result;
    }
}
