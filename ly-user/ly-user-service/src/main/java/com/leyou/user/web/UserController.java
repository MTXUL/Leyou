package com.leyou.user.web;


import com.leyou.item.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户 注册数据校验
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> userCheck(@PathVariable("data") String data,@PathVariable("type")Integer type) {
        Boolean boo = this.userService.userCheck(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(userService.userCheck(data,type));
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> smsCode(@RequestParam("phone")String phone){
        userService.smsCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> userRegister(User user, @RequestParam("code") String code){
        userService.userRegister(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("query")
    public ResponseEntity<User> queryUserNameAndPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        return ResponseEntity.ok(userService.queryUserNameAndPassword(username,password));
    }

}
