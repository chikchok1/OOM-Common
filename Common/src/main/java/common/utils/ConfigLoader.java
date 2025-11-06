package common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties 파일을 찾을 수 없습니다. " +
                        "src/main/resources 디렉토리에 config.properties 파일이 존재하는지 확인하세요.");
            }
            properties.load(input);
            isLoaded = true;
        } catch (IOException ex) {
            throw new RuntimeException("설정 파일 로딩 중 오류가 발생했습니다.", ex);
        }
    }

    /**
     * 주어진 키에 대한 설정값을 반환합니다.
     *
     * @param key 설정 키
     * @return 설정값, 해당 키가 없으면 null 반환
     * @throws IllegalArgumentException key가 null이거나 공백인 경우
     */
    public static String getProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key는 null이거나 공백일 수 없습니다.");
        }
        return properties.getProperty(key);
    }

    /**
     * 주어진 키에 대한 설정값을 반환하며, 없으면 기본값을 반환합니다.
     *
     * @param key 설정 키
     * @param defaultValue 기본값
     * @return 설정값 또는 기본값
     * @throws IllegalArgumentException key가 null이거나 공백인 경우
     */
    public static String getProperty(String key, String defaultValue) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key는 null이거나 공백일 수 없습니다.");
        }
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 설정이 정상적으로 로드되었는지 확인합니다.
     *
     * @return 설정 로드 성공 여부
     */
    public static boolean isLoaded() {
        return isLoaded;
    }

    /**
     * 특정 키가 설정에 존재하는지 확인합니다.
     *
     * @param key 설정 키
     * @return 키 존재 여부
     */
    public static boolean contains(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key는 null이거나 공백일 수 없습니다.");
        }
        return properties.containsKey(key);
    }
}
