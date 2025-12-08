package com.sescity.vbase.vmanager.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.sescity.vbase.conf.OauthConfig;
import com.sescity.vbase.conf.SipConfig;
import com.sescity.vbase.conf.exception.ControllerException;
import com.sescity.vbase.conf.security.SecurityUtils;
import com.sescity.vbase.conf.security.dto.LoginUser;
import com.sescity.vbase.service.IUserService;
import com.sescity.vbase.storager.dao.dto.Role;
import com.sescity.vbase.storager.dao.dto.User;
import com.sescity.vbase.utils.oauth.LoginEntity;
import com.sescity.vbase.utils.oauth.OauthUtils;
import com.sescity.vbase.vmanager.bean.ErrorCode;
import com.sescity.vbase.vmanager.bean.VbaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.util.List;

@Tag(name = "用户管理")
@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired private OauthConfig oauthConfig;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private IUserService userService;

  @GetMapping("/login")
  @PostMapping("/login")
  @Operation(summary = "登录")
  @Parameter(name = "username", description = "用户名", required = true)
  @Parameter(name = "password", description = "密码", required = true)
  @Parameter(name = "code", description = "code")
  public LoginUser login(
      @RequestParam String username, @RequestParam String password, @RequestParam String code) {
    LoginUser user = null;
    try {
      user = SecurityUtils.login(username, password, authenticationManager);
    } catch (AuthenticationException e) {
      throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
    }
    if (user == null) {
      throw new ControllerException(ErrorCode.ERROR100.getCode(), "用户名或密码错误");
    } else {
      try {
        String response =
            OauthUtils.doLogin(
                //oauthConfig.getOauthServer(), new LoginEntity("admin", "abc123", code));
                oauthConfig.getOauthServer(), new LoginEntity(oauthConfig.getAdminUsername(), oauthConfig.getAdminPassword(), code),
                        oauthConfig);
        if (response == null) {
          throw new ControllerException(ErrorCode.ERROR100.getCode(), "远程认证调用失败。");
        }
        
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.containsKey("code")
            && Integer.parseInt(jsonObject.getString("code")) != 200) {
          throw new ControllerException(ErrorCode.ERROR100.getCode(), jsonObject.getString("msg"));
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }

    return user;
  }

  @PostMapping("/changePassword")
  @Operation(summary = "修改密码")
  @Parameter(name = "username", description = "用户名", required = true)
  @Parameter(name = "oldpassword", description = "旧密码", required = true)
  @Parameter(name = "password", description = "新密码", required = true)
  public void changePassword(@RequestParam String oldPassword, @RequestParam String password) {
    // 获取当前登录用户id
    LoginUser userInfo = SecurityUtils.getUserInfo();
    if (userInfo == null) {
      throw new ControllerException(ErrorCode.ERROR100);
    }
    String username = userInfo.getUsername();
    LoginUser user = null;
    try {
      user = SecurityUtils.login(username, oldPassword, authenticationManager);
      if (user == null) {
        throw new ControllerException(ErrorCode.ERROR100);
      }
      int userId = SecurityUtils.getUserId();
      boolean result = userService.changePassword(userId, password);
      if (!result) {
        throw new ControllerException(ErrorCode.ERROR100);
      }
    } catch (AuthenticationException e) {
      throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
    }
  }

  @PostMapping("/add")
  @Operation(summary = "添加用户")
  @Parameter(name = "username", description = "用户名", required = true)
  @Parameter(name = "password", description = "密码", required = true)
  @Parameter(name = "roleId", description = "角色ID", required = true)
  public void add(
      @RequestParam String username, @RequestParam String password, @RequestParam Integer roleId) {
    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password) || roleId == null) {
      throw new ControllerException(ErrorCode.ERROR400.getCode(), "参数不可为空");
    }
    // 获取当前登录用户id
    int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
    if (currenRoleId != 1) {
      // 只用角色id为1才可以删除和添加用户
      throw new ControllerException(ErrorCode.ERROR400.getCode(), "用户无权限");
    }

    int addResult = userService.addUser(username, password, roleId);
    if (addResult <= 0) {
      throw new ControllerException(ErrorCode.ERROR100);
    }
  }

  @DeleteMapping("/delete")
  @Operation(summary = "删除用户")
  @Parameter(name = "id", description = "用户Id", required = true)
  public void delete(@RequestParam Integer id) {
    // 获取当前登录用户id
    int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
    if (currenRoleId != 1) {
      // 只用角色id为0才可以删除和添加用户
      throw new ControllerException(ErrorCode.ERROR400.getCode(), "用户无权限");
    }
    int deleteResult = userService.deleteUser(id);
    if (deleteResult <= 0) {
      throw new ControllerException(ErrorCode.ERROR100);
    }
  }

  @GetMapping("/all")
  @Operation(summary = "查询用户")
  public List<User> all() {
    // 获取当前登录用户id
    return userService.getAllUsers();
  }

  /**
   * 分页查询用户
   *
   * @param page 当前页
   * @param count 每页查询数量
   * @return 分页用户列表
   */
  @GetMapping("/users")
  @Operation(summary = "分页查询用户")
  @Parameter(name = "page", description = "当前页", required = true)
  @Parameter(name = "count", description = "每页查询数量", required = true)
  public PageInfo<User> users(int page, int count) {
    return userService.getUsers(page, count);
  }

  @RequestMapping("/changePushKey")
  @Operation(summary = "修改pushkey")
  @Parameter(name = "userId", description = "用户Id", required = true)
  @Parameter(name = "pushKey", description = "新的pushKey", required = true)
  public void changePushKey(@RequestParam Integer userId, @RequestParam String pushKey) {
    // 获取当前登录用户id
    int currenRoleId = SecurityUtils.getUserInfo().getRole().getId();
    VbaseResult<String> result = new VbaseResult<>();
    if (currenRoleId != 1) {
      // 只用角色id为0才可以删除和添加用户
      throw new ControllerException(ErrorCode.ERROR400.getCode(), "用户无权限");
    }
    int resetPushKeyResult = userService.changePushKey(userId, pushKey);
    if (resetPushKeyResult <= 0) {
      throw new ControllerException(ErrorCode.ERROR100);
    }
  }

  @PostMapping("/changePasswordForAdmin")
  @Operation(summary = "管理员修改普通用户密码")
  @Parameter(name = "adminId", description = "管理员id", required = true)
  @Parameter(name = "userId", description = "用户id", required = true)
  @Parameter(name = "password", description = "新密码", required = true)
  public void changePasswordForAdmin(@RequestParam int userId, @RequestParam String password) {
    // 获取当前登录用户id
    LoginUser userInfo = SecurityUtils.getUserInfo();
    if (userInfo == null) {
      throw new ControllerException(ErrorCode.ERROR100);
    }
    Role role = userInfo.getRole();
    if (role != null && role.getId() == 1) {
      boolean result = userService.changePassword(userId, password);
      if (!result) {
        throw new ControllerException(ErrorCode.ERROR100);
      }
    }
  }
}
