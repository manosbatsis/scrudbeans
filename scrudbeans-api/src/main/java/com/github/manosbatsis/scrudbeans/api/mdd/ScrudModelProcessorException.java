package com.github.manosbatsis.scrudbeans.api.mdd;


/**
 * Thrown when a com.github.manosbatsis.scrudbeans.javapoet.ScrudModelAnnotationProcessor encounters an error
 */
public class ScrudModelProcessorException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public ScrudModelProcessorException() {
		super();
	}

	/**
	 * Constructs a new ScrudModelProcessorException with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @param   message   the detail message. The detail message is saved for
	 *          later retrieval by the {@link #getMessage()} method.
	 */
	public ScrudModelProcessorException(String message) {
		super(message);
	}
}