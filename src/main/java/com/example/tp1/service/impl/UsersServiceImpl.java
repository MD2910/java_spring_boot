package com.example.tp1.service.impl;

import com.example.tp1.constant.RolesConstant;
import com.example.tp1.entity.Roles;
import com.example.tp1.entity.Users;
import com.example.tp1.repository.UsersRepo;
import com.example.tp1.service.RolesService;
import com.example.tp1.service.UsersService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;

@Service
public class UsersServiceImpl implements UsersService {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    @Autowired
    private UsersRepo repo;

    @Autowired
    private RolesService rolesService;

    @Override
//    public Users findByUsername(String username) {
//        return repo.findByUsername(username);
//    }
    public  Users doLogin(Users userRequest){
        Users userReponse = repo.findByUsername(userRequest.getUsername());
        if(ObjectUtils.isNotEmpty(userReponse)) {
            boolean checkPassword = bcrypt.matches(userRequest.getHashPassword(), userReponse.getHashPassword());
            return checkPassword ? userReponse : null;
        }
        return null;
    }


    @Override
    @Transactional(rollbackOn = {Throwable.class})
    public Users save(Users user) throws SQLException {
        Roles role = rolesService.findByDescription(RolesConstant.USER);
        user.setRoles(role);
        user.setDeleted(Boolean.FALSE);

        String hashPassword = bcrypt.encode(user.getHashPassword());
        user.setHashPassword(hashPassword);

        return repo.saveAndFlush(user);
    }

}
