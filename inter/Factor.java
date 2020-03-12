package inter;

import factor.Type;

import java.math.BigInteger;

public interface Factor extends Derivable {
    BigInteger getExpo();

    Type getType();
}
