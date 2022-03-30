insert into meeting(meeting_id, workspace_id, team_id, name, purpose, start_date, start_time, end_time, place, created_date, last_modified_date)
values(6, 1, 1, '테스트미팅', '공지사항', current_date, '14:00', '16:00', '구글 밋', current_timestamp, current_timestamp);
insert into attendee(attendee_id, meeting_id, workspace_user_id, attend_status, created_date, last_modified_date, is_meeting_admin)
values(18, 6, 1, 'UNKNOWN', current_timestamp, current_timestamp, 0);
insert into attendee(attendee_id, meeting_id, workspace_user_id, attend_status, created_date, last_modified_date, is_meeting_admin)
values(19, 6, 2, 'UNKNOWN', current_timestamp, current_timestamp, 1);
insert into attendee(attendee_id, meeting_id, workspace_user_id, attend_status, created_date, last_modified_date, is_meeting_admin)
values(20, 6, 3, 'ATTEND', current_timestamp, current_timestamp, 0);
insert into attendee(attendee_id, meeting_id, workspace_user_id, attend_status, created_date, last_modified_date, is_meeting_admin)
values(21, 6, 4, 'ABSENT', current_timestamp, current_timestamp, 0);
insert into attendee(attendee_id, meeting_id, workspace_user_id, attend_status, created_date, last_modified_date, is_meeting_admin)
values(22, 6, 5, 'ATTEND', current_timestamp, current_timestamp, 1);