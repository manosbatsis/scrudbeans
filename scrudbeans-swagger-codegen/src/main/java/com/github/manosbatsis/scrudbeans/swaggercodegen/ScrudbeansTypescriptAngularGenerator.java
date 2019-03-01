package com.github.manosbatsis.scrudbeans.swaggercodegen;

import java.util.regex.Pattern;

import io.swagger.codegen.languages.TypeScriptAngularClientCodegen;
import org.apache.commons.lang3.StringUtils;

/**
 * Removes springfox-generated operationId suffixes
 * before delegating to TypeScriptAngularClientCodegen,
 * i.e. "typescript-angular"
 */
public class ScrudbeansTypescriptAngularGenerator extends TypeScriptAngularClientCodegen {

	private static final Pattern patternForTrailingNumerics = Pattern.compile(".*[0-9]+$");
	private static final Pattern patternToRemoveTrailingNumerics = Pattern.compile("\\d*$");
	private static final String[] suffixes = {
			"UsingGET", "UsingPOST", "UsingPUT", "UsingDELETE", "UsingOPTIONS", "UsingPATCH",
			"UsingCONNECT", "UsingTRACE", "UsingHEAD"};

	@Override
	public String toOperationId(String operationId) {
		System.out.println("toOperationId, in: " + operationId);
		// throw exception if method name is empty
		if (StringUtils.isEmpty(operationId)) {
			throw new RuntimeException("Empty method name (operationId) not allowed");
		}

		// Remove numeric suffix if found
		if (patternForTrailingNumerics.matcher(operationId).matches()) {
			operationId = patternToRemoveTrailingNumerics.matcher(operationId).replaceAll("");
		}
		// Remove trailing underscore
		operationId = removeSuffix(operationId, "_");
		// Remove default springfox suffixes
		operationId = removeSuffix(operationId, suffixes);


		// Forward for normal processing
		operationId = super.toOperationId(operationId);
		System.out.println("toOperationId, out: " + operationId);
		return operationId;
	}

	private String removeSuffix(String original, String... suffixes) {
		boolean foundSuffix = false;
		for (int i = 0; !foundSuffix && i < suffixes.length; i++) {
			if (original.endsWith(suffixes[i])) {
				foundSuffix = true;
				int position = original.lastIndexOf(suffixes[i]);
				original = original.substring(0, position);
			}
		}
		return original;
	}

}
