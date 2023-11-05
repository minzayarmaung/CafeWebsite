package com.zayar.cafewebsite.serviceImpl;

import com.zayar.cafewebsite.JWT.CustomerUsersDetailsService;
import com.zayar.cafewebsite.JWT.JwtFilter;
import com.zayar.cafewebsite.JWT.JwtUtil;
import com.zayar.cafewebsite.POJO.User;
import com.zayar.cafewebsite.constents.CafeConstants;
import com.zayar.cafewebsite.dao.UserDao;
import com.zayar.cafewebsite.service.UserService;
import com.zayar.cafewebsite.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) throws InternalError {
        log.info("Inside singup{}", requestMap);
        if (validateSignUpMap(requestMap)) {
            User user = userDao.findByEmailId(requestMap.get("email"));

            if (Objects.isNull(user)) {

                userDao.save(getUserFromMap(requestMap));
                return CafeUtils.getResponseEntity("Registered Successfully",
                        HttpStatus.OK);

            } else {
                return CafeUtils.getResponseEntity("Email Already Exists",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return CafeUtils.getResponseEntity(CafeConstants.Invalid_Data, HttpStatus.BAD_REQUEST);
        }

    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")) {

            return true;

        } else {
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
        log.info("Inside Login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestMap.get("email"),
                            requestMap.get("password")
                    )
            );

            if (auth.isAuthenticated()) {
                if (customerUsersDetailsService.getUserDetails().getStatus()
                        .equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService
                                            .getUserDetails()
                                            .getEmail(),
                                    customerUsersDetailsService.getUserDetails()
                                            .getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>
                            ("{\"message\":\"" + "Wait for Admin Approval" + "\"}",
                                    HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
            log.error("{}", e);
        }
        return new ResponseEntity<String>
                ("{\"message\":\"" + "Bad Credentials" + "\"}",
                        HttpStatus.BAD_REQUEST);
    }
}

