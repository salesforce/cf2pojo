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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ValueDeserializer extends JsonDeserializer<ValueType> {
    private static final Logger logger = Logger.getLogger(
        com.salesforce.cf2pojo.model.ValueDeserializer.class.getName());

    @Override
    public ValueType deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return this.functionValue(node);
    }

    private ValueType functionValue(final JsonNode root) throws IOException {

        Iterator<String> fieldNames = root.fieldNames();

        while (fieldNames.hasNext()) {
            String next = fieldNames.next();

            if ("Fn::Base64".equals(next)) {
                JsonNode jsonNode = root.get(next);
                ValueType value = this.functionValue(jsonNode);
                return new Base64Function(value);
            } else if ("Fn::FindInMap".equals(next)) {
                JsonNode jsonNode = root.get(next);
                Iterator<JsonNode> elements = jsonNode.elements();

                JsonNode mapNameNode = elements.next();
                String mapName = mapNameNode.asText();

                JsonNode topLevelKeyNode = elements.next();
                ValueType topLevelKey = functionValue(topLevelKeyNode);

                JsonNode secondLevelKeyNode = elements.next();
                ValueType secondLevelKey = functionValue(secondLevelKeyNode);

                return new FindInMapFunction(mapName, topLevelKey, secondLevelKey);
            } else if ("Fn::If".equals(next)) {
                JsonNode jsonNode = root.get(next);
                Iterator<JsonNode> elements = jsonNode.elements();

                JsonNode conditionNameNode = elements.next();
                String conditionName = conditionNameNode.asText();

                JsonNode thenValueNode = elements.next();
                ValueType thenValue = functionValue(thenValueNode);

                JsonNode elseValueNode = elements.next();
                ValueType elseValue = functionValue(elseValueNode);

                return new com.salesforce.cf2pojo.model.IfFunction(conditionName, thenValue, elseValue);
            } else if ("Fn::GetAtt".equals(next)) {
                JsonNode jsonNode = root.get(next);
                Iterator<JsonNode> elements = jsonNode.elements();

                JsonNode logicalNameNode = elements.next();
                ValueType logicalNameOfResource = functionValue(logicalNameNode);

                JsonNode attributeNameNode = elements.next();
                ValueType attributeName = functionValue(attributeNameNode);

                return new GetAttFunction(logicalNameOfResource, attributeName);
            } else if ("Fn::Join".equals(next)) {
                JsonNode joinNode = root.get(next);
                Iterator<JsonNode> elements = joinNode.elements();

                JsonNode separatorNode = elements.next();
                String separator = separatorNode.textValue();

                JsonNode toJoinNodes = elements.next();
                List<ValueType> toJoinList = new ArrayList<>();

                if (toJoinNodes.isArray()) {
                    Iterator<JsonNode> iterator = toJoinNodes.iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        toJoinList.add(functionValue(node));
                    }
                } else {
                    toJoinList.add(functionValue(toJoinNodes));
                }

                return new JoinFunction(separator, toJoinList, toJoinNodes.isArray());
            } else if ("Fn::Sub".equals(next)) {
                JsonNode subNode = root.get(next);
                if (subNode.isArray()) {
                    Iterator<JsonNode> childNodeIterator = subNode.iterator();
                    JsonNode stringTemplateNode = childNodeIterator.next();
                    String stringTemplate = stringTemplateNode.textValue();
                    JsonNode variableMapNode = childNodeIterator.next();
                    Iterator<Map.Entry<String, JsonNode>> variableMapNodeIterator = variableMapNode.fields();
                    Map<String, ValueType> variableMap = new HashMap<>();
                    while (variableMapNodeIterator.hasNext()) {
                        Map.Entry<String, JsonNode> entry = variableMapNodeIterator.next();
                        variableMap.put(entry.getKey(), functionValue(entry.getValue()));
                    }
                    return new SubFunction(stringTemplate, variableMap);
                } else if (subNode.isTextual()) {
                    return new SubFunction(subNode.textValue());
                } else {
                    logger.warning("Unexpected value for Fn::Sub - must be array or string");
                }

            } else if ("Fn::Select".equals(next)) {
                JsonNode joinNode = root.get(next);
                Iterator<JsonNode> elements = joinNode.elements();

                JsonNode indexNode = elements.next();
                String index = indexNode.textValue();

                JsonNode arrayNode = elements.next();
                List<ValueType> arrayValuesList = new ArrayList<>();

                if (arrayNode.isArray()) {
                    Iterator<JsonNode> iterator = arrayNode.iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        arrayValuesList.add(functionValue(node));
                    }
                } else {
                    arrayValuesList.add(functionValue(arrayNode));
                }

                return new com.salesforce.cf2pojo.model.SelectFunction(index, arrayValuesList, arrayNode.isArray());
            } else if ("Ref".equals(next)) {
                return new com.salesforce.cf2pojo.model.RefValue(root.get(next).asText());
            } else {
                logger.warning("Value not supported: " + next + " - node: " + root.toString());
                return new StringValue(root.toString());
            }
        }
        return new StringValue(root.textValue());
    }
}
