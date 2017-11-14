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

@JsonSerialize(using = com.salesforce.cf2pojo.model.GetAttFunction.Serializer.class)
public class GetAttFunction implements ValueType {

    private final ValueType logicalNameOfResource;
    private final ValueType attributeName;

    public GetAttFunction(final ValueType logicalNameOfResource, final ValueType attributeName) {
        this.logicalNameOfResource = logicalNameOfResource;
        this.attributeName = attributeName;
    }

    public ValueType getLogicalNameOfResource() {
        return logicalNameOfResource;
    }

    public ValueType getAttributeName() {
        return attributeName;
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
     * A custom serializer for GetAttFunction.
     */
    public static class Serializer extends JsonSerializer<GetAttFunction> {

        @Override
        public void serialize(
            com.salesforce.cf2pojo.model.GetAttFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("Fn::GetAtt");
            serializers.defaultSerializeValue(value.getLogicalNameOfResource(), gen);
            serializers.defaultSerializeValue(value.getAttributeName(), gen);
            gen.writeEndArray();
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(com.salesforce.cf2pojo.model.GetAttFunction value, JsonGenerator gen,
            SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
