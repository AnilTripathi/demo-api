package com.myhealth.service;

import com.myhealth.dto.UserInfo;
import com.myhealth.dto.userprofile.UserProfileDto;
import com.myhealth.dto.userprofile.UserProfileRequest;
import java.util.List;

public interface UserService {
    List<UserInfo> getAllUsers();
    UserProfileDto getCurrentUserProfile();
    UserProfileDto updateCurrentUserProfile(UserProfileRequest request);
}