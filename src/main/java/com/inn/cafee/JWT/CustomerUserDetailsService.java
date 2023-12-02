package com.inn.cafee.JWT;

import com.inn.cafee.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.inn.cafee.POJO.User userDetails;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        userDetails = userDao.findByEmailId(email);

        if(!Objects.isNull(userDetails))
            return new User(userDetails.getEmail(), userDetails.getPassword(), new ArrayList());
        else
            throw new UsernameNotFoundException("User not found");

    }

    public com.inn.cafee.POJO.User getUserDetails(){
        return userDetails;
    }
}
