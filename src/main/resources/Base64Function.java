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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.nio.charset.Charset;

@JsonSerialize(using = com.salesforce.cf2pojo.model.Base64Function.Serializer.class)
public class Base64Function implements ValueType {

    private final ValueType toEncode;

    public Base64Function(final ValueType toEncode) {
        this.toEncode = toEncode;
    }

    @Override
    public String getValue() {
        return this.toEncode.getValue();
    }

    public String getEncodedValue() {
        return StringUtils.newStringUtf8(Base64.encodeBase64(toEncode.getValue().getBytes(Charset.forName("UTF-8"))));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public ValueType getToEncode() {
        return toEncode;
    }

    public static class Serializer extends JsonSerializer<Base64Function> {

        @Override
        public void serialize(Base64Function value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            gen.writeStartObject();
            gen.writeFieldName("Fn::Base64");
            serializers.defaultSerializeValue(value.getToEncode(), gen);
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(Base64Function value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefixForScalar(value, gen);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffixForScalar(value, gen);
        }
    }
}
