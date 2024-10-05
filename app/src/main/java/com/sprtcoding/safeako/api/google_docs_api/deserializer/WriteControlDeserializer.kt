package com.sprtcoding.safeako.api.google_docs_api.deserializer

import com.google.api.services.docs.v1.model.WriteControl
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class WriteControlDeserializer: JsonDeserializer<WriteControl> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): WriteControl {
        val jsonObject = json.asJsonObject
        val requiredRevisionId = jsonObject.get("requiredRevisionId")?.asString
        // Assuming WriteControl has a setter or similar mechanism
        val writeControl = WriteControl()
        writeControl.requiredRevisionId = requiredRevisionId

        return writeControl
    }
}