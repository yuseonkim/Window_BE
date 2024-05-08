package org.example.maeum2_be.service.gpt;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.dto.GPTRequestDTO;
import org.example.maeum2_be.dto.GPTResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {
    private final String OPENAI_API_URL;
    private final String OPENAI_API_KEY;

    private final RestTemplate restTemplate;

    @Autowired
    public GPTService(RestTemplate restTemplate,
                      @Value("${openai.api.url}") String apiUrl,
                      @Value("${openai.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.OPENAI_API_URL = apiUrl;
        this.OPENAI_API_KEY = apiKey;
    }

    public GPTResponseDTO getResponse(GPTRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

        HttpEntity<GPTRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<GPTResponseDTO> responseEntity = restTemplate.postForEntity(OPENAI_API_URL, requestEntity, GPTResponseDTO.class);

        return responseEntity.getBody();
    }
}