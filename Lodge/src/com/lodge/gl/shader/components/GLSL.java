package com.lodge.gl.shader.components;

import java.util.Vector;

import com.lodge.err.GLError;

public class GLSL {

	public enum Parenthesis{
		LEFT,
		RIGHT
	}
	

	abstract class Operator{

		Vector<Operator> mOperator = new Vector<GLSL.Operator>();

		void add(Operator op){
			mOperator.add(op);
		}
		protected String mToken;
		public String toString(){
			return mToken;
		}
		abstract public String apply(Expression e1,Expression e2);

	}

	class Equals extends Operator{

		public Equals(Expression exp1, Expression exp2) {
			mToken = "=";
			if(exp1.multiplicity() != exp2.multiplicity() )
				GLError.exit("GLSL: Equals invalid expressions");
		}

		@Override
		public String apply(Expression e1,Expression e2) {
			return e1.name()+mToken+e2.toString();
		}
	}

	class Mult extends Operator{

		public Mult(Expression exp1, Expression exp2) {
			mToken = "*";
			if(exp1.multiplicity() != 1 && exp2.multiplicity() != 1 && exp1.multiplicity() != exp2.multiplicity() )
				GLError.exit("GLSL: Equals invalid expressions");
		}

		@Override
		public String apply(Expression e1, Expression e2) {
			return e1.name()+mToken+e2.toString();
		}

	}

	class Add extends Operator{
		@Override
		public String apply(Expression e1, Expression e2) {
			mToken = "+";
			return e1.name()+mToken+e2.toString();
		}
	}

	class Sub extends Operator{
		@Override
		public String apply(Expression e1, Expression e2) {
			mToken = "-";
			return e1.name()+mToken+e2.toString();
		}
	}

	class Dot extends Operator{
		@Override
		public String apply(Expression e1, Expression e2) {
			return "dot(" + e1.name() +","+e2.toString();
		}
	}
	
	class Normalize extends Operator{
		@Override
		public String apply(Expression e1, Expression e2) {
			return "normalize(" + e1.name() +","+e2.toString();
		}
	}
	
	class Max extends Operator{
		@Override
		public String apply(Expression e1, Expression e2) {
			return "max(" + e1.name() +","+e2.toString();
		}
	}





	abstract public class Expression{
		

		
		protected String mType; 
		protected String mName;
		
		Operator   mOperator;
		Parenthesis   mParenthesis = null;
		Expression mSuccessor;
		
		public abstract int    multiplicity(); 
		public abstract String typeString();

		String name(){
			return mName;
		}

		public String toString(){
			
			if(mSuccessor == null)
				return "";
			
			String par = "";
			if(mParenthesis == Parenthesis.LEFT)
				par = "(";
			if(mParenthesis == Parenthesis.RIGHT)
				par = ")";
			
			return par+mOperator.apply(this, mSuccessor);
			
			
		}
		
		public void parenthesis(Parenthesis p){
			Expression endExpression = end();
			endExpression.mParenthesis = p;
		}
	
		//public void mult()

		public void eval(Operator op, Expression e){
			Expression endExpression = end();
			endExpression.mSuccessor = e;
			endExpression.mOperator  = new Equals(this,mSuccessor);
		}
		
		public Operator operator(){
			return mOperator;
		}
		
		Expression end(){
			if(mSuccessor == null)
				return this;
			
			return mSuccessor.end();
		}


	}

	public Vec Vec(String name){
		Vec v = new Vec();
		v.mName = name;
		return v;
	}
	
	public Vec Vec(String name,int multiplicity){
		Vec v = new Vec();
		v.mName = name;
		v.mMultiplicity = multiplicity;
		return v;
	}


	public class Vec extends Expression{


		int    mMultiplicity = -1;

		@Override
		public int multiplicity() {
			return mMultiplicity;
		}


		@Override
		public String typeString() {
			return "vec"+String.valueOf(mMultiplicity);
		}

	}

	class Empty extends Expression{

		
		
		@Override
		public int multiplicity() {

			return 0;
		}

		@Override
		public String typeString() {
			
			return "";
		}

	}


}
