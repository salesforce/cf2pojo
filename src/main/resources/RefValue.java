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

import java.io.IOException;

@JsonSerialize(using = com.salesforce.cf2pojo.model.RefValue.Serializer.class)
public class RefValue implements ValueType {

    private String resourceName;

    public RefValue(final String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getValue() {
        return this.resourceName;
    }

    @Override
    public String toString() {
        return "Ref=" + resourceName;
    }

    /**
     * A custom serializer for RefValue.
     */
    public static class Serializer extends JsonSerializer<RefValue> {

        @Override
        public void serialize(com.salesforce.cf2pojo.model.RefValue value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeFieldName("Ref");
            serializers.defaultSerializeValue(value.getValue(), gen);
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(
            com.salesforce.cf2pojo.model.RefValue value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
