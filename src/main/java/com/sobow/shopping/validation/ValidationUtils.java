package com.sobow.shopping.validation;

import com.sobow.shopping.config.MoneyConfig;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;

public class ValidationUtils {
    
    public static final String POLAND_POST_CODE_REGEX = "^\\d{2}-\\d{3}$";
    
    /**
     * Normalizes single-line user input in a null-safe way.
     * <p>
     * If {@code input} is {@code null}, the method simply returns {@code null}
     *
     * <br><br>
     * Otherwise it:
     * <ol>
     *   <li>Performs Unicode normalization via {@link java.text.Normalizer.Form#NFKC}
     *       to fold compatibility characters and unify look-alikes.</li>
     *   <li>Strips leading and trailing Unicode whitespace
     *       (via {@link String#strip()}).</li>
     *   <li>Collapses any internal whitespace runs to a single ASCII space:
     *       {@code s.replaceAll("\\s+", " ")}.</li>
     * </ol>
     * Use for fields like names or titles where multiple spaces and exotic
     * whitespace shouldn’t be preserved.
     *
     * @param input raw user input; may be {@code null}
     * @return normalized string, or {@code null} if {@code input} was {@code null}
     * @see java.text.Normalizer
     * @see java.text.Normalizer.Form#NFKC
     */
    public static String normalizeSingleLine(String input) {
        if (input == null) return null;
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return s.strip().replaceAll("\\s+", " ");
    }
    
    /**
     * Normalizes multi-line user input while preserving line breaks, in a null-safe way.
     * <p>
     * If {@code input} is {@code null}, returns {@code null}.
     * <br><br>
     * Otherwise it:
     * <ol>
     *   <li>Performs Unicode normalization via {@link java.text.Normalizer.Form#NFKC}.</li>
     *   <li>Normalizes Windows line endings to LF by converting CRLF to LF:
     *       {@code s.replace("\r\n", "\n")} (standalone {@code \r} are left intact).</li>
     *   <li>Strips leading and trailing Unicode whitespace
     *       (via {@link String#strip()}); internal whitespace and newlines are preserved.</li>
     * </ol>
     * Use for descriptions and text areas where you want consistent encoding and line
     * endings but do not want to collapse internal spaces.
     *
     * @param input raw user input; may be {@code null}
     * @return normalized string (may contain {@code \n}), or {@code null} if {@code input} was {@code null}
     * @see java.text.Normalizer
     * @see java.text.Normalizer.Form#NFKC
     */
    public static String normalizeMultiLine(String input) {
        if (input == null) return null;
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return s.replace("\r\n", "\n").strip();
    }
    
    /**
     * Normalizes a monetary amount to the application’s canonical scale.
     * <p>
     * If {@code input} is {@code null}, returns {@code null}. Otherwise this method calls
     * {@link java.math.BigDecimal#setScale(int, java.math.RoundingMode)} using {@code MoneyConfig.SCALE} and
     * {@code MoneyConfig.ROUNDING}.
     *
     * @param input raw amount; may be {@code null}
     * @return {@code null} if input is {@code null}; otherwise a new {@link BigDecimal} with scale {@code MoneyConfig.SCALE} and
     * rounding {@code MoneyConfig.ROUNDING}
     * @see MoneyConfig#SCALE
     * @see MoneyConfig#ROUNDING
     */
    public static BigDecimal normalizePrice(BigDecimal input) {
        if (input == null) return null;
        return input.setScale(MoneyConfig.SCALE, MoneyConfig.ROUNDING);
    }
    
    /**
     * Normalizes an email string in a null-safe way for consistent storage/lookup.
     * <p>
     * If {@code input} is {@code null}, returns {@code null}. Otherwise it:
     * <ol>
     *   <li>Strips leading/trailing Unicode whitespace via {@link String#strip()}.</li>
     *   <li>Lowercases using {@link java.util.Locale#ROOT}.</li>
     * </ol>
     * <b>Note:</b> This method does not alter internal whitespace (emails should not
     * contain it) nor convert internationalized domains to ASCII (Punycode). Use
     * bean validation (e.g. {@code @Email}, custom checks) to reject invalid formats,
     * or perform additional normalization if you accept IDNs.
     *
     * @param input raw email; may be {@code null}
     * @return normalized email (trimmed + lowercased), or {@code null} if input was {@code null}
     * @see java.util.Locale#ROOT
     */
    public static String normalizeEmail(String input) {
        if (input == null) return null;
        return input.strip().toLowerCase(Locale.ROOT);
    }
    
    /**
     * Normalizes an authority/role name into a plain, canonical form (without the {@code ROLE_} prefix).
     * <p>
     * If {@code input} is {@code null}, returns {@code null}. Otherwise it:
     * <ol>
     *   <li>Strips leading/trailing Unicode whitespace via {@link String#strip()}.</li>
     *   <li>Uppercases using {@link java.util.Locale#ROOT}.</li>
     *   <li>Removes a leading {@code ROLE_} prefix if present.</li>
     * </ol>
     * This is useful when your entity/service consistently prepends {@code ROLE_}
     * at persistence time, while clients may send either {@code ADMIN} or
     * {@code ROLE_ADMIN}. The returned value is the plain role (e.g. {@code ADMIN}).
     *
     * @param input raw role/authority; may be {@code null}
     * @return plain, uppercased role without {@code ROLE_} (e.g. {@code ADMIN}), or {@code null} if input was {@code null}
     * @see java.util.Locale#ROOT
     */
    public static String normalizeAuthority(String input) {
        if (input == null) return null;
        input.strip().toUpperCase(Locale.ROOT);
        
        if (input.startsWith("ROLE_")) {
            input = input.substring(5);
        }
        return input;
    }
}
