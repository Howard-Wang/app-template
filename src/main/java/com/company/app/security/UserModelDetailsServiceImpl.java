package com.company.app.security;

import com.company.app.domain.User;
import com.company.app.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通过用户名认证用户
 * @author YunJ
 */
@Component("userDetailsService")
public class UserModelDetailsServiceImpl implements UserDetailsService {

   private final Logger log = LoggerFactory.getLogger(UserModelDetailsServiceImpl.class);

   private final UserService userService;

   public UserModelDetailsServiceImpl(UserService userService) {
      this.userService = userService;
   }

   @Override
   @Transactional(rollbackFor = Exception.class)
   public UserDetails loadUserByUsername(final String login) {
      log.debug("Authenticating user '{}'", login);
       List<User> users = userService.findByUsername(login);
       if (CollectionUtils.isEmpty(users)) {
           throw new UsernameNotFoundException("User with email " + login +
               " was not found in the database");
       }
       User user = users.get(0);
       return createSpringSecurityUser(login, user);
   }

   private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
       String[] roles = new String[1];
       roles[0] = user.getUserRole();
       List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles);
      return new org.springframework.security.core.userdetails.User(user.getUsername(),
         user.getPassword(),
          authorities);
   }
}
