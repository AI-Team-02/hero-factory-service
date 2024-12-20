package com.herofactory.kafka.inspecteditem;


import static com.herofactory.common.Topic.INSPECTED_TOPIC;

import com.herofactory.kafka.CustomObjectMapper;
import com.herofactory.inspecteditem.dto.InspectedItemDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InspectedItemMessageProduceService  {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CustomObjectMapper objectMapper = new CustomObjectMapper();

    public void sendMessage(InspectedItemDto inspectedItemDto) {
        InspectedItemMessage message = new InspectedItemMessage(
                inspectedItemDto.getItemDto().getId(),
                new InspectedItemMessage.Payload(
                        inspectedItemDto.getItemDto(),
                        inspectedItemDto.getAutoGeneratedTags(),
                        inspectedItemDto.getInspectedAt()
                )
        );

        try {
            kafkaTemplate.send(INSPECTED_TOPIC, message.getId().toString(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
