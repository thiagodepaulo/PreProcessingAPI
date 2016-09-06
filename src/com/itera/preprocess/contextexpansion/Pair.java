package com.itera.preprocess.contextexpansion;


public class Pair<X, Y> {

	public X _1;
	public Y _2;

	public Pair(X _1, Y _2) {
		this._1 = _1;
		this._2 = _2;
	}

	@Override
	public String toString() {
		return "(" + _1 + "," + _2 + ")";
	}
}
