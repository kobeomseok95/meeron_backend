package com.cmc.meeron.meeting.adapter.in;

import com.cmc.meeron.common.exception.ClientErrorCode;
import com.cmc.meeron.common.exception.meeting.MeetingErrorCode;
import com.cmc.meeron.common.exception.meeting.MeetingNotFoundException;
import com.cmc.meeron.common.util.LocalDateTimeUtil;
import com.cmc.meeron.meeting.application.port.in.response.*;
import com.cmc.meeron.support.restdocs.RestDocsTestSupport;
import com.cmc.meeron.support.security.WithMockJwt;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static com.cmc.meeron.config.RestDocsConfig.field;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockJwt
class MeetingQueryRestControllerTest extends RestDocsTestSupport {

    @DisplayName("오늘 회의 리스트 조회 - 성공")
    @Test
    void today_meetings_list_success() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("workspaceId", "1");
        params.add("workspaceUserId", "1");
        List<TodayMeetingResponseDto> responseDtos = TodayMeetingResponseDtoBuilder.buildList();
        when(meetingQueryUseCase.getTodayMeetings(any()))
                .thenReturn(responseDtos);

        // when, then, docs
        TodayMeetingResponseDto responseDto = responseDtos.get(0);
        TodayMeetingResponseDto.ImproveMeetingResponseDto meeting = responseDto.getMeetingResponseDto();
        TodayMeetingResponseDto.ImproveTeamResponseDto team = responseDto.getTeamResponseDto();
        List<TodayMeetingResponseDto.ImproveAgendaResponseDto> agendaResponseDtos = responseDto.getAgendaResponseDtos();
        TodayMeetingResponseDto.ImproveAgendaResponseDto agenda1 = agendaResponseDtos.get(0);
        TodayMeetingResponseDto.ImproveAgendaResponseDto agenda2 = agendaResponseDtos.get(1);
        List<TodayMeetingResponseDto.ImproveWorkspaceUserResponseDto> adminResponseDtos = responseDto.getAdminResponseDto();
        TodayMeetingResponseDto.ImproveWorkspaceUserResponseDto admin1 = adminResponseDtos.get(0);
        TodayMeetingResponseDto.ImproveWorkspaceUserResponseDto admin2 = adminResponseDtos.get(1);
        TodayMeetingResponseDto.ImproveAttendCountResponseDto count = responseDto.getAttendCountResponseDto();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/today")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings", hasSize(1)))
                .andExpect(jsonPath("$.meetings[0].meeting.meetingId", is(meeting.getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[0].meeting.startDate", is(LocalDateTimeUtil.convertDate(meeting.getStartDate()))))
                .andExpect(jsonPath("$.meetings[0].meeting.startTime", is(LocalDateTimeUtil.convertTime(meeting.getStartTime()))))
                .andExpect(jsonPath("$.meetings[0].meeting.endTime", is(LocalDateTimeUtil.convertTime(meeting.getEndTime()))))
                .andExpect(jsonPath("$.meetings[0].meeting.meetingName", is(meeting.getName())))
                .andExpect(jsonPath("$.meetings[0].meeting.purpose", is(meeting.getPurpose())))
                .andExpect(jsonPath("$.meetings[0].meeting.place", is(meeting.getPlace())))
                .andExpect(jsonPath("$.meetings[0].team.teamId", is(team.getTeamId().intValue())))
                .andExpect(jsonPath("$.meetings[0].team.teamName", is(team.getTeamName())))
                .andExpect(jsonPath("$.meetings[0].agendas", hasSize(2)))
                .andExpect(jsonPath("$.meetings[0].agendas[0].agendaId", is(agenda1.getAgendaId().intValue())))
                .andExpect(jsonPath("$.meetings[0].agendas[0].agendaOrder", is(agenda1.getAgendaOrder())))
                .andExpect(jsonPath("$.meetings[0].agendas[0].agendaName", is(agenda1.getAgendaName())))
                .andExpect(jsonPath("$.meetings[0].agendas[0].agendaResult", is(agenda1.getAgendaResult())))
                .andExpect(jsonPath("$.meetings[0].agendas[1].agendaId", is(agenda2.getAgendaId().intValue())))
                .andExpect(jsonPath("$.meetings[0].agendas[1].agendaOrder", is(agenda2.getAgendaOrder())))
                .andExpect(jsonPath("$.meetings[0].agendas[1].agendaName", is(agenda2.getAgendaName())))
                .andExpect(jsonPath("$.meetings[0].agendas[1].agendaResult", is(agenda2.getAgendaResult())))
                .andExpect(jsonPath("$.meetings[0].admins", hasSize(2)))
                .andExpect(jsonPath("$.meetings[0].admins[0].workspaceUserId", is(admin1.getWorkspaceUserId().intValue())))
                .andExpect(jsonPath("$.meetings[0].admins[0].workspaceId", is(admin1.getWorkspaceId().intValue())))
                .andExpect(jsonPath("$.meetings[0].admins[0].workspaceAdmin", is(admin1.isWorkspaceAdmin())))
                .andExpect(jsonPath("$.meetings[0].admins[0].nickname", is(admin1.getNickname())))
                .andExpect(jsonPath("$.meetings[0].admins[0].position", is(admin1.getPosition())))
                .andExpect(jsonPath("$.meetings[0].admins[0].profileImageUrl", is(admin1.getProfileImageUrl())))
                .andExpect(jsonPath("$.meetings[0].admins[0].email", is(admin1.getEmail())))
                .andExpect(jsonPath("$.meetings[0].admins[0].phone", is(admin1.getPhone())))
                .andExpect(jsonPath("$.meetings[0].admins[0].phone", is(admin1.getPhone())))
                .andExpect(jsonPath("$.meetings[0].admins[1].workspaceUserId", is(admin2.getWorkspaceUserId().intValue())))
                .andExpect(jsonPath("$.meetings[0].admins[1].workspaceId", is(admin2.getWorkspaceId().intValue())))
                .andExpect(jsonPath("$.meetings[0].admins[1].workspaceAdmin", is(admin2.isWorkspaceAdmin())))
                .andExpect(jsonPath("$.meetings[0].admins[1].nickname", is(admin2.getNickname())))
                .andExpect(jsonPath("$.meetings[0].admins[1].position", is(admin2.getPosition())))
                .andExpect(jsonPath("$.meetings[0].admins[1].profileImageUrl", is(admin2.getProfileImageUrl())))
                .andExpect(jsonPath("$.meetings[0].admins[1].email", is(admin2.getEmail())))
                .andExpect(jsonPath("$.meetings[0].admins[1].phone", is(admin2.getPhone())))
                .andExpect(jsonPath("$.meetings[0].attendCount.attend", is(count.getAttend())))
                .andExpect(jsonPath("$.meetings[0].attendCount.absent", is(count.getAbsent())))
                .andExpect(jsonPath("$.meetings[0].attendCount.unknown", is(count.getUnknown())))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("workspaceId").description("워크스페이스 ID"),
                                parameterWithName("workspaceUserId").description("워크스페이스 유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("meetings[].meeting.meetingId").type(JsonFieldType.NUMBER).description("회의 ID"),
                                fieldWithPath("meetings[].meeting.meetingName").type(JsonFieldType.STRING).description("회의 명"),
                                fieldWithPath("meetings[].meeting.startDate").type(JsonFieldType.STRING).description("회의 진행 날짜"),
                                fieldWithPath("meetings[].meeting.startTime").type(JsonFieldType.STRING).description("회의 시작 시간"),
                                fieldWithPath("meetings[].meeting.endTime").type(JsonFieldType.STRING).description("회의 종료 시간"),
                                fieldWithPath("meetings[].meeting.purpose").type(JsonFieldType.STRING).description("회의 성격"),
                                fieldWithPath("meetings[].meeting.place").type(JsonFieldType.STRING).description("회의 장소"),
                                fieldWithPath("meetings[].team.teamId").type(JsonFieldType.NUMBER).description("회의 주최 팀 ID"),
                                fieldWithPath("meetings[].team.teamName").type(JsonFieldType.STRING).description("회의 주최 팀 명"),
                                fieldWithPath("meetings[].agendas[].agendaId").type(JsonFieldType.NUMBER).description("아젠다 ID"),
                                fieldWithPath("meetings[].agendas[].agendaName").type(JsonFieldType.STRING).description("아젠다 명"),
                                fieldWithPath("meetings[].agendas[].agendaOrder").type(JsonFieldType.NUMBER).description("아젠다 순서"),
                                fieldWithPath("meetings[].agendas[].agendaResult").type(JsonFieldType.STRING).description("아젠다 결과"),
                                fieldWithPath("meetings[].admins[].workspaceUserId").type(JsonFieldType.NUMBER).description("회의 관리자의 워크스페이스 유저 ID"),
                                fieldWithPath("meetings[].admins[].workspaceId").type(JsonFieldType.NUMBER).description("회의 관리자의 워크스페이스 ID"),
                                fieldWithPath("meetings[].admins[].workspaceAdmin").type(JsonFieldType.BOOLEAN).description("회의 관리자의 워크스페이스 관리 여부"),
                                fieldWithPath("meetings[].admins[].nickname").type(JsonFieldType.STRING).description("회의 관리자의 닉네임"),
                                fieldWithPath("meetings[].admins[].position").type(JsonFieldType.STRING).description("회의 관리자의 직책"),
                                fieldWithPath("meetings[].admins[].profileImageUrl").type(JsonFieldType.STRING).description("회의 관리자의 프로필 이미지 URL"),
                                fieldWithPath("meetings[].admins[].email").type(JsonFieldType.STRING).description("회의 관리자의 메일"),
                                fieldWithPath("meetings[].admins[].phone").type(JsonFieldType.STRING).description("회의 관리자의 휴대전화번호"),
                                fieldWithPath("meetings[].attendCount.attend").type(JsonFieldType.NUMBER).description("회의 참가자 수"),
                                fieldWithPath("meetings[].attendCount.absent").type(JsonFieldType.NUMBER).description("회의 불참자 수"),
                                fieldWithPath("meetings[].attendCount.unknown").type(JsonFieldType.NUMBER).description("회의 참여 응답을 하지 않은 사람의 수")
                        )
                ));
    }

    @DisplayName("오늘의 회의 리스트 조회 - 실패 / 워크스페이스, 워크스페이스 회원 ID를 파라미터로 주지 않을 경우")
    @Test
    void today_meetings_list_fail_required_workspace_workspace_user_id() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("workspaceId", "");
        params.add("workspaceUserId", "");

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/today")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    private List<Integer> getDays() {
        return List.of(10, 20, 15, 21);
    }

    @DisplayName("워크스페이스 캘린더에서 회의 날짜 조회 - 실패 / date(yyyy/M)를 제대로 주지 않을 경우")
    @ParameterizedTest
    @MethodSource("failMeetingDaysParameters")
    void get_workspace_meeting_days_fail_require_yearMonth(MultiValueMap<String, String> params) throws Exception {

        // given, when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/days")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.code", is(ClientErrorCode.BIND_EXCEPTION.getCode())))
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    private static Stream<Arguments> failMeetingDaysParameters() {
        MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
        params1.add("date", "2022/");
        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add("date", "20");
        MultiValueMap<String, String> params3 = new LinkedMultiValueMap<>();
        params3.add("date", "");
        MultiValueMap<String, String> params4 = new LinkedMultiValueMap<>();
        params4.add("date", null);
        MultiValueMap<String, String> params5 = new LinkedMultiValueMap<>();
        params5.add("date", "2022/15");
        return Stream.of(
                Arguments.of(params1),
                Arguments.of(params2),
                Arguments.of(params3),
                Arguments.of(params4),
                Arguments.of(params5)
        );
    }

    @DisplayName("캘린더에서 이번 달 회의 날짜 조회 - 성공 / 워크스페이스 캘린더의 경우")
    @Test
    void get_meeting_days_success_workspace() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", "2022/3");
        params.add("type", "workspace");
        params.add("id", "1");
        List<Integer> days = meetingDaysStubAndReturn();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/days")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days", hasSize(4)))
                .andExpect(jsonPath("$.days[0]", is(days.get(0))))
                .andExpect(jsonPath("$.days[1]", is(days.get(1))))
                .andExpect(jsonPath("$.days[2]", is(days.get(2))))
                .andExpect(jsonPath("$.days[3]", is(days.get(3))))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("워크스페이스의 경우 'workspace' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("워크스페이스 ID"),
                                parameterWithName("date").description("찾을 년 월").attributes(field("constraints", "yyyy/M 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("days[]").description("검색하는 년 월에 해당하는 회의 날짜(day)들")
                        )
                ));
    }

    private List<Integer> meetingDaysStubAndReturn() {
        List<Integer> days = getDays();
        when(meetingCalendarQueryUseCaseFactory.getMeetingDays(any(), any(), any()))
                .thenReturn(days);
        return days;
    }

    @DisplayName("캘린더에서 이번 달 회의 날짜 조회 - 성공 / 나의 미론, 내 캘린더의 경우")
    @Test
    void get_meeting_days_success_workspace_user() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", "2022/3");
        params.add("type", "workspace_user");
        params.add("id", "1");
        List<Integer> days = meetingDaysStubAndReturn();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/days")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days", hasSize(4)))
                .andExpect(jsonPath("$.days[0]", is(days.get(0))))
                .andExpect(jsonPath("$.days[1]", is(days.get(1))))
                .andExpect(jsonPath("$.days[2]", is(days.get(2))))
                .andExpect(jsonPath("$.days[3]", is(days.get(3))))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("나의 캘린더의 경우 'workspace_user' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("userId"),
                                parameterWithName("date").description("찾을 년 월").attributes(field("constraints", "yyyy/M 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("days[]").description("검색하는 년 월에 해당하는 회의 날짜(day)들")
                        )
                ));
    }

    @DisplayName("캘린더에서 이번 달 회의 날짜 조회 - 성공 / 팀 캘린더의 경우")
    @Test
    void get_meeting_days_success_team() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", "2022/02");
        params.add("type", "team");
        params.add("id", "1");
        List<Integer> days = meetingDaysStubAndReturn();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/days")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days", hasSize(4)))
                .andExpect(jsonPath("$.days[0]", is(days.get(0))))
                .andExpect(jsonPath("$.days[1]", is(days.get(1))))
                .andExpect(jsonPath("$.days[2]", is(days.get(2))))
                .andExpect(jsonPath("$.days[3]", is(days.get(3))))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("팀 캘린더의 경우 'team' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("팀 ID"),
                                parameterWithName("date").description("찾을 년 월").attributes(field("constraints", "yyyy/M 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("days[]").description("검색하는 년 월에 해당하는 회의 날짜(day)들")
                        )
                ));
    }

    @DisplayName("워크스페이스 캘린더에서 선택한 날짜의 회의 조회 - 실패 / date(yyyy/M/d)를 제대로 주지 않을 경우")
    @ParameterizedTest
    @MethodSource("failDayMeetingsParameters")
    void get_workspace_day_meetings_fail_require_localDate(MultiValueMap<String, String> params) throws Exception {

        // given, when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/day")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.code", is(ClientErrorCode.BIND_EXCEPTION.getCode())))
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    private static Stream<Arguments> failDayMeetingsParameters() {
        MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
        params1.add("date", "2022/");
        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add("date", "20");
        MultiValueMap<String, String> params3 = new LinkedMultiValueMap<>();
        params3.add("date", "");
        MultiValueMap<String, String> params4 = new LinkedMultiValueMap<>();
        params4.add("date", null);
        MultiValueMap<String, String> params5 = new LinkedMultiValueMap<>();
        params5.add("date", "2022/02");
        return Stream.of(
                Arguments.of(params1),
                Arguments.of(params2),
                Arguments.of(params3),
                Arguments.of(params4),
                Arguments.of(params5)
        );
    }

    @DisplayName("캘린더에서 선택한 날짜의 회의 조회 - 성공 / 워크스페이스의 경우")
    @Test
    void get_day_meetings_success_workspace() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", LocalDateTimeUtil.nowDate());
        params.add("type", "workspace");
        params.add("id", "1");
        List<DayMeetingResponseDto> responseDtos = getDayMeetingResponseDtos();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/day")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings", hasSize(2)))
                .andExpect(jsonPath("$.meetings[0].meetingId", is(responseDtos.get(0).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[0].meetingName", is(responseDtos.get(0).getMeetingName())))
                .andExpect(jsonPath("$.meetings[0].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(0).getStartDate()))))
                .andExpect(jsonPath("$.meetings[0].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getStartTime()))))
                .andExpect(jsonPath("$.meetings[0].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getEndTime()))))
                .andExpect(jsonPath("$.meetings[0].purpose", is(responseDtos.get(0).getPurpose())))
                .andExpect(jsonPath("$.meetings[0].place", is(responseDtos.get(0).getPlace())))
                .andExpect(jsonPath("$.meetings[0].workspaceId", is(0)))
                .andExpect(jsonPath("$.meetings[0].workspaceName", emptyString()))
                .andExpect(jsonPath("$.meetings[0].workspaceLogoUrl", emptyString()))
                .andExpect(jsonPath("$.meetings[1].meetingId", is(responseDtos.get(1).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[1].meetingName", is(responseDtos.get(1).getMeetingName())))
                .andExpect(jsonPath("$.meetings[1].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(1).getStartDate()))))
                .andExpect(jsonPath("$.meetings[1].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getStartTime()))))
                .andExpect(jsonPath("$.meetings[1].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getEndTime()))))
                .andExpect(jsonPath("$.meetings[1].purpose", is(responseDtos.get(1).getPurpose())))
                .andExpect(jsonPath("$.meetings[1].place", is(responseDtos.get(1).getPlace())))
                .andExpect(jsonPath("$.meetings[1].workspaceId", is(0)))
                .andExpect(jsonPath("$.meetings[1].workspaceName", emptyString()))
                .andExpect(jsonPath("$.meetings[1].workspaceLogoUrl", emptyString()))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("워크스페이스의 경우 'workspace' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("워크스페이스 ID"),
                                parameterWithName("date").description("찾을 년 월 일").attributes(field("constraints", "yyyy/M/d 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("meetings[].meetingId").type(JsonFieldType.NUMBER).description("회의 ID"),
                                fieldWithPath("meetings[].meetingName").type(JsonFieldType.STRING).description("회의 명"),
                                fieldWithPath("meetings[].startDate").type(JsonFieldType.STRING).description("회의 날짜"),
                                fieldWithPath("meetings[].startTime").type(JsonFieldType.STRING).description("회의 시작 시간"),
                                fieldWithPath("meetings[].endTime").type(JsonFieldType.STRING).description("회의 종료 시간"),
                                fieldWithPath("meetings[].purpose").type(JsonFieldType.STRING).description("회의 성격"),
                                fieldWithPath("meetings[].place").type(JsonFieldType.STRING).description("회의 장소"),
                                fieldWithPath("meetings[].workspaceId").type(JsonFieldType.NUMBER).description("워크스페이스 ID / 워크스페이스나 팀 조회 시 0으로 표기"),
                                fieldWithPath("meetings[].workspaceName").type(JsonFieldType.STRING).description("워크스페이스 명 / 워크스페이스나 팀 조회 시 \"\" 로 표기"),
                                fieldWithPath("meetings[].workspaceLogoUrl").type(JsonFieldType.STRING).description("워크스페이스 로고 URL / 워크스페이스나 팀 조회 시 \"\" 로 표기")
                        )
                ));
    }

    private List<DayMeetingResponseDto> getDayMeetingResponseDtos() {
        List<DayMeetingResponseDto> responseDtos = getDayMeetingsWorkspaceAndTeam();
        when(meetingCalendarQueryUseCaseFactory.getDayMeetings(any(), any(), any()))
                .thenReturn(responseDtos);
        return responseDtos;
    }

    private List<DayMeetingResponseDto> getDayMeetingsWorkspaceAndTeam() {
        return List.of(
                DayMeetingResponseDto.builder()
                        .meetingId(1L)
                        .startDate(LocalDate.now())
                        .startTime(LocalTime.now())
                        .endTime(LocalTime.now().plusHours(2))
                        .meetingName("테스트회의1")
                        .purpose("테스트회의성격1")
                        .place("테스트장소1")
                        .workspaceId(0L)
                        .workspaceName("")
                        .workspaceLogoUrl("")
                        .build(),
                DayMeetingResponseDto.builder()
                        .meetingId(2L)
                        .startDate(LocalDate.now())
                        .startTime(LocalTime.now())
                        .endTime(LocalTime.now().plusHours(2))
                        .meetingName("테스트회의2")
                        .purpose("테스트회의성격2")
                        .place("테스트장소2")
                        .workspaceId(0L)
                        .workspaceName("")
                        .workspaceLogoUrl("")
                        .build()
        );
    }

    @DisplayName("캘린더에서 선택한 날짜의 회의 조회 - 성공 / 나의 미론, 내 캘린더의 경우")
    @Test
    void get_day_meetings_success_workspace_user() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", LocalDateTimeUtil.nowDate());
        params.add("type", "workspace_user");
        params.add("id", "1");
        List<DayMeetingResponseDto> responseDtos = getMyDayMeetingsResponseDto();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/day")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings", hasSize(2)))
                .andExpect(jsonPath("$.meetings[0].meetingId", is(responseDtos.get(0).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[0].meetingName", is(responseDtos.get(0).getMeetingName())))
                .andExpect(jsonPath("$.meetings[0].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(0).getStartDate()))))
                .andExpect(jsonPath("$.meetings[0].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getStartTime()))))
                .andExpect(jsonPath("$.meetings[0].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getEndTime()))))
                .andExpect(jsonPath("$.meetings[0].purpose", is(responseDtos.get(0).getPurpose())))
                .andExpect(jsonPath("$.meetings[0].place", is(responseDtos.get(0).getPlace())))
                .andExpect(jsonPath("$.meetings[0].workspaceId", is(responseDtos.get(0).getWorkspaceId().intValue())))
                .andExpect(jsonPath("$.meetings[0].workspaceName", is(responseDtos.get(0).getWorkspaceName())))
                .andExpect(jsonPath("$.meetings[0].workspaceLogoUrl", is(responseDtos.get(0).getWorkspaceLogoUrl())))
                .andExpect(jsonPath("$.meetings[1].meetingId", is(responseDtos.get(1).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[1].meetingName", is(responseDtos.get(1).getMeetingName())))
                .andExpect(jsonPath("$.meetings[1].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(1).getStartDate()))))
                .andExpect(jsonPath("$.meetings[1].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getStartTime()))))
                .andExpect(jsonPath("$.meetings[1].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getEndTime()))))
                .andExpect(jsonPath("$.meetings[1].purpose", is(responseDtos.get(1).getPurpose())))
                .andExpect(jsonPath("$.meetings[1].place", is(responseDtos.get(1).getPlace())))
                .andExpect(jsonPath("$.meetings[1].workspaceId", is(responseDtos.get(1).getWorkspaceId().intValue())))
                .andExpect(jsonPath("$.meetings[1].workspaceName", is(responseDtos.get(1).getWorkspaceName())))
                .andExpect(jsonPath("$.meetings[1].workspaceLogoUrl", is(responseDtos.get(1).getWorkspaceLogoUrl())))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("나의 캘린더의 경우 'workspace_user' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("userId"),
                                parameterWithName("date").description("찾을 년 월 일").attributes(field("constraints", "yyyy/M/d 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("meetings[].meetingId").type(JsonFieldType.NUMBER).description("회의 ID"),
                                fieldWithPath("meetings[].meetingName").type(JsonFieldType.STRING).description("회의 명"),
                                fieldWithPath("meetings[].startDate").type(JsonFieldType.STRING).description("회의 날짜"),
                                fieldWithPath("meetings[].startTime").type(JsonFieldType.STRING).description("회의 시작 시간"),
                                fieldWithPath("meetings[].endTime").type(JsonFieldType.STRING).description("회의 종료 시간"),
                                fieldWithPath("meetings[].purpose").type(JsonFieldType.STRING).description("회의 성격"),
                                fieldWithPath("meetings[].place").type(JsonFieldType.STRING).description("회의 장소"),
                                fieldWithPath("meetings[].workspaceId").type(JsonFieldType.NUMBER).description("워크스페이스 ID / 워크스페이스나 팀 조회 시 0으로 표기"),
                                fieldWithPath("meetings[].workspaceName").type(JsonFieldType.STRING).description("워크스페이스 명 / 워크스페이스나 팀 조회 시 \"\" 로 표기"),
                                fieldWithPath("meetings[].workspaceLogoUrl").type(JsonFieldType.STRING).description("워크스페이스 로고 URL / 워크스페이스나 팀 조회 시 \"\" 로 표기")
                        )
                ));
    }

    private List<DayMeetingResponseDto> getMyDayMeetingsResponseDto() {
        List<DayMeetingResponseDto> responseDtos = getDayMeetingsWorkspaceUser();
        when(meetingCalendarQueryUseCaseFactory.getDayMeetings(any(), any(), any()))
                .thenReturn(responseDtos);
        return responseDtos;
    }

    private List<DayMeetingResponseDto> getDayMeetingsWorkspaceUser() {
        return List.of(
                DayMeetingResponseDto.builder()
                        .meetingId(1L)
                        .startDate(LocalDate.now())
                        .startTime(LocalTime.now())
                        .endTime(LocalTime.now().plusHours(2))
                        .meetingName("테스트회의1")
                        .purpose("테스트회의성격1")
                        .place("테스트장소1")
                        .workspaceId(1L)
                        .workspaceName("첫번째 워크스페이스")
                        .workspaceLogoUrl("")
                        .build(),
                DayMeetingResponseDto.builder()
                        .meetingId(2L)
                        .startDate(LocalDate.now())
                        .startTime(LocalTime.now())
                        .endTime(LocalTime.now().plusHours(2))
                        .meetingName("테스트회의2")
                        .purpose("테스트회의성격2")
                        .place("테스트장소2")
                        .workspaceId(1L)
                        .workspaceName("첫번째 워크스페이스")
                        .workspaceLogoUrl("")
                        .build()
        );
    }

    @DisplayName("캘린더에서 선택한 날짜의 회의 조회 - 성공 / 팀의 경우")
    @Test
    void get_day_meetings_success_team() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", LocalDateTimeUtil.nowDate());
        params.add("type", "team");
        params.add("id", "1");
        List<DayMeetingResponseDto> responseDtos = getDayMeetingResponseDtos();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/day")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings", hasSize(2)))
                .andExpect(jsonPath("$.meetings[0].meetingId", is(responseDtos.get(0).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[0].meetingName", is(responseDtos.get(0).getMeetingName())))
                .andExpect(jsonPath("$.meetings[0].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(0).getStartDate()))))
                .andExpect(jsonPath("$.meetings[0].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getStartTime()))))
                .andExpect(jsonPath("$.meetings[0].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(0).getEndTime()))))
                .andExpect(jsonPath("$.meetings[0].purpose", is(responseDtos.get(0).getPurpose())))
                .andExpect(jsonPath("$.meetings[0].place", is(responseDtos.get(0).getPlace())))
                .andExpect(jsonPath("$.meetings[0].workspaceId", is(0)))
                .andExpect(jsonPath("$.meetings[0].workspaceName", emptyString()))
                .andExpect(jsonPath("$.meetings[0].workspaceLogoUrl", emptyString()))
                .andExpect(jsonPath("$.meetings[1].meetingId", is(responseDtos.get(1).getMeetingId().intValue())))
                .andExpect(jsonPath("$.meetings[1].meetingName", is(responseDtos.get(1).getMeetingName())))
                .andExpect(jsonPath("$.meetings[1].startDate", is(LocalDateTimeUtil.convertDate(responseDtos.get(1).getStartDate()))))
                .andExpect(jsonPath("$.meetings[1].startTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getStartTime()))))
                .andExpect(jsonPath("$.meetings[1].endTime", is(LocalDateTimeUtil.convertTime(responseDtos.get(1).getEndTime()))))
                .andExpect(jsonPath("$.meetings[1].purpose", is(responseDtos.get(1).getPurpose())))
                .andExpect(jsonPath("$.meetings[1].place", is(responseDtos.get(1).getPlace())))
                .andExpect(jsonPath("$.meetings[1].workspaceId", is(0)))
                .andExpect(jsonPath("$.meetings[1].workspaceName", emptyString()))
                .andExpect(jsonPath("$.meetings[1].workspaceLogoUrl", emptyString()))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("팀 캘린더의 경우 'team' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("팀 ID"),
                                parameterWithName("date").description("찾을 년 월").attributes(field("constraints", "yyyy/M/d 형식으로 입력"))
                        ),
                        responseFields(
                                fieldWithPath("meetings[].meetingId").type(JsonFieldType.NUMBER).description("회의 ID"),
                                fieldWithPath("meetings[].meetingName").type(JsonFieldType.STRING).description("회의 명"),
                                fieldWithPath("meetings[].startDate").type(JsonFieldType.STRING).description("회의 날짜"),
                                fieldWithPath("meetings[].startTime").type(JsonFieldType.STRING).description("회의 시작 시간"),
                                fieldWithPath("meetings[].endTime").type(JsonFieldType.STRING).description("회의 종료 시간"),
                                fieldWithPath("meetings[].purpose").type(JsonFieldType.STRING).description("회의 성격"),
                                fieldWithPath("meetings[].place").type(JsonFieldType.STRING).description("회의 장소"),
                                fieldWithPath("meetings[].workspaceId").type(JsonFieldType.NUMBER).description("워크스페이스 ID / 워크스페이스나 팀 조회 시 0으로 표기"),
                                fieldWithPath("meetings[].workspaceName").type(JsonFieldType.STRING).description("워크스페이스 명 / 워크스페이스나 팀 조회 시 \"\" 로 표기"),
                                fieldWithPath("meetings[].workspaceLogoUrl").type(JsonFieldType.STRING).description("워크스페이스 로고 URL / 워크스페이스나 팀 조회 시 \"\" 로 표기")
                        )
                ));
    }

    @DisplayName("캘린더에서 년도별 회의 갯수 조회 - 성공 / 워크스페이스 캘린더의 경우")
    @Test
    void get_year_meetings_count_success_workspace() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "workspace");
        params.add("id", "1");
        List<YearMeetingsCountResponseDto> responseDtos = meetingCountPerYearStubAndReturn();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/years")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearCounts", hasSize(2)))
                .andExpect(jsonPath("$.yearCounts[0].year", is(responseDtos.get(0).getYear())))
                .andExpect(jsonPath("$.yearCounts[0].count", is(responseDtos.get(0).getCount().intValue())))
                .andExpect(jsonPath("$.yearCounts[1].year", is(responseDtos.get(1).getYear())))
                .andExpect(jsonPath("$.yearCounts[1].count", is(responseDtos.get(1).getCount().intValue())))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("워크스페이스의 경우 'workspace' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("워크스페이스 ID")
                        ),
                        responseFields(
                                fieldWithPath("yearCounts[].year").type(JsonFieldType.NUMBER).description("회의가 존재하는 년도"),
                                fieldWithPath("yearCounts[].count").type(JsonFieldType.NUMBER).description("해당 년도의 회의 갯수")
                        )
                ));
    }

    private List<YearMeetingsCountResponseDto> meetingCountPerYearStubAndReturn() {
        List<YearMeetingsCountResponseDto> responseDtos = createYearMeetingsCountResposeDtos();
        when(meetingCalendarQueryUseCaseFactory.getMeetingCountPerYear(any(), any()))
                .thenReturn(responseDtos);
        return responseDtos;
    }

    private List<YearMeetingsCountResponseDto> createYearMeetingsCountResposeDtos() {
        return List.of(
                YearMeetingsCountResponseDto.builder().year(2022).count(12L).build(),
                YearMeetingsCountResponseDto.builder().year(2021).count(13L).build()
        );
    }

    @DisplayName("캘린더에서 년도별 회의 갯수 조회 - 성공 / 나의 미론, 내 캘린더의 경우")
    @Test
    void get_year_meetings_count_success_workspace_user() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "workspace_user");
        params.add("id", "1");
        List<YearMeetingsCountResponseDto> responseDtos = meetingCountPerYearStubAndReturn();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/years")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearCounts", hasSize(2)))
                .andExpect(jsonPath("$.yearCounts[0].year", is(responseDtos.get(0).getYear())))
                .andExpect(jsonPath("$.yearCounts[0].count", is(responseDtos.get(0).getCount().intValue())))
                .andExpect(jsonPath("$.yearCounts[1].year", is(responseDtos.get(1).getYear())))
                .andExpect(jsonPath("$.yearCounts[1].count", is(responseDtos.get(1).getCount().intValue())))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("나의 캘린더의 경우 'workspace_user' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("userId")
                        ),
                        responseFields(
                                fieldWithPath("yearCounts[].year").type(JsonFieldType.NUMBER).description("회의가 존재하는 년도"),
                                fieldWithPath("yearCounts[].count").type(JsonFieldType.NUMBER).description("해당 년도의 회의 갯수")
                        )
                ));
    }

    @DisplayName("캘린더에서 년도별 회의 갯수 조회 - 성공 / 팀 캘린더의 경우")
    @Test
    void get_year_meetings_count_success_team() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "team");
        params.add("id", "1");
        List<YearMeetingsCountResponseDto> responseDtos = meetingCountPerYearStubAndReturn();

        // when, then, docs

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/years")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearCounts", hasSize(2)))
                .andExpect(jsonPath("$.yearCounts[0].year", is(responseDtos.get(0).getYear())))
                .andExpect(jsonPath("$.yearCounts[0].count", is(responseDtos.get(0).getCount().intValue())))
                .andExpect(jsonPath("$.yearCounts[1].year", is(responseDtos.get(1).getYear())))
                .andExpect(jsonPath("$.yearCounts[1].count", is(responseDtos.get(1).getCount().intValue())))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("팀 캘린더의 경우 'team' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("yearCounts[].year").type(JsonFieldType.NUMBER).description("회의가 존재하는 년도"),
                                fieldWithPath("yearCounts[].count").type(JsonFieldType.NUMBER).description("해당 년도의 회의 갯수")
                        )
                ));
    }

    @DisplayName("캘린더에서 월별 회의 갯수 조회 - 성공 / 워크스페이스 캘린더의 경우")
    @Test
    void get_month_meetings_count_success_workspace() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "workspace");
        params.add("id", "1");
        params.add("year", "2022");
        countPerMonthStub();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/months")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthCounts", hasSize(12)))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("워크스페이스의 경우 'workspace' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("워크스페이스 ID"),
                                parameterWithName("year").description("찾을 년도")
                        ),
                        responseFields(
                                fieldWithPath("monthCounts[].month").type(JsonFieldType.NUMBER).description("선택한 년도의 월"),
                                fieldWithPath("monthCounts[].count").type(JsonFieldType.NUMBER).description("해당 월의 회의 갯수")
                        )
                ));
    }

    private void countPerMonthStub() {
        List<MonthMeetingsCountResponseDto> responseDtos = createMonthMeetingsCountResposeDtos();
        when(meetingCalendarQueryUseCaseFactory.getMeetingCountPerMonth(any(), any(), any()))
                .thenReturn(responseDtos);
    }

    private List<MonthMeetingsCountResponseDto> createMonthMeetingsCountResposeDtos() {
        return List.of(
                MonthMeetingsCountResponseDto.builder().month(1).count(17L).build(),
                MonthMeetingsCountResponseDto.builder().month(2).count(12L).build(),
                MonthMeetingsCountResponseDto.builder().month(3).count(16L).build(),
                MonthMeetingsCountResponseDto.builder().month(4).count(29L).build(),
                MonthMeetingsCountResponseDto.builder().month(5).count(3L).build(),
                MonthMeetingsCountResponseDto.builder().month(6).count(0L).build(),
                MonthMeetingsCountResponseDto.builder().month(7).count(0L).build(),
                MonthMeetingsCountResponseDto.builder().month(8).count(0L).build(),
                MonthMeetingsCountResponseDto.builder().month(9).count(0L).build(),
                MonthMeetingsCountResponseDto.builder().month(10).count(1L).build(),
                MonthMeetingsCountResponseDto.builder().month(11).count(12L).build(),
                MonthMeetingsCountResponseDto.builder().month(12).count(25L).build()
        );
    }

    @DisplayName("캘린더에서 년도별 회의 갯수 조회 - 성공 / 나의 미론, 내 캘린더의 경우")
    @Test
    void get_month_meetings_count_success_workspace_user() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "workspace_user");
        params.add("id", "1");
        params.add("year", "2022");
        countPerMonthStub();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/months")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthCounts", hasSize(12)))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("나의 캘린더의 경우 'workspace_user' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("userId"),
                                parameterWithName("year").description("찾을 년도")
                        ),
                        responseFields(
                                fieldWithPath("monthCounts[].month").type(JsonFieldType.NUMBER).description("선택한 년도의 월"),
                                fieldWithPath("monthCounts[].count").type(JsonFieldType.NUMBER).description("해당 월의 회의 갯수")
                        )
                ));
    }

    @DisplayName("캘린더에서 년도별 회의 갯수 조회 - 성공 / 팀 캘린더의 경우")
    @Test
    void get_month_meetings_count_success_team() throws Exception {

        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "team");
        params.add("id", "1");
        params.add("year", "2022");
        countPerMonthStub();

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/months")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthCounts", hasSize(12)))
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("type").description("팀 캘린더의 경우 'team' 입력").attributes(field("constraints", "해당 파라미터는 workspace, workspace_user, team 중 하나")),
                                parameterWithName("id").description("팀 ID"),
                                parameterWithName("year").description("찾을 년도")
                        ),
                        responseFields(
                                fieldWithPath("monthCounts[].month").type(JsonFieldType.NUMBER).description("회의가 존재하는 년도"),
                                fieldWithPath("monthCounts[].count").type(JsonFieldType.NUMBER).description("해당 년도의 회의 갯수")
                        )
                ));
    }

    @DisplayName("회의 상세 정보 조회 - 실패 / 회의가 존재하지 않을 경우")
    @Test
    void get_meeting_fail_not_found_meeting() throws Exception {

        // given
        when(meetingQueryUseCase.getMeeting(any()))
                .thenThrow(new MeetingNotFoundException());

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/{meetingId}", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.code", is(MeetingErrorCode.NOT_FOUND_MEETING.getCode())));
    }

    @DisplayName("회의 상세 정보 조회 - 성공")
    @Test
    void get_meeting_success() throws Exception {

        // given
        MeetingResponseDto responseDto = MeetingResponseDtoBuilder.build();
        when(meetingQueryUseCase.getMeeting(any()))
                .thenReturn(responseDto);

        // when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/meetings/{meetingId}", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer TestAccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("meetingId").description("조회할 회의 ID")
                        ),
                        responseFields(
                                fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("회의가 존재하는 년도"),
                                fieldWithPath("meetingName").type(JsonFieldType.STRING).description("해당 년도의 회의 갯수"),
                                fieldWithPath("meetingPurpose").type(JsonFieldType.STRING).description("회의 성격"),
                                fieldWithPath("meetingDate").type(JsonFieldType.STRING).description("회의 날짜"),
                                fieldWithPath("startTime").type(JsonFieldType.STRING).description("회의 시작 시간"),
                                fieldWithPath("endTime").type(JsonFieldType.STRING).description("회의 종료 시간"),
                                fieldWithPath("operationTeamId").type(JsonFieldType.NUMBER).description("주관하는 팀 ID"),
                                fieldWithPath("operationTeamName").type(JsonFieldType.STRING).description("주관하는 팀 이름"),
                                fieldWithPath("admins[].workspaceUserId").type(JsonFieldType.NUMBER).description("회의 관리자 워크스페이스 유저 ID"),
                                fieldWithPath("admins[].nickname").type(JsonFieldType.STRING).description("회의 관리자 워크스페이스 유저 닉네임")
                        )
                ));
    }
}
