package com.jojos.bank.money;

/**
 * Custom currency implementation holding the ISO code and the decimal places
 *
 * TODO support more currencies
 *
 * @author karanikasg@gmail.com
 */
public enum Currency {
    EURO("EUR", 2);

    private final String iso;
    private final int decimalPlaces;

    Currency(String iso, int decimalPlaces) {
        this.iso = iso;
        this.decimalPlaces = decimalPlaces;
    }

    public String getIso() {
        return iso;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public static Currency getFor(String code) {
        for (Currency currency : values()) {
            if (currency.iso.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new EnumConstantNotPresentException(Currency.class, String.format("Invalid constant defined %s", code));
    }

}
