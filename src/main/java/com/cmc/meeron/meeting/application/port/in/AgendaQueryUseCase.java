package com.cmc.meeron.meeting.application.port.in;

import com.cmc.meeron.meeting.application.port.in.request.FindAgendaIssuesFilesRequestDto;
import com.cmc.meeron.meeting.application.port.in.response.AgendaIssuesFilesResponseDto;
import com.cmc.meeron.meeting.application.port.in.response.AgendaCountResponseDto;

public interface AgendaQueryUseCase {

    AgendaCountResponseDto getAgendaCountsByMeetingId(Long meetingId);

    AgendaIssuesFilesResponseDto getAgendaIssuesFiles(FindAgendaIssuesFilesRequestDto findAgendaIssuesFilesRequestDto);
}