package model;


public class MyClass {

	private int value;

	public MyClass() {
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

	public String op1(int arg1) {
		// TODO implement op1(...)
		throw new UnsupportedOperationException();
	}

}
