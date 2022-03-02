package com.cmc.meeron.user.application;

import com.cmc.meeron.auth.domain.AuthUser;
import com.cmc.meeron.user.application.dto.response.MeResponseDto;
import com.cmc.meeron.user.application.dto.response.MyWorkspaceUserResponseDto;

import java.util.List;

public interface UserQueryUseCase {

    MeResponseDto getMe(AuthUser authUser);

    List<MyWorkspaceUserResponseDto> getMyWorkspaceUsers(Long userId);

    MyWorkspaceUserResponseDto getMyWorkspaceUser(Long workspaceUserId);
}