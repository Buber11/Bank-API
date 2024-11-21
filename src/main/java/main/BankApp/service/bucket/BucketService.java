package main.BankApp.service.bucket;



public interface BucketService {
    boolean tryConsume(String token);
    void invalidateExpiredBucket();
}
