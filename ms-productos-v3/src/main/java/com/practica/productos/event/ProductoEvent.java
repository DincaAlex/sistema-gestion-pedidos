package com.practica.productos.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProductoCreatedEvent.class, name = "PRODUCTO_CREATED"),
    @JsonSubTypes.Type(value = ProductoUpdatedEvent.class, name = "PRODUCTO_UPDATED"),
    @JsonSubTypes.Type(value = ProductoDeletedEvent.class, name = "PRODUCTO_DELETED"),
    @JsonSubTypes.Type(value = StockUpdatedEvent.class, name = "STOCK_UPDATED")
})
public abstract class ProductoEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String correlationId;
    private String version;
    private EventMetadata metadata;

    public ProductoEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
        this.version = "1.0";
        this.metadata = new EventMetadata();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventMetadata {
        private String userId;
        private String source = "ms-productos-v2";
    }
}
