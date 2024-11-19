package org.kraftenty.api;

import retrofit2.Call;
import retrofit2.http.*;

public interface ChatGPTService {
    @POST("v1/chat/completions")
    Call<ChatResponse> translate(
        @Header("Authorization") String authorization,
        @Body ChatRequest request
    );
}