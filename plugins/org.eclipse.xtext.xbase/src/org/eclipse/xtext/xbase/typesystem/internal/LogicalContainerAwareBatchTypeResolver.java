/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.jvmmodel.ILogicalContainerProvider;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 * TODO JavaDoc, toString
 */
@NonNullByDefault
public class LogicalContainerAwareBatchTypeResolver extends DefaultBatchTypeResolver {
	
	@Inject
	private ILogicalContainerProvider logicalContainerProvider;
	
	@Override
	protected List<EObject> getEntryPoints(EObject object) {
		if (object.eContainer() == null && object.eResource() != null) {
			Resource resource = object.eResource();
			List<EObject> contents = resource.getContents();
			List<EObject> result = Lists.newArrayList();
			for(EObject content: contents) {
				if (content instanceof JvmType) {
					result.add(content);
				}
			}
			return result;
		}
		JvmIdentifiableElement logicalContainer = logicalContainerProvider.getNearestLogicalContainer(object);
		if (logicalContainer == null) {
			if (object instanceof JvmIdentifiableElement) {
				logicalContainer = (JvmIdentifiableElement) object;
			} else {
				EObject container = object.eContainer();
				if (container != null) {
					return getEntryPoints(container);
				}
				throw new IllegalStateException("object is not contained in a logical container: " + object);
			}
		}
		JvmDeclaredType declaredType = EcoreUtil2.getContainerOfType(logicalContainer, JvmDeclaredType.class);
		if (declaredType != null) {
			while(declaredType.getDeclaringType() != null) {
				declaredType = declaredType.getDeclaringType();
			}
		} else {
			throw new IllegalStateException("logicalContainer is not contained in a declaredType");
		}
		return Collections.<EObject>singletonList(declaredType);
	}

}
