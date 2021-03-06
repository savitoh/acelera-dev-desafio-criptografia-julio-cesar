package desafiocriptografiajuliocesar.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import desafiocriptografiajuliocesar.http.payload.CriptografiaJulioCesarPayload;
import desafiocriptografiajuliocesar.http.urisuporte.URIApiCodeNation;
import desafiocriptografiajuliocesar.http.urisuporte.URIApiCodeNationEnviaDesafioDecifrado;
import desafiocriptografiajuliocesar.http.urisuporte.URIApiCodeNationRecebeDesafio;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class CodeNationApiClient {

    private URIApiCodeNation uriApiCodeNation;

    private final ObjectMapper objectMapper;

    public CodeNationApiClient() {
        this.objectMapper = new ObjectMapper();
    }

    public CriptografiaJulioCesarPayload recebeDesafio() throws IOException, InterruptedException {
        uriApiCodeNation = new URIApiCodeNationRecebeDesafio();
        final String uriRecebeTextoCriptografado = uriApiCodeNation.getURI();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uriRecebeTextoCriptografado))
                .build();
        HttpResponse<String> response = ClientHttpFactory.of().send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        final String resquestBody = response.body();
        return objectMapper.readValue(resquestBody, CriptografiaJulioCesarPayload.class);
    }

    public String enviarDesafio(Path path) throws IOException, InterruptedException {
        Map<String, Path> data = new LinkedHashMap();
        data.put("answer", path);
        final String boundary = new BigInteger(256, new Random()).toString();
        uriApiCodeNation = new URIApiCodeNationEnviaDesafioDecifrado();

        final String uriEnviaDesafio = uriApiCodeNation.getURI();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                .POST(BodyPublisherMultipartDataFactory.of(data, boundary))
                .uri(URI.create(uriEnviaDesafio))
                .build();
        HttpResponse<String> response = ClientHttpFactory.of().send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        return response.body();
    }
}
