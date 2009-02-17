/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.parser;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.IMetamodelAccess;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.parsetree.AbstractNode;

import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class DefaultEcoreElementFactory implements IAstFactory {

	// required due to static initializer in template for metamodelAccess
	@SuppressWarnings("unused")
	@Inject
	private IMetamodelAccess metamodelAccess;

	@Inject
	private IValueConverterService converterService;

	@Inject(optional=true)
	private ITokenToStringConverter tokenConverter;

	public IValueConverterService getConverterService() {
		return converterService;
	}

	public void setConverterService(IValueConverterService converterService) {
		this.converterService = converterService;
	}

	public EObject create(EClassifier classifier) {
		if (classifier == null)
			throw new NullPointerException("Classifier may not be null.");
		if (!(classifier instanceof EClass))
			throw new IllegalArgumentException("Cannot create instance of datatype '" + classifier.getName() + "'");
		EClass clazz = (EClass) classifier;
		if (clazz.isAbstract() || clazz.isInterface())
			throw new IllegalArgumentException("Cannot create instance of abstract class '" + clazz.getName() + "'");
		return clazz.getEPackage().getEFactoryInstance().create(clazz);
	}

	public void set(EObject object, String feature, Object value, String ruleName, AbstractNode node) throws ValueConverterException {
		final EStructuralFeature structuralFeature = object.eClass().getEStructuralFeature(feature);
		if (structuralFeature == null)
			throw new IllegalArgumentException(feature + " object was: " + object);
		final Object tokenValue = getTokenValue(value, ruleName, node);
		object.eSet(structuralFeature, tokenValue);
	}

	private Object getTokenValue(Object tokenOrValue, String ruleName, AbstractNode node) throws ValueConverterException {
		try {
			Object value = getTokenAsStringIfPossible(tokenOrValue);
			if (value instanceof CharSequence && ruleName != null) {
				value = converterService.toValue(value.toString(), ruleName, node);
			}
			return value;
		} catch(ValueConverterException e) {
			throw e;
		} catch(Exception e) {
			throw new ValueConverterException(null, node, e);
		}
	}

	protected Object getTokenAsStringIfPossible(Object tokenOrValue) {
		if (tokenConverter != null)
			return tokenConverter.getTokenAsStringIfPossible(tokenOrValue);
		return tokenOrValue;
	}

	@SuppressWarnings("unchecked")
	public void add(EObject object, String feature, Object value, String ruleName, AbstractNode node) throws ValueConverterException {
		if (value == null)
			return;
		final EStructuralFeature structuralFeature = object.eClass().getEStructuralFeature(feature);
		if (structuralFeature == null)
			throw new IllegalArgumentException(feature);
		((Collection<Object>) object.eGet(structuralFeature)).add(getTokenValue(value, ruleName, node));
	}

}
