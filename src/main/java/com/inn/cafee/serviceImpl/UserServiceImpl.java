package com.inn.cafee.serviceImpl;

import com.inn.cafee.JWT.CustomerUserDetailsService;
import com.inn.cafee.JWT.JWTFileter;
import com.inn.cafee.JWT.JWTUtil;
import com.inn.cafee.POJO.User;
import com.inn.cafee.constant.CafeConstants;
import com.inn.cafee.dao.UserDao;
import com.inn.cafee.service.UserService;
import com.inn.cafee.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JWTUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside sign up{}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {

                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully register.", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exits", HttpStatus.BAD_GATEWAY);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_GATEWAY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("" + ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {

        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")) {

            log.info("validating true");
            return true;
        } else {
            log.info("validating false");
            return false;
        }
    }

    private User getUserFromMap(Map<String, String> requestMap) {

        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;

    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login" + requestMap);
        try {

            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"));

            Authentication auth = authenticationManager.authenticate(authReq);

//            Authentication auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
//            );

            if(auth.isAuthenticated()){
                if(customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetails().getName(),
                                    customerUserDetailsService.getUserDetails().getRole())+"\"}", HttpStatus.OK);

                }else{
                    new ResponseEntity<String>("{\"message\":\""+"wait for admin approval."+"\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            log.info("{}" + ex);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bed Credentials."+"\"}",
                HttpStatus.BAD_REQUEST);
    }
}
