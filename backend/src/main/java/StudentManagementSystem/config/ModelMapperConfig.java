package StudentManagementSystem.config;

import StudentManagementSystem.dto.CourseDTO;
import StudentManagementSystem.dto.GradeDTO;
import StudentManagementSystem.dto.SubjectDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Grade;
import StudentManagementSystem.entity.Subject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
//
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);

//        // Course Mapping
//        mapper.typeMap(Course.class, CourseDTO.class)
//                .addMappings(m -> m.map(
//                        src -> src.getTeacher() != null ? src.getTeacher().getUsername() : null,
//                        CourseDTO::setTeacherName
//                ));
//
//        // Subject Mapping
//        mapper.typeMap(Subject.class, SubjectDTO.class)
//                .addMappings(m -> m.map(
//                        src -> src.getTeacher() != null ? src.getTeacher().getUsername() : null,
//                        SubjectDTO::setTeacherName
//                ));
//
//        // Grade Mapping
//        mapper.typeMap(Grade.class, GradeDTO.class)
//                .addMappings(m -> {
//                    m.map(
//                            src -> src.getStudent() != null && src.getStudent().getUser() != null
//                                    ? src.getStudent().getUser().getUsername()
//                                    : null,
//                            GradeDTO::setStudentName
//                    );
//
//                    m.map(
//                            src -> src.getSubject() != null ? src.getSubject().getName() : null,
//                            GradeDTO::setSubjectName
//                    );
//                });

        return mapper;
    }
}
