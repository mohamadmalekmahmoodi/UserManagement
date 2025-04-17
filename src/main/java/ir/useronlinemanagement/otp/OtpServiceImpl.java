package ir.useronlinemanagement.otp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
@Service
public class OtpServiceImpl implements OtpService{

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final String OTP_KEY_PREFIX = "otp:";

    @Autowired
    public OtpServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateOtp(String email) {
        String otp = String.valueOf(secureRandom.nextInt(900000) + 100000);
        String key = OTP_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5));
        return otp;
    }

    public boolean validateOtp(String email, String inputOtp) {
        String key = OTP_KEY_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
