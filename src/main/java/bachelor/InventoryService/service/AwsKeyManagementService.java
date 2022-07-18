package bachelor.InventoryService.service;

import bachelor.InventoryService.dto.DataKeyDto;

public interface AwsKeyManagementService {
    String GetKeyByAlias(String alias);
    byte[] EncryptText(String textToEncrypt, String keyId);
    String DecryptText(byte[] encryptedText);

    String DecryptKey(byte[] encryptedText);

    DataKeyDto GenerateDataKey();
}
