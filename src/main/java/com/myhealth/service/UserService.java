package com.myhealth.service;

import com.myhealth.dto.UserInfo;
import java.util.List;

public interface UserService {
    List<UserInfo> getAllUsers();
}