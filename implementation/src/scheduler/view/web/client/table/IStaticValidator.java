package scheduler.view.web.client.table;


public interface IStaticValidator<ObjectType, ValueType> {
	public abstract class ValidateResult { }
	public class InputValid extends ValidateResult { }
	public class InputWarning extends ValidateResult {
		String reason;
		public InputWarning(String reason) { this.reason = reason; }
	}
	public class InputInvalid extends ValidateResult {
		String reason;
		public InputInvalid(String reason) { this.reason = reason; }
	}
	
	ValidateResult validate(ObjectType object, ValueType newValue);
}
