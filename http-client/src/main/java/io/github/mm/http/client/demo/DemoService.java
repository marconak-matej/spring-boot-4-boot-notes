package io.github.mm.http.client.demo;

import io.github.mm.http.client.shared.exception.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final Map<Long, Demo> store = new ConcurrentHashMap<>();
    private final AtomicLong generator = new AtomicLong(1);

    public Demo createDemo(Demo demo) {
        var id = generator.getAndIncrement();
        var newDemo = new Demo(id, demo.name());
        store.put(id, newDemo);
        return newDemo;
    }

    public Demo updateDemo(Long id, Demo demo) {
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

    public Demo getDemoById(Long id) {
        var demo = store.get(id);
        if (demo == null) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        return demo;
    }

    public void deleteDemo(Long id) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Demo with id " + id + " not found");
        }
        store.remove(id);
    }
}
