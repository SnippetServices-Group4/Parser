package com.services.group4.parser.dto.result;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.services.group4.parser.common.json.ResponseDtoDeserializer;
import com.services.group4.parser.common.json.ResponseDtoSerializer;
import com.services.group4.parser.common.response.DataTuple;

@JsonSerialize(using = ResponseDtoSerializer.class)
@JsonDeserialize(using = ResponseDtoDeserializer.class)
public record ResponseDto<T>(String message, DataTuple<T> data) {}
