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
        //refresh token done
        //rate limit done
        //check if ip is for the person in redis while register
        //add event handling to say welcome to user after register
        //reset password by redis
        //add sso
        //ifraim for sso
        //add logout
        //add api to delete token
        //spring reactive
        //added threads for handling things
        //added delete token for persons after logout at meed night from db

//        باید رفرش توکن رو:
//        در دیتابیس ذخیره کنی
//        همراه با تاریخ انقضا (مثلاً ۷ روز بعد)
//        بعد هنگام مصرفش (refresh کردن توکن)، چک کنی که هنوز منقضی نشده.
        SpringApplication.run(UserOnlineManagementApplication.class, args);
    }
}
