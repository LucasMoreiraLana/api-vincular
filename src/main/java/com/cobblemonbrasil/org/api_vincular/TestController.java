package com.cobblemonbrasil.org.api_vincular.controller;

import com.cobblemonbrasil.org.api_vincular.GoogleSheetsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    @Autowired
    private GoogleSheetsService sheetsService;

    private static final String CLIENT_ID = "SEU_CLIENT_ID"; // Substitua
    private static final String CLIENT_SECRET = "SEU_CLIENT_SECRET"; // Substitua
    private static final String REDIRECT_URI = "http://localhost:8080/vincular/callback";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/vincular/callback")
    public ResponseEntity<String> vincularCallback(@RequestParam("code") String code, @RequestParam("state") String nick) {
        try {
            // Passo 2: Troca o code por um token
            String token = exchangeCodeForToken(code);

            // Passo 3: Usa o token para pegar os dados do Discord
            JsonNode userData = getDiscordUserData(token);
            String email = userData.get("email").asText();
            String discordUsername = userData.get("username").asText();

            // Passo 4: Salva na planilha
            sheetsService.saveToSheets(nick, email, token);

            return ResponseEntity.ok("Conta vinculada com sucesso! Nick: " + nick + ", Email: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar o vínculo: " + e.getMessage());
        }
    }

    private String exchangeCodeForToken(String code) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build();

        Request request = new Request.Builder()
                .url("https://discord.com/api/oauth2/token")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Erro na troca do token: " + response);
            JsonNode json = mapper.readTree(response.body().string());
            return json.get("access_token").asText();
        }
    }

    private JsonNode getDiscordUserData(String token) throws IOException {
        Request request = new Request.Builder()
                .url("https://discord.com/api/users/@me")
                .header("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Erro ao pegar dados do Discord: " + response);
            return mapper.readTree(response.body().string());
        }
    }
}