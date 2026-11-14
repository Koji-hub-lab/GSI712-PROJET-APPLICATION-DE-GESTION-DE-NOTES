package StudentManagementSystem.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Normalize user table name and FKs to avoid legacy mismatch errors
        exec("SET FOREIGN_KEY_CHECKS=0");
        exec("RENAME TABLE `user` TO `users`");
        exec("ALTER TABLE teacher DROP FOREIGN KEY FKpb6g6pahj1mr2ijg92r7m1xlh");
        exec("ALTER TABLE teacher DROP FOREIGN KEY FKcp1vpkh4bh0qux9vtvs0fkwrn");
        exec("ALTER TABLE teacher ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id)");
        exec("ALTER TABLE student DROP FOREIGN KEY FKk0thg920a3xk3v59yjbsatw1l");
        exec("ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)");
        exec("SET FOREIGN_KEY_CHECKS=1");

        removeLegacyEnrollmentStudentForeignKeys();

        // Ensure enrollment.status accepts enum names (ENROLLED/COMPLETED/DROPPED).
        // Some legacy schemas still keep numeric/enum definitions that truncate strings.
        ensureEnrollmentStatusColumnCompatible();
    }

    private void exec(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ignored) {
            // ignore if already applied
        }
    }

    private void ensureEnrollmentStatusColumnCompatible() {
        try {
            java.util.Map<String, Object> column = jdbcTemplate.query(
                    "SELECT DATA_TYPE AS dataType, CHARACTER_MAXIMUM_LENGTH AS maxLen FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'enrollment' " +
                            "AND COLUMN_NAME = 'status'",
                    rs -> rs.next()
                            ? java.util.Map.of(
                            "dataType", rs.getString("dataType"),
                            "maxLen", rs.getObject("maxLen"))
                            : null
            );

            if (column == null) {
                return;
            }

            String dataType = String.valueOf(column.get("dataType"));
            Object maxLenObj = column.get("maxLen");
            Long maxLen = maxLenObj instanceof Number ? ((Number) maxLenObj).longValue() : null;
            boolean needsAlter = !"varchar".equalsIgnoreCase(dataType) || maxLen == null || maxLen < 20;

            if (!needsAlter) {
                return;
            }

            log.warn("Legacy enrollment.status definition detected (type={}, maxLen={}). Converting to VARCHAR(20).",
                    dataType, maxLen);
            jdbcTemplate.execute("ALTER TABLE enrollment MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED'");
            jdbcTemplate.execute(
                    "UPDATE enrollment SET status = CASE " +
                            "WHEN status IS NULL OR TRIM(status) = '' OR status = '0' THEN 'ENROLLED' " +
                            "WHEN status = '1' THEN 'COMPLETED' " +
                            "WHEN status = '2' THEN 'DROPPED' " +
                            "ELSE status END"
            );
        } catch (Exception e) {
            log.warn("Could not normalize enrollment.status column automatically: {}", e.getMessage());
        }
    }

    private void removeLegacyEnrollmentStudentForeignKeys() {
        try {
            var badFks = jdbcTemplate.queryForList(
                    "SELECT DISTINCT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'enrollment' " +
                            "AND REFERENCED_TABLE_NAME = 'students'",
                    String.class
            );

            for (String fkName : badFks) {
                jdbcTemplate.execute("ALTER TABLE enrollment DROP FOREIGN KEY `" + fkName + "`");
                log.warn("Dropped legacy enrollment FK pointing to students: {}", fkName);
            }
        } catch (Exception e) {
            log.warn("Could not remove legacy enrollment foreign keys: {}", e.getMessage());
        }
    }
}
