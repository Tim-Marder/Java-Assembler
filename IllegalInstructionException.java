package project;

public class IllegalInstructionException extends RuntimeException {
	private static final long serialVersionUID = 4356485326194262708L;

	/**
	 * Preferred constructor that sets the inherited message field
	 * of the exception object
	 * @param arg0 message passed by the exception that was thrown
	 */
	public IllegalInstructionException(String arg0) {
		super(arg0);
	}
	
	public IllegalInstructionException() {
	}

}

