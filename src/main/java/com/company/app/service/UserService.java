package com.company.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.app.constants.AuthoritiesConstants;
import com.company.app.domain.User;
import com.company.app.exception.InvalidParamException;
import com.company.app.exception.UserExistException;
import com.company.app.exception.UserNotFoundException;
import com.company.app.mapper.UserMapper;
import com.company.app.utils.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 * @author YunJ
 */
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取当前用户的 username
     */
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new InvalidParamException();
        }
        return authentication.getName();
    }

    public User getCurrentUserInfo() {
        List<User> users = findByUsername(getCurrentUser());
        return users.get(0);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<User> findByUsername(String username) {
        log.debug("findByUsername() : username={}", username);
        Map<String, Object> map = new HashMap<>(1);
        map.put("username", username);
        return userMapper.selectByMap(map);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public User findUser(User user) {
        log.debug("findUser() : user={}", user.toString());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        return userMapper.selectOne(queryWrapper);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<User> findUser(QueryWrapper<User> queryWrapper, Page<User> page) {
        return userMapper.selectPage(page, queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        if (StringUtils.isEmpty(user.getUserRole()) || StringUtils.isEmpty(user.getUsername()) ||
            StringUtils.isEmpty(user.getPassword())) {
            throw new InvalidParamException();
        }
        if (!findByUsername(user.getUsername()).isEmpty()) {
            throw new UserExistException();
        }
        if (!AuthoritiesConstants.SUPER_ADMIN.equals(user.getUserRole()) &&
        !AuthoritiesConstants.USER.equals(user.getUserRole())) {
            throw new InvalidParamException("userRole");
        }
        user.setPassword(ResourceUtil.encodePwd(user.getPassword()));
        userMapper.insert(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String id) {
        User user = find(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        user.setPassword(ResourceUtil.encodePwd(AuthoritiesConstants.DEFAULT_PASSWORD));
        userMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        // 用户密码需要后端加密，才能入库
        User oldUser = find(user.getId());
        if (!oldUser.getPassword().equals(user.getPassword())) {
            user.setPassword(ResourceUtil.encodePwd(user.getPassword()));
        }
        userMapper.updateById(user);
    }

    public User find(String id) {
        return userMapper.selectById(id);
    }
}
