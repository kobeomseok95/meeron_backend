package com.cmc.meeron.team.application.port.in.request;

import com.cmc.meeron.common.advice.workspaceuser.WorkspaceUserIdCheckable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteTeamRequestDto implements WorkspaceUserIdCheckable {

    private Long adminWorkspaceUserId;
    private Long teamId;
}