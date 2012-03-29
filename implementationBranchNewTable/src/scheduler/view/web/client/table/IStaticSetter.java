package scheduler.view.web.client.table;

public interface IStaticSetter<ObjectType, ValType> {
	void setValueInObject(ObjectType object, ValType newValue);
}
