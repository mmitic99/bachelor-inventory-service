package bachelor.InventoryService.service;

public interface EncryptionService {

    byte[] encrypt(String message, String key);

    String decrypt(byte[] message, String key);
}
