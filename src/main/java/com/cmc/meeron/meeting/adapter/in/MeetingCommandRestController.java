package com.cmc.meeron.meeting.adapter.in;

import com.cmc.meeron.meeting.adapter.in.request.CreateAgendaRequest;
import com.cmc.meeron.meeting.adapter.in.request.CreateMeetingRequest;
import com.cmc.meeron.meeting.adapter.in.request.JoinAttendeesRequest;
import com.cmc.meeron.meeting.adapter.in.response.CreateAgendaResponse;
import com.cmc.meeron.meeting.adapter.in.response.CreateMeetingResponse;
import com.cmc.meeron.meeting.application.port.in.MeetingCommandUseCase;
import com.cmc.meeron.meeting.application.port.in.request.JoinAttendeesRequestDto;
import com.cmc.meeron.meeting.application.port.in.response.CreateAgendaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        Long createdTeamId = meetingCommandUseCase.createMeeting(createMeetingRequest.toDto());
        return CreateMeetingResponse.of(createdTeamId);
    }

    @PostMapping(value = "/{meetingId}/attendees")
    @ResponseStatus(HttpStatus.CREATED)
    public void joinAttendees(@PathVariable Long meetingId,
                              @RequestBody @Valid JoinAttendeesRequest joinAttendeesRequest) {
        JoinAttendeesRequestDto joinAttendeesRequestDto = JoinAttendeesRequestDto.of(meetingId,
                joinAttendeesRequest.getWorkspaceUserIds());
        meetingCommandUseCase.joinAttendees(joinAttendeesRequestDto);
    }

    @PostMapping("/{meetingId}/agendas")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAgendaResponse createAgendas(@PathVariable Long meetingId,
                                              @RequestBody @Valid CreateAgendaRequest createAgendaRequest) {
        List<CreateAgendaResponseDto> responseDtos =
                meetingCommandUseCase.createAgendas(createAgendaRequest.toRequestDto(meetingId));
        return CreateAgendaResponse.of(responseDtos);
    }
}