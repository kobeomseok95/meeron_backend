package com.cmc.meeron.meeting.adapter.out;

import com.cmc.meeron.meeting.application.port.out.MeetingCommandPort;
import com.cmc.meeron.meeting.domain.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class MeetingCommandRepository implements MeetingCommandPort {

    private final MeetingJpaRepository meetingJpaRepository;

    @Override
    public Long save(Meeting meeting) {
        return meetingJpaRepository.save(meeting)
                .getId();
    }
}
