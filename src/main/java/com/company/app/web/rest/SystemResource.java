package com.company.app.web.rest;

import com.company.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息
 * @author YunJ
 */
@Api(value="系统信息", tags = {"系统信息"})
@RestController
@RequestMapping("/api/system")
public class SystemResource {

    @Value("${app.version}")
    private String systemVersion;

    private final UserService userService;

    public SystemResource(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value="获取后端版本号")
    @GetMapping(value = "/version")
    public ResponseEntity getSystemVersion() {
        Map<String, String> map = new HashMap<>(1);
        map.put("version", systemVersion);
        return ResponseEntity.ok(map);
    }
}
