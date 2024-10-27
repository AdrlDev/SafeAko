package com.sprtcoding.safeako.firebase.messaging

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class AccessToken {
    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken() : String? {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"safeako-5848b\",\n" +
                    "  \"private_key_id\": \"8fbb6af75a982e55e700ad0d09b37e6781ff6a0f\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCuYWhQsGx+VfBH\\nujS1rGQL0izhijYbLK2bMWWmiHOQsjs/HXmYI5FOoLEF5EyRNeJ3i3b9oqSBLsR5\\nu8lQevRetkdYdJ3x11VwppVpl2yqvjxr2WnRLgyTqYw6jS1MVl3zO/39DAlLvGq0\\nKOaLVO9QS3YX5LfqUWWrgYs4W8Q22pdBVjNrkLuffH34r3R1ZfW5Kw/R2puFjlH3\\n2h6SH5DrOQyFEZuXMOO677eJ6OlXfPJ6qFM0XGdK8ttjw2rN2vJIlSRE3UkMc0FQ\\nhv9a75Nf5k3jS8n1FdEoVplE2eX6jaNU2mYawzFc+dNx6aUG3bWMVpYo5AKOeAHw\\n44xJQmTNAgMBAAECggEAIqIF7cCcS5sV1akNvuc2F4Lulvy0MkF1WVU+Mwz2ps4/\\nJhHcjHLD8THI9rqY2l4xVDzGrSkYMu/u5Z5+JRXGYukg0liW5ek2duEe25HPaznN\\nFj5f3cOfi9QonZhcXsToc95+WzuAvSH2r0FQSGvd+7yA/83VGRyBbMaVt+PB3ILJ\\nG1QPoQhuQdvC20mC/OcLTtnL+KIA7FLMf0DX66Xjr3+Qg1F/CgDhbqpUxZQt1K7o\\nXjdmnXwDq8I0y/CPdBSEZ/WvxdohDfcjib+ILuciDj1rCBknzeEITvJlFx8PrBbo\\npVSTRfTtJG0mcqHbjFvgzkg02A7N7uN/3wuz2sFNoQKBgQC+2nSUmlP0IwmAT1cW\\n1eaNYgL57Fw1thVgQd96P9am8tPQ/VFCRIM/mAC8EEOu/R/IiRfZVbWe25nWSj5V\\nD+4moa2CdLBDx/BltNvSbIZ9VIr8qnvNEwc6ora4oF2wPF01bP9uIRg0Xl9QjxwI\\n2i2OBsDukktKsNgTq6g/yu15YwKBgQDp53zcjbw+FD9J/hFv5/5hdWx5mvO/Ds8n\\nKvBF+8nK3g4QNClorIYUfVUwWEUF8LE+dHrTJT9ieuArDmZ+SPCP7iQOyJX9tpUU\\njKkXkCylmDDPJYi2aVtoa5wFlrbsW7gf78b5V3EGQFQfIjUFeStA+ByYUhRuRhLR\\nHJD+o10YDwKBgCQsuAgqsFsIARcG8tTrOkX2G+ZPPLMZjjeuhLNhQkhmomtseGh9\\nH/t/zjPXSVzzsYCLK7AQJNq1eRU7AVGSMWu1HXQqtfZDAXflrfzWgS9kROvbTJf0\\nUVx11vhrvRZYerpTrJ6tOy9B7AMS/RdYc2YGNa+Cny7LEkm4Ma3VwulNAoGAcSng\\n2+fqyfILyKWWFBkellI0QAU6phXpHR8GDiXKNE8IaMiDvDOzztfwTip2gnA3oGrE\\nPcFIkEG4kLwv1Cs/z7K8WTascopSSBXhfRfwsdEW1DFpmi8Q3U0qt/151JJvW2wr\\nTNkEQqz+xe1Lz7EWvyEK3JrhbZSTZ/41HC03TfsCgYAPnXDbyTQBiqpHpTifs/1K\\nkCWo1l8WeZBhnm443QMgnrFsTe5OBNJ9aVjt/hNIxviqOSUQCUpaGVPXn0vWt4J5\\neEu9JONIzdAnzF3K+qPDkbPvNLct3leIvG7CmGRV4XWNGNDE3hdx0zTsAR3kNf0d\\nIugoA9cq7iy0R9jly3CXHQ==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-s4r50@safeako-5848b.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"115696940981101910477\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-s4r50%40safeako-5848b.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"

            val stream: InputStream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredentials: GoogleCredentials = GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(firebaseMessagingScope))

            googleCredentials.refresh()

            return googleCredentials.accessToken.tokenValue


        } catch (e: IOException) {
            e.message?.let { Log.d("ErrorFirebaseToken", it) }
            return null
        }
    }
}