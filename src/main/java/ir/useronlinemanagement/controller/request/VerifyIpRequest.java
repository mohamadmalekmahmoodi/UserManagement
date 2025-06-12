package ir.useronlinemanagement.controller.request;


public class VerifyIpRequest {
    private String ip;
    private String otp;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

