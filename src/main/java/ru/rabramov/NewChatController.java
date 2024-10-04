package ru.rabramov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
public class NewChatController {

    private static final Logger logger = LoggerFactory.getLogger(TimedRestController.class);
    private final RestTemplate restTemplate;

    private static final Map<String, String> conversationMap = new HashMap();
    private static final Map<String, String> epkMap = new HashMap();

    public NewChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

//        MESSENGER_963172 --  - 1589945782878816202

        conversationMap.put("scenario1", "2038121461");
        conversationMap.put("scenario2", "963172");
        conversationMap.put("scenario3", "4719778509973");
        epkMap.put("scenario1", "1347251362964467934");
        epkMap.put("scenario2", "1589945782878816202");
        epkMap.put("scenario3", "1326883008836687694");
    }

    @PostMapping("/chat")
    public String sendMessage(@RequestBody Map params) {

        logger.info("App method invocation with param: {}", params);


        String body = template.replace("MESSAGE_ID_PARAM", "" + Math.abs(new Random().nextLong()));
        body = body.replace("MESSAGE_PARAM", Optional.ofNullable(params.get("message"))
                .map(Object::toString).orElse("").trim());
        body = body.replace("CONVERSATION_ID_PARAM", Optional.ofNullable(params.get("scenario"))
                .map(Object::toString)
                .map(conversationMap::get)
                        .orElse("1347251362964467934"));
        body = body.replace("EPK_ID_PARAM", Optional.ofNullable(params.get("scenario"))
                .map(Object::toString)
                .map(epkMap::get)
                        .orElse("2038121461"));
        logger.info("body: {}", body);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> result = restTemplate.postForEntity("http://10.53.130.45/chatx/messenger/sendMessage/EFS", request, String.class);
        logger.info("result: {}", result);

        return "OK";
    }

    String template = """
            {
              "message_id": MESSAGE_ID_PARAM,
              "conversation_id": CONVERSATION_ID_PARAM,
              "type": 38,
              "message_name": "WIDGET_FROM_USER",
              "content": {
                "widget_data": {
                  "subtype": "widget_context",
                  "widget_context": {
                    "context": "Обращение в чат",
                    "message": "MESSAGE_PARAM",
                    "segment": "messenger_test_demo"
                  }
                }
              },
              "sender": {
                "id": 86887416000005,
                "device": {
                  "client_type": "RETAIL",
                  "channel": "MP_SBOL_IOS",
                  "channel_version": "12.8",
                  "platform_name": "Opera",
                  "platform_version": "62.0.3331.119",
                  "context": {
                    "entry_point": "https://www.sberbank.ru",
                    "entry_title": "GOOGLE",
                    "latitude": "55.798282",
                    "longitude": "37.709955",
                    "location_addr": "Москва",
                    "timestamp": 1711011826818,
                    "timezone": -3.0
                  }
                },
                "epk_id": "EPK_ID_PARAM",
                "erib_auth_token": "a294bcb76f7f2eafd8e37e1fe4c00b72"
              }
            }
            """;
}