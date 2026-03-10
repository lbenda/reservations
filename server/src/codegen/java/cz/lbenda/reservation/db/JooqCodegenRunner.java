package cz.lbenda.reservation.db;

import org.flywaydb.core.Flyway;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Paths;
import java.time.Duration;

public final class JooqCodegenRunner {
    private JooqCodegenRunner() {
    }

    public static void main(String[] args) {
        Settings external = readExternalSettings();
        PostgreSQLContainer<?> container = null;

        if (external == null) {
            container = new PostgreSQLContainer<>("postgres:16-alpine");
            container.withDatabaseName("reservation_codegen");
            container.withUsername("reservation");
            container.withPassword("reservation");
            container.withStartupTimeout(Duration.ofSeconds(60));
            container.start();
        }

        try {
            Settings settings = external != null ? external : new Settings(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
            );

            Flyway.configure()
                .dataSource(settings.url, settings.user, settings.password)
                .locations("classpath:db/migration")
                .load()
                .migrate();

            String outputDir = Paths.get("build", "generated-src", "jooq").toString();

            Configuration configuration = new Configuration()
                .withJdbc(new Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl(settings.url)
                    .withUser(settings.user)
                    .withPassword(settings.password)
                )
                .withGenerator(new Generator()
                    .withName("org.jooq.codegen.KotlinGenerator")
                    .withDatabase(new Database()
                        .withName("org.jooq.meta.postgres.PostgresDatabase")
                        .withInputSchema("public")
                    )
                    .withGenerate(new Generate()
                        .withRecords(false)
                        .withPojos(false)
                        .withDaos(false)
                        .withImmutablePojos(false)
                        .withFluentSetters(false)
                    )
                    .withTarget(new Target()
                        .withPackageName("cz.lbenda.reservation.jooq")
                        .withDirectory(outputDir)
                    )
                );

            try {
                GenerationTool.generate(configuration);
            } catch (Exception ex) {
                throw new RuntimeException("jOOQ code generation failed", ex);
            }
        } finally {
            if (container != null) {
                container.stop();
            }
        }
    }

    private static Settings readExternalSettings() {
        String url = System.getenv("JOOQ_DB_URL");
        String user = System.getenv("JOOQ_DB_USER");
        String password = System.getenv("JOOQ_DB_PASS");

        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        return new Settings(url, user, password);
    }

    private static final class Settings {
        private final String url;
        private final String user;
        private final String password;

        private Settings(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }
}
