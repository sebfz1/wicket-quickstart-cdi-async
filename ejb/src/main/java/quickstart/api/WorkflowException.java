package quickstart.api;


public class WorkflowException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WorkflowException()
	{
		super();
	}

	public WorkflowException(String message)
	{
		super(message);
	}

	public WorkflowException(Throwable cause)
	{
		super(cause);
	}

	public WorkflowException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
