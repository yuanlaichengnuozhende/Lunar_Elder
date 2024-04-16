package com.lunar.common.core.convert.serializer;

import com.lunar.common.core.enums.BaseEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BaseEnumJsonSerializer extends JsonSerializer<BaseEnum> {

    @Override
    public void serialize(BaseEnum baseEnum, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeRawValue(String.valueOf(baseEnum.getCode()));
        gen.writeFieldName(gen.getOutputContext().getCurrentName() + "_name");
        gen.writeRawValue("\"" + baseEnum.getName() + "\"");
    }

}
