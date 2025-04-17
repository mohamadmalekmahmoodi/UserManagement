package ir.useronlinemanagement.otp;

public interface OtpService {
    String generateOtp(String email);
    boolean validateOtp(String email, String inputOtp);
}
