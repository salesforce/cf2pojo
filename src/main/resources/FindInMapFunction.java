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

@JsonSerialize(using = com.salesforce.cf2pojo.model.FindInMapFunction.Serializer.class)
public class FindInMapFunction implements ValueType {

    private final String mapName;
    private final ValueType topLevelKey;
    private final ValueType secondLevelKey;

    public FindInMapFunction(final String mapName, final ValueType topLevelKey, final ValueType secondLevelKey) {
        this.mapName = mapName;
        this.topLevelKey = topLevelKey;
        this.secondLevelKey = secondLevelKey;
    }

    public String getMapName() {
        return mapName;
    }

    public ValueType getTopLevelKey() {
        return topLevelKey;
    }

    public ValueType getSecondLevelKey() {
        return secondLevelKey;
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
     * A custom serializer for FindInMapFunction.
     */
    public static class Serializer extends JsonSerializer<FindInMapFunction> {

        @Override
        public void serialize(FindInMapFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("Fn::FindInMap");
            serializers.defaultSerializeValue(value.getMapName(), gen);
            serializers.defaultSerializeValue(value.getTopLevelKey(), gen);
            serializers.defaultSerializeValue(value.getSecondLevelKey(), gen);
            gen.writeEndArray();
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(FindInMapFunction value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
