package com.cmc.meeron.meeting.adapter.in;

import com.cmc.meeron.meeting.adapter.in.request.CreateMeetingRequest;
import com.cmc.meeron.meeting.adapter.in.request.JoinAttendeesRequest;
import com.cmc.meeron.meeting.adapter.in.response.CreateMeetingResponse;
import com.cmc.meeron.meeting.application.port.in.MeetingCommandUseCase;
import com.cmc.meeron.meeting.application.port.in.request.JoinAttendeesRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingCommandRestController {

    private final MeetingCommandUseCase meetingCommandUseCase;
    private final MeetingDayValidator meetingDayValidator;

    @InitBinder("createMeetingRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(meetingDayValidator);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMeetingResponse createMeeting(@RequestBody @Valid CreateMeetingRequest createMeetingRequest) {
        Long createdTeamId = meetingCommandUseCase.creteMeeting(createMeetingRequest.toDto());
        return CreateMeetingResponse.of(createdTeamId);
    }

    @PostMapping("/{meetingId}/attendees")
    @ResponseStatus(HttpStatus.CREATED)
    public void joinAttendees(@PathVariable Long meetingId,
                              @RequestBody @Valid JoinAttendeesRequest joinAttendeesRequest) {
        JoinAttendeesRequestDto joinAttendeesRequestDto = JoinAttendeesRequestDto.of(meetingId,
                joinAttendeesRequest.getWorkspaceUserIds());
        meetingCommandUseCase.joinAttendees(joinAttendeesRequestDto);
    }
}
