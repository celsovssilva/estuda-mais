package com.estudamais.backend.service;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.User;

import java.util.List;

public interface DailyReminderService {
    void sendMorningDigest();
    void sendReminderIfNothingDone();
    void sendNoonReminder();
    void sendEveningReminder();
    void sendEmail(User user, List<Schedule> schedules, String subject);

}
