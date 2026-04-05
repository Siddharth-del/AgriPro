
package com.SmartAgriculture.Cropp.Security.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartAgriculture.Cropp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.SmartAgriculture.Cropp.model.User;


@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService{
   
    private final UserRepository userRepository;
     
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }    
    

    
}