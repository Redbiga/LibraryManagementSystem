package com.read.library.controller;

import com.alibaba.fastjson.JSONObject;
import com.read.library.entitys.User;
import com.reda.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 用户控制器
 * @author redA
 */
@RestController
@RequestMapping("/user")
public class UserHandler {
    /**
     * 用户数据库操作接口
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户数据
     * @param page 页码
     * @param size 大小
     * @return 用户数据
     */
    @GetMapping("/{page}/{size}")
    public Page<User> getPageUser(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        return userRepository.findAll(pageable);
    }

    /**
     * 用户信息搜索
     * @param text 搜索信息
     * @return 用户信息
     */
    @GetMapping("/search/{type}/{text}/{page}/{size}")
    public Page<User> findUser(@PathVariable("type") String type,
                               @PathVariable("text") String text,
                               @PathVariable("page") Integer page,
                               @PathVariable("size") Integer size) {
        Page<User> userPage;
        PageRequest pageable = PageRequest.of(page - 1, size);
        switch (type) {
            case "username":
                userPage = userRepository.findByUserNameLike(text, pageable);
                break;
            case "idCard":
                userPage = userRepository.findByIdCardLike(text, pageable);
                break;
            case "phone":
                userPage = userRepository.findByPressLike(text, pageable);
                break;
            default:
                userPage = null;
                break;
        }

        return userPage;
    }

    /**
     * 添加用户
     * 0 - 添加用户失败
     * 1 - 添加用户成功
     * 2 - 用户名存在
     * 3 - 借书卡存在
     * 4 - 手机号存在
     * @param user 用户信息
     * @return 添加用户状态
     */
    @PostMapping("/save")
    public JSONObject save(@RequestBody User user) {
        JSONObject userMessage = new JSONObject();
        userMessage.put("message", "addUser");
        userMessage.put("statusCode", 0);
        User tempUser;

        // 判断用户是否为空
        if (user != null) {
            tempUser = userRepository.findByUsername(user.getUsername());
            // 判断用户名是否存在
            if (tempUser == null) {
                tempUser = userRepository.findByIdCard(user.getIdCard());
                // 判断借书卡是否存在
                if (tempUser == null) {
                    tempUser = userRepository.findByPhone(user.getPhone());
                    // 判断手机号是否存在
                    if (tempUser == null) {
                        // 设置用户信息
                        Integer bookCount = 3;
                        String userIdentity = user.getIdentity();
                        if ("教师".equals(userIdentity)) {
                            bookCount = 5;
                        }
                        if ("管理员".equals(userIdentity)) {
                            bookCount = 999;
                        }
                        user.setBookCount(bookCount);
                        user.setState(1);

                        // 添加用户
                        userRepository.save(user);
                        userMessage.replace("statusCode", 1);
                    }
                    else {
                        userMessage.replace("statusCode", 4);
                    }
                }
                else {
                    userMessage.replace("statusCode", 3);
                }
            }
            else {
                userMessage.replace("statusCode", 2);
            }
        }

        return userMessage;
    }

    /**
     * 更新用户信息
     * 0 - 更新用户失败
     * 1 - 更新用户成功
     * 2 - 手机号存在
     * @param user 用户信息
     * @return 更新用户状态
     */
    @PostMapping("/update")
    public JSONObject edit(@RequestBody User user) {
        JSONObject userMessage = new JSONObject();
        userMessage.put("message", "editUser");
        userMessage.put("statusCode", 0);
        User tempUser;

        // 判断用户是否为空
        if (user != null) {
            tempUser = userRepository.findByPhone(user.getPhone());
            // 判断手机号是否存在
            if (tempUser == null || user.getId().equals(tempUser.getId())) {
                // 添加用户
                userRepository.save(user);
                userMessage.replace("statusCode", 1);
            } else {
                userMessage.replace("statusCode", 2);
            }
        }

        return userMessage;
    }

}