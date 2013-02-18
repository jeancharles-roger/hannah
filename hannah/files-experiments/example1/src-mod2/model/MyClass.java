package model;


public class MyClass {

	private int value;

	private String name;

	public MyClass() {
		value = 1;
	}

	/**
	 * <p>Gets value.</p>
	 */
	public int getValue() {
		return value;
	}

	/**
	 * <p>Sets value.</p>
	 */
	public void setValue(int newValue) {
		if (value != newValue) {
			this.value= newValue;
		}
	}

	/**
	 * <p>Gets name.</p>
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Sets name.</p>
	 */
	public void setName(String newValue) {
		if (name == null ? newValue != null : (name.equals(newValue) == false)) {
			this.name= newValue;
		}
	}

	public String op1(int arg1, String myArg) {
		return "op:" + arg1 + ", " + myArg;
	}

}

