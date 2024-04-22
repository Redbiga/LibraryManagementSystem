package com.reda.library.controller;

import com.alibaba.fastjson.JSONObject;
import com.read.library.entitys.User;
import com.reda.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 登录控制器
 * @author redA
 */
@RestController
@RequestMapping("/admin")
public class LoginHandler {
    /**
     * 用户数据库操作接口
     */
    @Resource
    private UserRepository userRepository;

    /**
     * 管理员登录
     * @param user 用户信息
     * @return 登录信息
     */
    @PostMapping("/login")
    public JSONObject adminLogin(@RequestBody User user) {
        return login(user, "admin");
    }


    /**
     * 管理员登录
     * 0 - 账号或密码错误
     * 1 - 登录成功
     * 2 - 账号冻结无法登录
     * @param user 用户表单
     * @return 登录状态
     */
    public JSONObject login(User user, String userGroups) {
        // 封装表单
        Example<User> userExample = Example.of(user);
        // 搜索用户
        Optional<User> userOptional = userRepository.findOne(userExample);
        // 创建登录返回信息
        JSONObject loginMessage = new JSONObject();
        loginMessage.put("message", "login");
        loginMessage.put("statusCode", 0);

        // 判断是否存在该用户信息
        if (userOptional.isPresent()) {
            // 获取用户数据
            User userTemp = userOptional.get();
            // 判断用户是否为 用户组
            if (userGroups.equals(userTemp.getGroups())) {
                if (userTemp.getState() == 1) {
                    // 判断用户账号密码是否正确
                    if (!user.getUsername().equals(userTemp.getUsername()) || !user.getPassword().equals(userTemp.getPassword())) {
                        return loginMessage;
                    }
                    loginMessage.replace("statusCode", 1);
                    loginMessage.put("username", user.getUsername());
                }
                else {
                    loginMessage.replace("statusCode", 2);
                }
            }
        }

        return loginMessage;
    }
}
