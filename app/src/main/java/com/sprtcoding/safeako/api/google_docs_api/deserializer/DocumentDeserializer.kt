package com.sprtcoding.safeako.api.google_docs_api.deserializer

import com.google.api.services.docs.v1.model.Body
import com.google.api.services.docs.v1.model.Document
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class DocumentDeserializer : JsonDeserializer<Document> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Document {
        // Manually deserialize the JSON into a Document object
        val jsonObject = json.asJsonObject
        val body = context.deserialize<Body>(jsonObject.get("body"), Body::class.java)
        // Other fields...
        return Document(/* Set fields accordingly */)
    }
}