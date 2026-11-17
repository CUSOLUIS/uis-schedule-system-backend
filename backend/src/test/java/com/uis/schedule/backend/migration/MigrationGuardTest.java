package com.uis.schedule.backend.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guardia del pipeline: los datos de prueba solo pueden entrar por db/migration-dev.
 *
 * Las pruebas usan H2 con Flyway desactivado, así que nada más en el CI revisa las
 * migraciones. Estas comprobaciones leen los archivos tal como se empaquetan.
 */
class MigrationGuardTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final String COMMON = "db/migration";
    private static final String PROD = "db/migration-prod";
    private static final String DEV = "db/migration-dev";

    /** V1–V10 son anteriores a la separación y no se reescriben; la regla rige desde V11. */
    private static final int FIRST_GUARDED_VERSION = 11;

    /** Catálogos que cualquier entorno necesita. Una tabla nueva de este tipo se agrega aquí a propósito. */
    private static final Set<String> CATALOG_TABLES = Set.of("roles", "day_of_week");

    private static final Pattern VERSIONED = Pattern.compile("^V(\\d+(?:[._]\\d+)*)__.+\\.sql$");
    private static final Pattern INSERT = Pattern.compile(
            "\\binsert\\s+into\\s+(?:\"?public\"?\\.)?\"?(\\w+)\"?", Pattern.CASE_INSENSITIVE);
    private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENT = Pattern.compile("--[^\\n]*");

    @Test
    void commonAndProdMigrationsDoNotInsertTestData() throws IOException {
        List<String> violations = new ArrayList<>();
        for (String location : List.of(COMMON, PROD)) {
            for (Path file : migrations(location)) {
                if (majorVersion(file) < FIRST_GUARDED_VERSION) {
                    continue;
                }
                for (String table : insertedTables(Files.readString(file, StandardCharsets.UTF_8))) {
                    if (!CATALOG_TABLES.contains(table)) {
                        violations.add(location + "/" + file.getFileName() + " inserta en " + table);
                    }
                }
            }
        }
        assertTrue(violations.isEmpty(),
                "Los datos de prueba van en " + DEV + ", no en las migraciones comunes ni de prod: " + violations);
    }

    @Test
    void insertDetectionIgnoresCommentsAndQuotes() {
        String sql = """
                -- INSERT INTO public.users (comentario, no cuenta)
                /* INSERT INTO teacher VALUES (1); */
                INSERT INTO public.roles (name) VALUES ('X');
                insert   into "public"."classroom" (number) values ('101');
                """;
        assertEquals(List.of("roles", "classroom"), insertedTables(sql));
    }

    @Test
    void defaultAndProdProfilesNeverLoadDevData() throws IOException {
        for (String file : List.of("application.properties", "application-prod.properties")) {
            List<String> locations = flywayLocations(file);
            assertFalse(locations.stream().anyMatch(l -> l.contains(DEV)), file + " no puede cargar " + DEV);
            assertTrue(locations.contains("classpath:" + PROD), file + " debe aplicar " + PROD);
        }
    }

    @Test
    void devProfileLoadsDevDataInsteadOfProdCleanup() throws IOException {
        List<String> locations = flywayLocations("application-dev.properties");
        assertEquals(List.of("classpath:" + COMMON, "classpath:" + DEV), locations);
    }

    @Test
    void versionsAreUniqueAcrossLocations() throws IOException {
        Map<String, List<String>> byVersion = new HashMap<>();
        for (String location : List.of(COMMON, PROD, DEV)) {
            for (Path file : migrations(location)) {
                byVersion.computeIfAbsent(version(file), v -> new ArrayList<>())
                        .add(location + "/" + file.getFileName());
            }
        }
        List<List<String>> duplicated = byVersion.values().stream().filter(files -> files.size() > 1).toList();
        assertTrue(duplicated.isEmpty(), "Versiones de Flyway repetidas entre carpetas: " + duplicated);
    }

    private static List<Path> migrations(String location) throws IOException {
        Path dir = RESOURCES.resolve(location);
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(f -> VERSIONED.matcher(f.getFileName().toString()).matches()).sorted().toList();
        }
    }

    private static String version(Path file) {
        Matcher m = VERSIONED.matcher(file.getFileName().toString());
        if (!m.matches()) {
            throw new IllegalArgumentException("No es una migración versionada: " + file);
        }
        // Flyway trata "2_1" y "2.1" como la misma versión, y "1" igual que "1.0".
        String[] parts = m.group(1).split("[._]");
        int end = parts.length;
        while (end > 1 && Integer.parseInt(parts[end - 1]) == 0) {
            end--;
        }
        return String.join(".", Arrays.stream(parts, 0, end).map(p -> String.valueOf(Integer.parseInt(p))).toList());
    }

    private static int majorVersion(Path file) {
        return Integer.parseInt(version(file).split("\\.")[0]);
    }

    private static List<String> insertedTables(String sql) {
        String code = LINE_COMMENT.matcher(BLOCK_COMMENT.matcher(sql).replaceAll(" ")).replaceAll(" ");
        List<String> tables = new ArrayList<>();
        Matcher insert = INSERT.matcher(code);
        while (insert.find()) {
            tables.add(insert.group(1).toLowerCase());
        }
        return tables;
    }

    private static List<String> flywayLocations(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(RESOURCES.resolve(propertiesFile), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        String value = properties.getProperty("spring.flyway.locations", "");
        return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
