package com.cobblemonbrasil.org.api_vincular;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetsService {

    private Sheets sheetsService;
    private static final String SPREADSHEET_ID = "1Vs4jU0eUIRPWhjmVVar8wFDekTWLimU3B4X_8vdDn1E"; // Substitua pelo ID da sua planilha

    @PostConstruct
    public void init() throws Exception {
        // Carrega as credenciais do arquivo JSON
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("credentials.json");
        if (credentialsStream == null) {
            throw new IllegalStateException("Arquivo credentials.json não encontrado em src/main/resources!");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped("https://www.googleapis.com/auth/spreadsheets");

        // Inicializa o serviço do Google Sheets
        sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Minecraft Vincular")
                .build();
    }

    public void saveToSheets(String nick, String email, String token) throws Exception {
        // Dados a serem salvos
        List<Object> row = Arrays.asList(nick, email, token);
        ValueRange body = new ValueRange().setValues(Arrays.asList(row));

        // Adiciona os dados à planilha
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, "Sheet1!A:C", body)
                .setValueInputOption("RAW")
                .execute();
    }
}