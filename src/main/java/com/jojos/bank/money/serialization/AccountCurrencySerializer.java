package com.jojos.bank.money.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jojos.bank.money.Currency;

import java.io.IOException;

/**
 * Serializer that is used by the ObjectMapper and other chained JsonSerializers
 * to serialize the account's {@link Currency}
 *
 */
public class AccountCurrencySerializer extends JsonSerializer<Currency> {
    @Override
    public void serialize(Currency value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getIso());
    }
}