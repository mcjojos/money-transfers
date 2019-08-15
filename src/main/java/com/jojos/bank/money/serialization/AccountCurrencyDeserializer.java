package com.jojos.bank.money.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.jojos.bank.money.Currency;

import java.io.IOException;

/**
 * Serializer that is used by the ObjectMapper and other chained JsonDeserializers
 * to deserialize the account's {@link Currency}
 *
 */
public class AccountCurrencyDeserializer extends JsonDeserializer<Currency> {
    @Override
    public Currency deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return Currency.getFor(node.asText());
    }
}