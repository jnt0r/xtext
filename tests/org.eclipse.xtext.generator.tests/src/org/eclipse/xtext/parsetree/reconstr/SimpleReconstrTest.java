/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.parsetree.reconstr;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parsetree.NodeUtil;
import org.eclipse.xtext.testlanguages.SimpleExpressionsStandaloneSetup;
import org.eclipse.xtext.tests.AbstractGeneratorTest;
import org.eclipse.xtext.util.EmfFormater;

public class SimpleReconstrTest extends AbstractGeneratorTest {

	private static final Logger logger = Logger
			.getLogger(ComplexReconstrTest.class);

	public void testSimple1() throws Exception {
		String model = "a b";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimple2() throws Exception {
		String model = "( a b ) !";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testFollowingHiddenTokens() throws Exception {
		String model = "a ";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testComplex() throws Exception {
		String model = "( ( a b ) ! c  d e  f (  x s ) ( \t ( a \n\rb/*ffo \n bar */ ) ! c ) ! ) //holla\n!";
		assertEquals(model, parseAndSerialize(model));
	}

	private String parseAndSerialize(String model) throws Exception {
		EObject result = (EObject) getModel(model);
		if (logger.isDebugEnabled()) {
			logger.debug(EmfFormater.objToStr(result, ""));
			logger
					.debug(EmfFormater.objToStr(NodeUtil.getRootNode(result),
							""));
			logger.debug(EmfFormater.objToStr(NodeUtil.getRootNode(result)
					.getLeafNodes(), ""));
		}
		return serialize(result);
	}

	public void testSimpleExpressions5() throws Exception {
		with(SimpleExpressionsStandaloneSetup.class);
		String model = "a + b - c * d / e";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleExpressions1() throws Exception {
		with(SimpleExpressionsStandaloneSetup.class);
		String model = "a + b - c";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleTwoNumbers() throws Exception {
		String model = "2 45";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleTwoNumbersWithDefault() throws Exception {
		String model = "0 45";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleTwoNumbersWithDefault2() throws Exception {
		String model = "0 45 # 0 # 1 # 2";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleManyStrings1() throws Exception {
		String model = "= 'xxx' 'yyy'";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleManyStrings2() throws Exception {
		String model = "= 'xxx' 'yyy' 'zzzz'";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testSimpleAlternativeAssignment1() throws Exception {
		String model = "#2 mykeyword1";
		assertEquals(model, parseAndSerialize(model));
	}

	// FIXME: this depends on
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=250313
	// public void testSimpleAlternativeAssignment2() throws Exception {
	// String model = "#2 'str'";
	// assertEquals(model, parseAndSerialize(model));
	// }

	public void testCrossRef() throws Exception {
		String model = "type A extends B type B extends A";
		assertEquals(model, parseAndSerialize(model));
	}
	
	public void testSpare() throws Exception {
		String model = "#3 id1";
		assertEquals(model, parseAndSerialize(model));
	}

	@Override
	protected void setUp() throws Exception {
		with(SimpleReconstrTestStandaloneSetup.class);
		super.setUp();
	}
}
