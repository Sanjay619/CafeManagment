package com.inn.cafee.serviceImpl;

import com.inn.cafee.constant.CafeConstants;
import com.inn.cafee.service.UserService;
import com.inn.cafee.urils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class UserServiceImpl implements UserService {


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside sign up{}", requestMap);
        if(validateSignUpMap(requestMap)){

        }else{
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_GATEWAY);

        }

        return null;
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){

       if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                &&requestMap.containsKey("email")&&requestMap.containsKey("password")){
           return true;
       }else{
           return false;
       }
    }
}
