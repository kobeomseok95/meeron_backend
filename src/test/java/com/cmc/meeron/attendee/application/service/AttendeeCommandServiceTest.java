package com.cmc.meeron.attendee.application.service;

import com.cmc.meeron.attendee.application.port.in.request.ChangeAttendStatusRequestDto;
import com.cmc.meeron.attendee.application.port.in.request.JoinAttendeesRequestDto;
import com.cmc.meeron.attendee.application.port.in.request.JoinAttendeesRequestDtoBuilder;
import com.cmc.meeron.attendee.application.port.out.AttendeeQueryPort;
import com.cmc.meeron.attendee.application.port.out.AttendeeToMeetingQueryPort;
import com.cmc.meeron.attendee.domain.AttendStatus;
import com.cmc.meeron.attendee.domain.Attendee;
import com.cmc.meeron.common.exception.meeting.AttendeeDuplicateException;
import com.cmc.meeron.common.exception.meeting.AttendeeNotFoundException;
import com.cmc.meeron.common.exception.meeting.MeetingNotFoundException;
import com.cmc.meeron.common.exception.workspace.WorkspaceUsersNotInEqualWorkspaceException;
import com.cmc.meeron.meeting.application.port.in.request.ChangeAttendStatusRequestDtoBuilder;
import com.cmc.meeron.meeting.domain.Attendees;
import com.cmc.meeron.meeting.domain.Meeting;
import com.cmc.meeron.support.TestImproved;
import com.cmc.meeron.workspaceuser.domain.WorkspaceUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cmc.meeron.meeting.MeetingFixture.MEETING_ATTEND_ATTENDEES;
import static com.cmc.meeron.workspace.WorkspaceFixture.WORKSPACE_1;
import static com.cmc.meeron.workspaceuser.WorkspaceUserFixture.WORKSPACE_USER_3;
import static com.cmc.meeron.workspaceuser.WorkspaceUserFixture.WORKSPACE_USER_4;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendeeCommandServiceTest {

    @Mock
    AttendeeToMeetingQueryPort attendeeToMeetingQueryPort;
    @Mock
    JoinMeetingValidator joinMeetingValidator;
    @Mock
    AttendeeQueryPort attendeeQueryPort;
    @InjectMocks
    AttendeeCommandService attendeeCommandService;

    private Meeting meetingWithAttendee;
    private Attendee changeStatusAttendee;

    @BeforeEach
    void setUp() {
        meetingWithAttendee = Meeting.builder()
                .workspace(WORKSPACE_1)
                .attendees(Attendees.builder()
                        .values(new ArrayList<>(Arrays.asList(Attendee.builder()
                                .workspaceUser(WorkspaceUser.builder()
                                        .workspace(WORKSPACE_1)
                                        .build())
                                .build())))
                        .build())
                .build();
        changeStatusAttendee = Attendee.builder()
                .id(1L)
                .attendStatus(AttendStatus.ATTEND)
                .build();
    }

    @AfterEach
    void tearDown() {
        meetingWithAttendee = null;
    }

    @DisplayName("회의 참가자 추가 - 성공")
    @Test
    void join_attendees_success() throws Exception {

        // given
        JoinAttendeesRequestDto requestDto = JoinAttendeesRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.of(meetingWithAttendee));
        when(joinMeetingValidator.workspaceUsersInEqualWorkspace(any(), any()))
                .thenReturn(List.of(WORKSPACE_USER_3, WORKSPACE_USER_4));

        // when
        attendeeCommandService.joinAttendees(requestDto);

        // then
        assertAll(
                () -> verify(attendeeToMeetingQueryPort).findWithAttendeesById(requestDto.getMeetingId()),
                () -> verify(joinMeetingValidator).workspaceUsersInEqualWorkspace(requestDto.getWorkspaceUserIds(),
                        meetingWithAttendee.getWorkspace()),
                () -> assertEquals(requestDto.getWorkspaceUserIds().size() + 1, meetingWithAttendee.getAttendees().size())
        );
    }

    @DisplayName("회의 참가자 추가 - 실패 / 회의가 존재하지 않을 경우")
    @Test
    void join_attendees_fail_not_found_meeting() throws Exception {

        // given
        JoinAttendeesRequestDto requestDto = JoinAttendeesRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.empty());

        // when, then
        assertThrows(
                MeetingNotFoundException.class,
                () -> attendeeCommandService.joinAttendees(requestDto)
        );
    }

    @DisplayName("회의 참가자 추가 - 실패 / 이미 참여한 참가자인 경우")
    @Test
    void join_attendees_fail_not_duplicate_attendees() throws Exception {

        // given
        JoinAttendeesRequestDto requestDto = JoinAttendeesRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.of(meetingWithAttendee));
        when(joinMeetingValidator.workspaceUsersInEqualWorkspace(any(), any()))
                .thenReturn(meetingWithAttendee.getAttendees()
                        .getValues()
                        .stream()
                        .map(Attendee::getWorkspaceUser)
                        .collect(Collectors.toList()));

        // when, then
        assertThrows(AttendeeDuplicateException.class,
                () -> attendeeCommandService.joinAttendees(requestDto));
    }

    @DisplayName("회의 참가자 추가 - 실패 / 회의 참가자 워크스페이스가 회의 워크스페이스와 다른 경우")
    @Test
    void join_attendees_fail_attendees_in_another_workspace() throws Exception {

        // given
        JoinAttendeesRequestDto requestDto = JoinAttendeesRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.of(meetingWithAttendee));
        when(joinMeetingValidator.workspaceUsersInEqualWorkspace(any(), any()))
                .thenThrow(new WorkspaceUsersNotInEqualWorkspaceException());

        // when, then
        assertThrows(
                WorkspaceUsersNotInEqualWorkspaceException.class,
                () -> attendeeCommandService.joinAttendees(requestDto)
        );
    }

    @Deprecated
    @DisplayName("회의 참가자 상태 변경 - 실패 / 회의를 찾을 수 없을 경우")
    @Test
    void change_attend_status_fail_not_found_meeting() throws Exception {

        // given
        ChangeAttendStatusRequestDto requestDto = ChangeAttendStatusRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.empty());

        // when, then
        assertThrows(MeetingNotFoundException.class,
                () -> attendeeCommandService.changeAttendStatus(requestDto));
    }

    @Deprecated
    @DisplayName("회의 참가자 상태 변경 - 실패 / 존재하지 않는 참가자인 경우")
    @Test
    void change_attend_status_fail_not_attendee() throws Exception {

        // given
        ChangeAttendStatusRequestDto requestDto = ChangeAttendStatusRequestDtoBuilder.buildFailRequest();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.of(MEETING_ATTEND_ATTENDEES));

        // when, then
        assertThrows(AttendeeNotFoundException.class,
                () -> attendeeCommandService.changeAttendStatus(requestDto));
    }

    @TestImproved(originMethod = "change_attend_status_fail_not_attendee")
    @DisplayName("회의 참가자 상태 변경 - 실패 / 존재하지 않는 참가자인 경우 V2")
    @Test
    void change_attend_status_fail_not_attendee_v2() throws Exception {

        // given
        ChangeAttendStatusRequestDto requestDto = ChangeAttendStatusRequestDtoBuilder.build();
        when(attendeeQueryPort.findById(any()))
                .thenReturn(Optional.empty());

        // when, then
        assertThrows(AttendeeNotFoundException.class,
                () -> attendeeCommandService.changeAttendStatusV2(requestDto));
    }

    @Deprecated
    @DisplayName("회의 참가자 상태 변경 - 성공")
    @Test
    void change_attend_status_success() throws Exception {

        // given
        ChangeAttendStatusRequestDto requestDto = ChangeAttendStatusRequestDtoBuilder.build();
        when(attendeeToMeetingQueryPort.findWithAttendeesById(any()))
                .thenReturn(Optional.of(MEETING_ATTEND_ATTENDEES));

        // when
        attendeeCommandService.changeAttendStatus(requestDto);

        // then
        verify(attendeeToMeetingQueryPort).findWithAttendeesById(requestDto.getMeetingId());
    }

    @TestImproved(originMethod = "change_attend_status_success")
    @DisplayName("회의 참가자 상태 변경 - 성공 V2")
    @Test
    void change_attend_status_success_v2() throws Exception {

        // given
        ChangeAttendStatusRequestDto requestDto = ChangeAttendStatusRequestDtoBuilder.build();
        when(attendeeQueryPort.findById(any()))
                .thenReturn(Optional.of(changeStatusAttendee));

        // when
        attendeeCommandService.changeAttendStatusV2(requestDto);

        // then
        assertAll(
                () -> verify(attendeeQueryPort).findById(requestDto.getAttendeeId()),
                () -> assertEquals(changeStatusAttendee.getAttendStatus(), AttendStatus.valueOf(requestDto.getStatus()))
        );
    }
}
