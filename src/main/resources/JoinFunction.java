/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 * Modifications: (c) 2017 Salesforce
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
import java.util.List;

@JsonSerialize(using = com.salesforce.cf2pojo.model.JoinFunction.Serializer.class)
public class JoinFunction implements ValueType {

    private final String separator;
    private final List<ValueType> strings;
    private final boolean explicitArray;

    public JoinFunction(final String separator, final List<ValueType> strings, final boolean explicitArray) {
        this.separator = separator;
        this.strings = strings;
        this.explicitArray = explicitArray;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (ValueType s : this.strings) {
            sb.append(s.getValue()).append(this.separator);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getSeparator() {
        return separator;
    }

    public List<ValueType> getStrings() {
        return strings;
    }

    public boolean isExplicitArray() {
        return explicitArray;
    }

    /**
     * A custom serializer for JoinFunction.
     */
    public static class Serializer extends JsonSerializer<JoinFunction> {

        @Override
        public void serialize(JoinFunction value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("Fn::Join");
            gen.writeString(value.getSeparator());

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
        public void serializeWithType(JoinFunction value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
