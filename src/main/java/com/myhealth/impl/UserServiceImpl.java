package com.myhealth.impl;

import com.myhealth.dto.UserInfo;
import com.myhealth.entity.UserProfile;
import com.myhealth.repository.UserProfileRepository;
import com.myhealth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserProfileRepository userProfileRepository;
    
    @Override
    public List<UserInfo> getAllUsers() {
        return userProfileRepository.findAll()
                .stream()
                .map(this::mapToUserInfo)
                .collect(Collectors.toList());
    }
    
    private UserInfo mapToUserInfo(UserProfile userProfile) {
        return new UserInfo(
                userProfile.getId(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getEmail(),
                userProfile.getProfilePictureUrl()
        );
    }
}