package com.codeway.auth.service;

import com.codeway.api.user.UserServiceRpc;
import com.codeway.exception.custom.RemoteRpcException;
import com.codeway.model.dto.user.RoleDto;
import com.codeway.model.dto.user.UserDto;
import com.codeway.utils.JsonData;
import com.codeway.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用户信息服务
 * 实现 Spring Security的UserDetailsService接口方法，用于身份认证
 **/
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserServiceRpc userService;

	@Autowired
	ClientDetailsService clientDetailsService;

	/**
	 * 根据用户名查找账户信息并返回用户信息实体
	 *
	 * @param userJson 用户名
	 * @return 用于身份认证的 UserDetails 用户信息实体
	 */
	@Override
    public UserDetails loadUserByUsername(String userJson) {

		UserDto userDto = JsonUtil.jsonToPojo(userJson, UserDto.class);
		JsonData<UserDto> userByUser = userService.getUserInfo(userDto);
		if (!userByUser.isStatus()) {
			throw new RemoteRpcException(userByUser);
		}
		UserDto defUser = userByUser.getData();
		if (defUser == null) {
			return null;
		}
		String password = defUser.getPassword();
		List<GrantedAuthority> authorities = new ArrayList<>();
		Set<RoleDto> roles = defUser.getRoles();
		roles.forEach(role ->
				authorities.add(new SimpleGrantedAuthority(role.getId())));
		UserJwt userDetails = new UserJwt(defUser.getUserName(),
				password,
				defUser.getId(),
				defUser.getNickName(),
				defUser.getAvatar(),
				defUser.getEmail(),
				defUser.getPhone(),
				defUser.getAccount(),
				authorities);
		return userDetails;
    }

    /**
     * 获得登录者所有角色的权限集合.
     *
     * @param user
     * @return
     */
    /*private Set<GrantedAuthority> obtainGrantedAuthorities(User user) {
        Set<Role> roles = roleService.queryUserRolesByUserId(user.getId());
        log.info("user:{},roles:{}", user.getUsername(), roles);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .collect(Collectors.toSet());
    }*/
}
