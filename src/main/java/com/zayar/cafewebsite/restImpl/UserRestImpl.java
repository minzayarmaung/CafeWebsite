package com.zayar.cafewebsite.restImpl;

import com.zayar.cafewebsite.constents.CafeConstants;
import com.zayar.cafewebsite.rest.UserRest;
import com.zayar.cafewebsite.service.UserService;
import com.zayar.cafewebsite.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
       try{
           return userService.login(requestMap);
       }
       catch (Exception e){
           e.printStackTrace();
       }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
