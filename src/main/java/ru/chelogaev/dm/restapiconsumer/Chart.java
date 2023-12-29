package ru.chelogaev.dm.restapiconsumer;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.knowm.xchart.*;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.chelogaev.dm.restapiconsumer.dto.MeasureDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Chart {

    public static void main(String[] args) {
        double[] xData = new double[1000];
        final double[] yData = new double[1000];
        for (int i=0; i<1000; i++) {
            xData[i]=i;
        }
        String url = "http://localhost:8080/measurements";
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        try {
            ResponseEntity<MeasureDTO[]> responseEntity = restTemplate.getForEntity(url, MeasureDTO[].class);
            MeasureDTO[] measureArr = responseEntity.getBody();
            Arrays.setAll(yData, x->measureArr[x].getValue());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        XYChart chart = QuickChart.getChart("График температур y(х): у - значение температуры, x - номер измерения", "X", "Y", "y(x)", xData, yData);
        new SwingWrapper(chart).displayChart();
    }
}
