package com.company.app.web.rest;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.app.utils.PaginationUtil;
import com.company.app.annotation.Patch;
import com.company.app.annotation.PatchRequestBody;
import com.company.app.domain.User;
import com.company.app.exception.UserNotFoundException;
import com.company.app.service.UserService;
import com.company.app.utils.ResourceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户接口
 * @author YunJ
 */
@Api(value="系统用户接口", tags = {"系统用户接口"})
@RestController
@RequestMapping("/api/user")
public class UserResource {
    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value="获取当前登录用户的信息")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo() {
        User user = userService.getCurrentUserInfo();
        user.setPassword("");
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value="重置用户的密码")
    @PatchMapping("/reset/{id}")
    public ResponseEntity updateAccount(@PathVariable String id) {
        userService.resetPassword(id);
        return ResponseEntity.ok(ResourceUtil.success());
    }

    @ApiOperation(value="获取指定 id 的用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "id", value = "用户 id", required = true, dataType = "String")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        User user = new User();
        user.setId(id);
        User result = userService.findUser(user);
        if (result == null) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value="查询用户信息", notes="查询条件可以组合使用")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "name", value = "用户名", required = false, dataType = "String")
    })
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(value = "name", required = false) String name,
                                      Pageable pageable) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.lambda().like(User::getUsername, name);
        }

        Page<User> page = userService.findUser(queryWrapper, PaginationUtil.getPage(pageable));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    @ApiOperation(value="删除指定 id 的用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResourceUtil.success());
    }

    @ApiOperation(value="新增系统用户")
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok(ResourceUtil.success());
    }

    @ApiOperation(value="修改系统用户信息")
    @PatchMapping("/{id}")
    @Patch(service = UserService.class, id = String.class)
    public ResponseEntity<?> updateUser(@PathVariable String id, @PatchRequestBody User user) {
        userService.update(user);
        return ResponseEntity.ok(ResourceUtil.success());
    }
}
