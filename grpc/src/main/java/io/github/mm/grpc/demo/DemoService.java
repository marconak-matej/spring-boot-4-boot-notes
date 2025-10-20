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
        var id = UUID.randomUUID().toString();
        var demo = new Demo(id, name);
        store.put(id, demo);
        return demo;
    }

    public Demo updateDemo(String id, String name) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        var demo = new Demo(id, name);
        store.put(id, demo);
        return demo;
    }

    public Demo getDemoById(String id) {
        var demo = store.get(id);
        if (demo == null) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        return demo;
    }

    public List<Demo> getAllDemos() {
        return store.values().stream().toList();
    }

    public void deleteDemo(String id) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        store.remove(id);
    }
}
