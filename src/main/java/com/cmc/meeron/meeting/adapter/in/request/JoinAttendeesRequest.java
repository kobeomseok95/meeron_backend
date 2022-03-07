package com.cmc.meeron.meeting.adapter.in.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinAttendeesRequest {

    @Builder.Default
    @NotEmpty(message = "참가자의 워크스페이스 유저 ID는 반드시 하나 이상이어야 합니다.")
    private List<Long> workspaceUserIds = new ArrayList<>();
}
