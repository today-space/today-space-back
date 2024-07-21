package com.complete.todayspace.global.security;

import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.user.repository.UserRepository;
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

        User user = userRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException("아이디, 비밀번호를 확인해주세요."));

        return new UserDetailsImpl(user);
    }

}
