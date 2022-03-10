package com.cmc.meeron.team.integration;

import com.cmc.meeron.support.IntegrationTest;
import com.cmc.meeron.support.security.WithMockJwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockJwt
public class TeamIntegrationTest extends IntegrationTest {

    @DisplayName("워크스페이스 내 팀 조회 - 성공")
    @ParameterizedTest
    @MethodSource("workspaceTeamsParameters")
    void get_workspace_teams_success(MultiValueMap<String, String> params, int expectedValue) throws Exception {

        // given, when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams", hasSize(expectedValue)));
    }

    private static Stream<Arguments> workspaceTeamsParameters() {
        MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
        params1.add("workspaceId", "1");
        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add("workspaceId", "2");
        return Stream.of(
                Arguments.of(params1, 3),
                Arguments.of(params2, 0)
        );
    }
}