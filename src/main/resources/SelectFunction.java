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
import java.util.List;

@JsonSerialize(using = com.salesforce.cf2pojo.model.SelectFunction.Serializer.class)
public class SelectFunction implements ValueType {

    private String index;
    private final List<ValueType> strings;
    private final boolean explicitArray;

    public SelectFunction(final String index, final List<ValueType> strings, boolean explicitArray) {
        this.index = index;
        this.strings = strings;
        this.explicitArray = explicitArray;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (ValueType s : this.strings) {
            sb.append(s.getValue()).append(this.index);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List<ValueType> getStrings() {
        return strings;
    }

    public boolean isExplicitArray() {
        return explicitArray;
    }

    public static class Serializer extends JsonSerializer<SelectFunction> {
        @Override
        public void serialize(SelectFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("Fn::Select");
            gen.writeString(value.getIndex());

            if (value.isExplicitArray()) {
                gen.writeStartArray();
            }

            for (ValueType valueType : value.getStrings()) {
                serializers.defaultSerializeValue(valueType, gen);
            }

            if (value.isExplicitArray()) {
                gen.writeEndArray();
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(SelectFunction value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
