/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

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
import java.util.Map;

@JsonSerialize(using = com.salesforce.cf2pojo.model.SubFunction.Serializer.class)
public class SubFunction implements ValueType {

    private final String stringTemplate;
    private final Map<String, ValueType> variableMap;

    public SubFunction(final String stringTemplate) {
        this.stringTemplate = stringTemplate;
        this.variableMap = null;
    }

    public SubFunction(final String stringTemplate, final Map<String, ValueType> variableMap) {
        this.stringTemplate = stringTemplate;
        this.variableMap = variableMap;
    }

    @Override
    public String getValue() {
        return toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getStringTemplate() {
        return stringTemplate;
    }

    public Map<String, ValueType> getVariableMap() {
        return variableMap;
    }

    /**
     * A custom serializer for SubFunction.
     */
    public static class Serializer extends JsonSerializer<SubFunction> {

        @Override
        public void serialize(SubFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeFieldName("Fn::Sub");

            if (value.getVariableMap() == null) {
                gen.writeString(value.getStringTemplate());
            } else {
                gen.writeStartArray();
                gen.writeString(value.getStringTemplate());
                serializers.defaultSerializeValue(value.getVariableMap(), gen);
                gen.writeEndArray();
            }

            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(SubFunction value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
