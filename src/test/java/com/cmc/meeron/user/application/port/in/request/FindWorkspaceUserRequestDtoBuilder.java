package com.cmc.meeron.user.application.port.in.request;

public class FindWorkspaceUserRequestDtoBuilder {

    public static FindWorkspaceUserRequestDto build() {
        return FindWorkspaceUserRequestDto.builder()
                .nickname("test")
                .workspaceId(1L)
                .build();
    }
}
