package main;

import inter.Factor;
import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryTest {

    @Test
    public void parseFunction() {
        String Expr = "(1 + 2) * (3 * 4) + 5";
        Factor function = Factory.parseFunction(Expr);
        assertEquals("41", function.toString());
    }
}