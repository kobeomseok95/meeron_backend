package com.cmc.meeron.meeting.application.service;

import com.cmc.meeron.meeting.application.port.in.MeetingQueryUseCase;
import com.cmc.meeron.meeting.application.port.in.request.MeetingAttendeesRequestDto;
import com.cmc.meeron.meeting.application.port.in.request.TodayMeetingRequestDto;
import com.cmc.meeron.meeting.application.port.in.response.MeetingAttendeesResponseDto;
import com.cmc.meeron.meeting.application.port.in.response.TodayMeetingResponseDto;
import com.cmc.meeron.meeting.application.port.out.MeetingQueryPort;
import com.cmc.meeron.meeting.domain.Attendee;
import com.cmc.meeron.meeting.domain.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class MeetingQueryService implements MeetingQueryUseCase {

    private final MeetingQueryPort meetingQueryPort;

    @Override
    public List<TodayMeetingResponseDto> getTodayMeetings(TodayMeetingRequestDto todayMeetingRequestDto) {
        List<Meeting> todayMeetings = meetingQueryPort.findTodayMeetings(todayMeetingRequestDto.getWorkspaceId(), todayMeetingRequestDto.getWorkspaceUserId());
        return TodayMeetingResponseDto.fromEntities(todayMeetings);
    }

    @Override
    public MeetingAttendeesResponseDto getMeetingAttendees(MeetingAttendeesRequestDto meetingAttendeesRequestDto) {
        List<Attendee> attendees = meetingQueryPort.findWithWorkspaceUserByMeetingIdTeamId(
                meetingAttendeesRequestDto.getMeetingId(),
                meetingAttendeesRequestDto.getTeamId());
        MeetingAttendeesResponseDto meetingAttendeesResponseDto = MeetingAttendeesResponseDto.fromEntities(attendees);
        meetingAttendeesResponseDto.sort();
        return meetingAttendeesResponseDto;
    }
}
