package com.jojos.bank.money;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author karanikasg@gmail.com
 */
public class CurrencyTest {
    @Test
    public void testEuros() throws Exception {
        String euroCode = "eur";
        Currency currency = Currency.getFor(euroCode);
        Assert.assertSame(currency, Currency.EURO);
        Assert.assertEquals(2, currency.getDecimalPlaces());
    }

    @Test(expected = EnumConstantNotPresentException.class)
    public void testCurrencyNotSupported() throws Exception {
        String euroCode = "this is not a currency";
        Currency.getFor(euroCode);
    }

}
