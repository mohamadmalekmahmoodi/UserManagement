package ir.useronlinemanagement.otp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String OTP_THROTTLE_PREFIX = "otp:verified:";


    @Autowired
    public OtpServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateOtp(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("ایمیل نمی‌تواند خالی باشد");
        }
        email = email.toLowerCase().trim();

        String key = OTP_KEY_PREFIX + email;
        String throttleKey = OTP_THROTTLE_PREFIX + email;

        //this is like a flag that protect form requests of bad user
        if (Boolean.TRUE.equals(redisTemplate.hasKey(throttleKey))) {
            throw new RuntimeException("لطفاً کمی صبر کرده و سپس دوباره تلاش کنید");
        }

        String otp = String.valueOf(secureRandom.nextInt(900000) + 100000);
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(2));
        redisTemplate.opsForValue().set(throttleKey, "1", Duration.ofSeconds(30));
        return otp;
    }

    public boolean validateOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null || email.isEmpty() || inputOtp.isEmpty()) {
            throw new IllegalArgumentException("ایمیل یا کد وارد نشده است");
        }
        String key = OTP_KEY_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new RuntimeException("کد منقضی شده یا پیدا نشد");
        }

        if (!storedOtp.equals(inputOtp)) {
            throw new RuntimeException("کد وارد شده اشتباه است");
        }

        redisTemplate.delete(key);
        return true;
    }

}
