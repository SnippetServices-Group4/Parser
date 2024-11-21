package com.services.group4.parser.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.common.response.DataTuple;
import com.services.group4.parser.dto.result.ResponseDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResponseDtoDeserializer<T> extends JsonDeserializer<ResponseDto<T>> {

  @Override
  public ResponseDto<T> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    JsonNode rootNode = mapper.readTree(p);

    String message = rootNode.get("message").asText();
    JsonNode dataNode = rootNode.get("data");

    DataTuple<T> dataTuple;
    if (dataNode.has("name") && dataNode.has("data")) {
      String name = dataNode.get("name").asText();
      T data =
          mapper.convertValue(
              dataNode.get("data"), ctxt.getTypeFactory().constructType(Object.class));
      dataTuple = new DataTuple<>(name, data);
    } else {
      String name = dataNode.fieldNames().next();
      JsonNode valueNode = dataNode.get(name);
      T data;
      if (name.equals("snippetList")) {
        List<Long> list = new ArrayList<>();
        valueNode.forEach(node -> list.add(node.asLong()));
        data = (T) list;
      } else {
        data = mapper.convertValue(valueNode, ctxt.getTypeFactory().constructType(Object.class));
      }
      dataTuple = new DataTuple<>(name, data);
    }

    return new ResponseDto<>(message, dataTuple);
  }
}
