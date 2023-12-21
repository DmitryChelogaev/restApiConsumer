package ru.chelogaev.dm.restapiconsumer;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.chelogaev.dm.restapiconsumer.dto.MeasureDTO;
import ru.chelogaev.dm.restapiconsumer.dto.SensorDTO;

import java.net.ConnectException;
import java.time.Duration;
import java.util.*;

public class Consumer {

    /**
     * get float range [-100..100]
     */
    private static float getRandomFloat() {
        Random r = new Random();
        return -100 + r.nextFloat() * 200;
    }

    private static boolean getRandomBoolean() {
        Random r = new Random();
        return r.nextBoolean();
    }

    private static void addSensor(RestTemplate restTemplate, String sensorName) {
        System.out.println("----------------------------------------------addSensor");
        String url = "http://localhost:8080/sensors";
        Map<String, Object> jsonToSend = new HashMap<>();
        jsonToSend.put("name", sensorName);
        MakePostRequest(restTemplate, url, jsonToSend);
    }

    private static void MakePostRequest(RestTemplate restTemplate, String url, Map<String, Object> body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        try {
            String response = restTemplate.postForObject(url, request, String.class);
            System.out.println("Объект создан: " + response);
        } catch (HttpClientErrorException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void add1000Measurments(RestTemplate restTemplate, String sensorName) {
        System.out.println("----------------------------------------------add1000Measurments");
        String url = "http://localhost:8080/measurements";
        try {
            for (int i = 0; i < 1000; i++) {
                Map<String, Object> jsonToSend = new HashMap<>();
                jsonToSend.put("value", String.valueOf(getRandomFloat()));
                jsonToSend.put("raining", String.valueOf(getRandomBoolean()));
                Map<String, Object> sensorMap = new HashMap<>();
                sensorMap.put("name", sensorName);
                jsonToSend.put("sensor", sensorMap);
                MakePostRequest(restTemplate, url, jsonToSend);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void getMeasurments(RestTemplate restTemplate) {
        System.out.println("----------------------------------------------getMeasurments");
        String url = "http://localhost:8080/measurements";
        try {
            ResponseEntity<MeasureDTO[]> responseEntity = restTemplate.getForEntity(url, MeasureDTO[].class);
            MeasureDTO[] measureArr = responseEntity.getBody();
            Arrays.stream(measureArr).forEach(x -> System.out.println("Аппарат: " + x.getSensor().getName() + ", значение: " + x.getValue()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String sensorName = "Прибор на Белорусской";
        addSensor(restTemplate, sensorName);
        add1000Measurments(restTemplate, sensorName);
        getMeasurments(restTemplate);
    }
}
