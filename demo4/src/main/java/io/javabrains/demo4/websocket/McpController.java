package io.javabrains.demo4.websocket;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/model")
    public Map<String, Object> getModel() {
        return Map.of(
                "name", "demo-mcp",
                "version", "1.0",
                "activeOrders", 3,
                "description", "Simple MCP model context for demo"
        );
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(0L);
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    OrderEvent ev = new OrderEvent(
                            "order-" + i,
                            (i % 2 == 0) ? "SHIPPED" : "CREATED",
                            Instant.now().toString(),
                            "Demo event " + i
                    );
                    emitter.send(ev, MediaType.APPLICATION_JSON);
                    Thread.sleep(1200);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}

