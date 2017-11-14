package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;

@JsonSerialize(using = com.salesforce.cf2pojo.model.IfFunction.Serializer.class)
public class IfFunction implements ValueType {

    private final String conditionName;
    private final ValueType thenValue;
    private final ValueType elseValue;

    public IfFunction(final String conditionName, final ValueType thenValue, final ValueType elseValue) {
        this.conditionName = conditionName;
        this.thenValue = thenValue;
        this.elseValue = elseValue;
    }

    public String getConditionName() {
        return conditionName;
    }

    public ValueType getThenValue() {
        return thenValue;
    }

    public ValueType getElseValue() {
        return elseValue;
    }

    @Override
    public String getValue() {
        return toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * A custom serializer for IfFunction.
     */
    public static class Serializer extends JsonSerializer<IfFunction> {

        @Override
        public void serialize(IfFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("Fn::If");
            serializers.defaultSerializeValue(value.getConditionName(), gen);
            serializers.defaultSerializeValue(value.getThenValue(), gen);
            serializers.defaultSerializeValue(value.getElseValue(), gen);
            gen.writeEndArray();
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(IfFunction value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
