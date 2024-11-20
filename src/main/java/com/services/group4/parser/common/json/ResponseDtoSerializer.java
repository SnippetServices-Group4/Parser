package com.services.group4.parser.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.services.group4.parser.dto.result.ResponseDto;
import java.io.IOException;

public class ResponseDtoSerializer<T> extends JsonSerializer<ResponseDto<T>> {

  @Override
  public void serialize(
      ResponseDto<T> responseDto, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField("message", responseDto.message());

    // Personalizar el nombre de "data" en función del tipo o algún otro criterio
    String dataFieldName = responseDto.data().name();
    gen.writeObjectFieldStart("data");
    gen.writeObjectField(dataFieldName, responseDto.data().data());
    gen.writeEndObject();

    gen.writeEndObject();
  }
}
