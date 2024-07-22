package com.complete.todayspace.global.security;

import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException(ErrorCode.CHECK_USERNAME_PASSWORD.getMessage()));

        return new UserDetailsImpl(user);
    }

}
