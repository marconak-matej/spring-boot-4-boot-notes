package io.github.mm.grpc.demo;

import io.github.mm.grpc.shared.exception.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final Map<String, Demo> store = new ConcurrentHashMap<>();

    public Demo createDemo(String name) {
        validateName(name);
        var id = UUID.randomUUID().toString();
        var demo = new Demo(id, name);
        store.put(id, demo);
        return demo;
    }

    public Demo updateDemo(String id, String name) {
        validateName(name);
        var demo = store.computeIfPresent(id, (_, _) -> new Demo(id, name));
        if (demo == null) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        return demo;
    }

    public Demo getDemoById(String id) {
        return store.computeIfAbsent(id, _ -> {
            throw new NotFoundException("Demo with id " + id + " not found");
        });
    }

    public List<Demo> getAllDemos() {
        return List.copyOf(store.values()); // Return immutable copy
    }

    public void deleteDemo(String id) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        store.remove(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name must not exceed 50 characters");
        }
    }
}
