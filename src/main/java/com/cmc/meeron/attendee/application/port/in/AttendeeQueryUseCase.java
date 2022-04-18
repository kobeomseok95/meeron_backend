package com.cmc.meeron.attendee.application.port.in;

import com.cmc.meeron.attendee.application.port.in.request.MeetingTeamAttendeesRequestDto;
import com.cmc.meeron.attendee.application.port.in.response.MeetingAttendeesResponseDto;
import com.cmc.meeron.attendee.application.port.in.response.MeetingTeamAttendeesResponseDto;

import java.util.List;

public interface AttendeeQueryUseCase {

    List<MeetingAttendeesResponseDto> getMeetingAttendees(Long meetingId);

    MeetingTeamAttendeesResponseDto getMeetingTeamAttendees(MeetingTeamAttendeesRequestDto meetingTeamAttendeesRequestDto);
}