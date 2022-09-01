package com.sparta.avengers.security.user;

import com.sparta.avengers.model.Account;
import com.sparta.avengers.repository.AccountRepository;
import com.sparta.avengers.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Account account = accountRepository.findByUserId(userId).orElseThrow(
                () -> new RuntimeException("Not Found Account")
        );
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setAccount(account);
        return userDetails;
    }
}