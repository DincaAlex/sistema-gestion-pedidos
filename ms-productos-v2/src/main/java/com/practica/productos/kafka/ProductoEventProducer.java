package com.practica.productos.kafka;

import com.practica.productos.event.ProductoEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Component
public class ProductoEventProducer {

    private final KafkaSender<String, ProductoEvent> kafkaSender;

    @Value("${kafka.topic.producto-events}")
    private String topic;

    public ProductoEventProducer(KafkaSender<String, ProductoEvent> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    /**
     * Publica un evento de producto a Kafka de forma reactiva
     * @param key clave del mensaje (generalmente el ID del producto)
     * @param event evento a publicar
     * @return Mono que completa cuando el evento es enviado
     */
    public Mono<Void> publishEvent(String key, ProductoEvent event) {
        log.info("Publicando evento: {} con key: {} al topic: {}",
                 event.getEventType(), key, topic);

        ProducerRecord<String, ProductoEvent> producerRecord =
            new ProducerRecord<>(topic, key, event);

        SenderRecord<String, ProductoEvent, String> senderRecord =
            SenderRecord.create(producerRecord, event.getCorrelationId());

        return kafkaSender.send(Mono.just(senderRecord))
            .doOnNext(result -> {
                log.info("Evento {} publicado exitosamente - Partition: {}, Offset: {}, CorrelationId: {}",
                         event.getEventType(),
                         result.recordMetadata().partition(),
                         result.recordMetadata().offset(),
                         result.correlationMetadata());
            })
            .doOnError(error -> {
                log.error("Error al publicar evento {} con correlationId: {}",
                         event.getEventType(), event.getCorrelationId(), error);
            })
            .then();
    }

    /**
     * Publica un evento usando el ID del producto como key
     * @param productoId ID del producto
     * @param event evento a publicar
     * @return Mono que completa cuando el evento es enviado
     */
    public Mono<Void> publishEvent(Long productoId, ProductoEvent event) {
        return publishEvent(String.valueOf(productoId), event);
    }
}
