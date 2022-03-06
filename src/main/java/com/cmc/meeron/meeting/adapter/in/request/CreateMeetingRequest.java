package com.cmc.meeron.meeting.adapter.in.request;

import com.cmc.meeron.meeting.application.port.in.request.CreateMeetingRequestDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetingRequest {

    private LocalDate meetingDate;

    @NotNull(message = "회의 시작 시간을 'HH:mm' 형식으로 입력해주세요.")
    private LocalTime startTime;

    @NotNull(message = "회의 마감 시간을 'HH:mm' 형식으로 입력해주세요.")
    private LocalTime endTime;

    @NotBlank(message = "회의명을 3자 이상 30자 이하로 입력해주세요.")
    @Length(min = 3, max = 30, message = "회의명을 3자 이상 30자 이하로 입력해주세요.")
    private String meetingName;

    @NotBlank(message = "회의 성격을 1자 이상 10자 이하로 입력해주세요.")
    @Length(max = 10, message = "회의 성격을 1자 이상 10자 이하로 입력해주세요.")
    private String meetingPurpose;

    @NotNull(message = "담당 팀 ID를 입력해주세요.")
    private Long operationTeamId;

    private List<Long> meetingAdminIds;

    public CreateMeetingRequestDto toDto() {
        return CreateMeetingRequestDto.builder()
                .startDate(meetingDate)
                .startTime(startTime)
                .endTime(endTime)
                .meetingName(meetingName)
                .meetingPurpose(meetingPurpose)
                .operationTeamId(operationTeamId)
                .meetingManagerIds(meetingAdminIds)
                .build();
    }
}
