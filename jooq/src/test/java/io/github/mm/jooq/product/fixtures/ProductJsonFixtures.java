package io.github.mm.jooq.product.fixtures;

public final class ProductJsonFixtures {

    private ProductJsonFixtures() {}

    public static String webcam() {
        return """
                {
                    "name": "Webcam",
                    "description": "HD Webcam with microphone",
                    "price": 89.99,
                    "stockQuantity": 30,
                    "sku": "PROD-WEBCAM-001",
                    "category": "ELECTRONICS",
                    "status": "ACTIVE"
                }
                """;
    }

    public static String updateRequest() {
        return """
                {
                    "name": "Updated Product",
                    "description": "Updated description",
                    "price": 149.99,
                    "stockQuantity": 75,
                    "sku": "PROD-UPDATED-001",
                    "category": "ELECTRONICS",
                    "status": "ACTIVE"
                }
                """;
    }

    public static String deleteRequest() {
        return """
                {
                    "name": "Product to Delete",
                    "description": "This product will be deleted",
                    "price": 25.00,
                    "stockQuantity": 10,
                    "sku": "PROD-DELETE-TEST",
                    "category": "ELECTRONICS",
                    "status": "ACTIVE"
                }
                """;
    }

    public static String invalid() {
        return """
                {
                    "description": "Missing required name field",
                    "price": -10.00
                }
                """;
    }

    public static String createElectronics() {
        return """
                {
                    "name": "Gaming Keyboard",
                    "description": "Mechanical RGB Gaming Keyboard",
                    "price": 129.99,
                    "stockQuantity": 100,
                    "sku": "PROD-KEYBOARD-001",
                    "category": "ELECTRONICS",
                    "status": "ACTIVE"
                }
                """;
    }

    public static String createPeripherals() {
        return """
                {
                    "name": "USB Hub",
                    "description": "7-Port USB 3.0 Hub",
                    "price": 39.99,
                    "stockQuantity": 150,
                    "sku": "PROD-HUB-001",
                    "category": "PERIPHERALS",
                    "status": "ACTIVE"
                }
                """;
    }
}
