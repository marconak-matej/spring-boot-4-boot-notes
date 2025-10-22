package io.github.mm.http.client.demo.internal;

import io.github.mm.http.client.demo.rest.Demo;
import io.github.mm.http.client.infrastructure.exception.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final Map<String, Demo> store = new ConcurrentHashMap<>();

    public Demo createDemo(Demo demo) {
        var id = UUID.randomUUID().toString();
        var newDemo = new Demo(id, demo.name());
        store.put(id, newDemo);
        return newDemo;
    }

    public Demo updateDemo(String id, Demo demo) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        var updatedDemo = new Demo(id, demo.name());
        store.put(id, updatedDemo);
        return updatedDemo;
    }

    public List<Demo> getAllDemos() {
        return store.values().stream().toList();
    }

    public Demo getDemoById(String id) {
        var demo = store.get(id);
        if (demo == null) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        return demo;
    }

    public void deleteDemo(String id) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        store.remove(id);
    }
}
