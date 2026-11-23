package StudentManagementSystem.service;

import StudentManagementSystem.dto.NotificationDTO;
import StudentManagementSystem.entity.Notification;
import StudentManagementSystem.entity.NotificationType;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.User;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.NotificationRepository;
import StudentManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CourseStudentRepository courseStudentRepository;

    @Transactional
    public NotificationDTO createForUser(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : NotificationType.GENERAL);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void createForCourseStudents(Long courseId, String title, String message, NotificationType type) {
        List<Student> students = courseStudentRepository.findByCourse_Id(courseId).stream()
                .map(link -> link.getStudent())
                .filter(student -> student != null && student.getUser() != null && student.getUser().getId() != null)
                .toList();
        for (Student student : students) {
            createForUser(student.getUser().getId(), title, message, type);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getByUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public NotificationDTO markRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to this user.");
        }
        notification.setIsRead(true);
        return toDto(notificationRepository.save(notification));
    }

    private NotificationDTO toDto(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getCreatedAt(),
                notification.getIsRead()
        );
    }
}

