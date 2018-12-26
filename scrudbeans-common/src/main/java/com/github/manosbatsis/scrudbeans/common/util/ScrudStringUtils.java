package com.github.manosbatsis.scrudbeans.common.util;

import java.util.regex.Pattern;

public class ScrudStringUtils {

	public static final String SPACE = " ";


	// uncamelise: Contains  bits from humanize project Copyright 2013-2015 mfornos, ASL license,
	// see https://github.com/mfornos/humanize
	public static final Pattern PATTERN_SPLIT_CAMELCASE = Pattern
			.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

	/**
	 * <p>
	 * Converts a camel case string into a human-readable name.
	 * </p>
	 *
	 * <p>
	 * Example assuming SPACE as replacement:
	 * </p>
	 *
	 * <table border="0" cellspacing="0" cellpadding="3" width="100%">
	 * <tr>
	 * <th class="colFirst">Input</th>
	 * <th class="colLast">Output</th>
	 * </tr>
	 * <tr>
	 * <td>"MyClass"</td>
	 * <td>"My Class"</td>
	 * </tr>
	 * <tr>
	 * <td>"GL11Version"</td>
	 * <td>"GL 11 Version"</td>
	 * </tr>
	 * <tr>
	 * <td>"AString"</td>
	 * <td>"A String"</td>
	 * </tr>
	 * <tr>
	 * <td>"SimpleXMLParser"</td>
	 * <td>"Simple XML Parser"</td>
	 * </tr>
	 * </table>
	 *
	 * @param words
	 *            String to be converted
	 * @return words converted to human-readable name
	 */
	public static String decamelize(final String words) {
		return PATTERN_SPLIT_CAMELCASE.matcher(words).replaceAll(SPACE);
	}

	/**
	 * Converts the first character of the string to low case
	 * @param s
	 *            String to be converted
	 * @return the string with the first char in low case
	 */
	public static String withFirstCharToLowercase(final String s) {
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}
}
