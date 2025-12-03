package com.sescity.vbase.service.impl;

import com.sescity.vbase.conf.exception.ControllerException;
import com.sescity.vbase.conf.security.SecurityUtils;
import com.sescity.vbase.service.IRoleService;
import com.sescity.vbase.service.IUserService;
import com.sescity.vbase.storager.dao.RoleMapper;
import com.sescity.vbase.storager.dao.UserMapper;
import com.sescity.vbase.storager.dao.dto.Role;
import com.sescity.vbase.storager.dao.dto.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sescity.vbase.utils.DateUtil;
import com.sescity.vbase.vmanager.bean.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IRoleService roleService;

    @Override
    public User getUser(String username, String password) {
        return userMapper.select(username, password);
    }

    @Override
    public boolean changePassword(int id, String password) {
        User user = userMapper.selectById(id);
        user.setPassword(SecurityUtils.encryptPassword(password));
        return userMapper.update(user) > 0;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public int addUser(String username, String password, int roleId) {
        User userByUsername = userMapper.getUserByUsername(username);
        if (userByUsername != null) {
            return 0;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(SecurityUtils.encryptPassword(password));
        //新增用户的pushKey的生成规则为md5(时间戳+用户名)
        user.setPushKey(DigestUtils.md5DigestAsHex((System.currentTimeMillis()+username).getBytes()));
        Role role = roleService.getRoleById(roleId);

        if (role == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "角色不存在");
        }
        user.setRole(role);
        user.setCreateTime(DateUtil.getNow());
        user.setUpdateTime(DateUtil.getNow());

        return userMapper.add(user);
    }
    @Override
    public int deleteUser(int id) {
        return userMapper.delete(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public int updateUsers(User user) {
        return userMapper.update(user);
    }


    @Override
    public boolean checkPushAuthority(String callId, String sign) {
        if (ObjectUtils.isEmpty(callId)) {
            return userMapper.checkPushAuthorityByCallId(sign).size() > 0;
        }else {
            return userMapper.checkPushAuthorityByCallIdAndSign(callId, sign).size() > 0;
        }
    }

    @Override
    public PageInfo<User> getUsers(int page, int count) {
        PageHelper.startPage(page, count);
        List<User> users = userMapper.getUsers();
        return new PageInfo<>(users);
    }

    @Override
    public int changePushKey(int id, String pushKey) {
        return userMapper.changePushKey(id,pushKey);
    }
}
