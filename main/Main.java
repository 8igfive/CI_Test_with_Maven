package main;

import factor.ExprFactor;
import inter.Factor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String function0 = scanner.nextLine();
        Factor function = Factory.parseFunction(function0);
        if (function == null) {
            System.out.println("WRONG FORMAT!");
            return;
        }
        Factor derivation = Factory.getDerivation(function);
        if (derivation instanceof ExprFactor) {
            String result = derivation.toString();
            System.out.println(result.substring(1, result.length() - 1));
        } else {
            System.out.println(derivation);
        }
        System.out.println(function);
    }
}
