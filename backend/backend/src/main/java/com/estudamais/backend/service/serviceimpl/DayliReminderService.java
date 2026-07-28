package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.SheduleRepository;
import com.estudamais.backend.repository.UserRepository;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.service.DailyReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DayliReminderService implements DailyReminderService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SheduleRepository sheduleRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Scheduled(cron = "0 0 7 * * *")
    public void sendMorningDigest() {
        List<User> allUsers = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : allUsers) {
            List<Schedule> todaySchedule = sheduleRepository.findByUserIdAndTargetDate(user.getId(), today);
            if (todaySchedule.isEmpty()) {
                sendEmail(user, todaySchedule, "Nenhum agendamento para hoje");
            } else {
                sendEmail(user, todaySchedule, "Seu dia de estudos");
            }

        }
    }
    @Override
    public void sendReminderIfNothingDone() {
        List<User> allUsers = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for(User user : allUsers){
            List<Schedule> todaySchedules = sheduleRepository.findByUserIdAndTargetDate(user.getId(),today);

         boolean nothingCompleted =  todaySchedules.stream().noneMatch(s -> s.isCompleted());
            if (nothingCompleted) {
                sendEmail(user, todaySchedules, "Ainda dá tempo hoje");
            }
        }
    }


    @Override
    @Scheduled(cron = "0 0 12 * * *")
    public void sendNoonReminder() {
        sendReminderIfNothingDone();
    }

    @Override
    @Scheduled(cron = "0 0 18 * * *")
    public void sendEveningReminder() {
    sendReminderIfNothingDone();
    }

    @Override
    public void sendEmail(User user, List<Schedule> schedules, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);

        StringBuilder body = new StringBuilder();
        body.append("Olá, ").append(user.getName()).append("!\n\n");
        for (Schedule s : schedules) {
            body.append("- ").append(s.getTitle());

            if (s.getStartTime() != null && s.getEndTime() != null) {
                body.append(" (").append(s.getStartTime()).append(" às ").append(s.getEndTime()).append(")");
            }

            body.append("\n");
        }
        message.setText(body.toString());
        javaMailSender.send(message);
    }
}
