package com.practica.productoswriter.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practica.productoswriter.event.*;
import com.practica.productoswriter.service.ProductoWriteService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductoEventConsumer {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final ProductoWriteService writeService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void startConsuming() {
        log.info("Iniciando consumidor de eventos de productos...");

        kafkaReceiver.receive()
                .flatMap(this::processRecord)
                .doOnError(error -> log.error("Error en el flujo del consumidor", error))
                .retry() // Reintentar en caso de error
                .subscribe();

        log.info("Consumidor de eventos de productos iniciado exitosamente");
    }

    private Mono<Void> processRecord(ReceiverRecord<String, String> record) {
        log.debug("Mensaje recibido - Partition: {}, Offset: {}, Key: {}",
                  record.partition(), record.offset(), record.key());

        return Mono.fromCallable(() -> parseJsonToMap(record.value()))
                .flatMap(map -> {
                    String eventType = extractEventType(map);
                    return processEvent(eventType, map);
                })
                .doOnSuccess(v -> {
                    record.receiverOffset().acknowledge();
                    log.debug("Offset confirmado - Partition: {}, Offset: {}",
                             record.partition(), record.offset());
                })
                .onErrorResume(error -> {
                    log.error("Error procesando mensaje - Partition: {}, Offset: {}, Key: {}",
                             record.partition(), record.offset(), record.key(), error);
                    // Aquí podrías enviar a una DLQ (Dead Letter Queue)
                    // Por ahora, confirmamos el offset para no bloquear el consumo
                    record.receiverOffset().acknowledge();
                    return Mono.empty();
                });
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonToMap(String json) throws Exception {
        return objectMapper.readValue(json, Map.class);
    }

    private String extractEventType(Map<String, Object> map) {
        String eventType = (String) map.get("eventType");
        if (eventType == null) {
            throw new IllegalArgumentException("Campo eventType no encontrado en el mensaje");
        }
        return eventType;
    }

    private Mono<Void> processEvent(String eventType, Map<String, Object> value) {
        log.info("Procesando evento de tipo: {}", eventType);

        return switch (eventType) {
            case "PRODUCTO_CREATED" -> {
                ProductoCreatedEvent event = objectMapper.convertValue(value, ProductoCreatedEvent.class);
                yield writeService.procesarCreacion(event).then();
            }
            case "PRODUCTO_UPDATED" -> {
                ProductoUpdatedEvent event = objectMapper.convertValue(value, ProductoUpdatedEvent.class);
                yield writeService.procesarActualizacion(event).then();
            }
            case "STOCK_UPDATED" -> {
                StockUpdatedEvent event = objectMapper.convertValue(value, StockUpdatedEvent.class);
                yield writeService.procesarActualizacionStock(event);
            }
            case "PRODUCTO_DELETED" -> {
                ProductoDeletedEvent event = objectMapper.convertValue(value, ProductoDeletedEvent.class);
                yield writeService.procesarEliminacion(event);
            }
            default -> {
                log.warn("Tipo de evento desconocido: {}", eventType);
                yield Mono.empty();
            }
        };
    }
}
