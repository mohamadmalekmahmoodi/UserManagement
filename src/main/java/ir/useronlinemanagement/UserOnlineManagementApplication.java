package ir.useronlinemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserOnlineManagementApplication {

    public static void main(String[] args) {
        //needed
        //accessToken done
        //refresh token
        //reset password by redis
        //add sso
        //ifraim for sso
        //add logout
        //add api to delete token
        SpringApplication.run(UserOnlineManagementApplication.class, args);
    }
}
