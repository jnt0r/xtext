/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.resource.impl;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.ignorecase.IIgnoreCaseResourceDescription;

import com.google.common.collect.Iterables;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class ResourceDescriptionsBasedContainerTest extends TestCase implements IResourceDescriptions {

	private ResourceDescriptionsBasedContainer container;
	private EClass eClass;
	private URI uri;
	private ResourceDescription resourceDescription;
	
	private class ResourceDescription implements IIgnoreCaseResourceDescription {

		public Iterable<IEObjectDescription> getExportedObjects() {
			if (eClass != null)
				return Collections.singleton(EObjectDescription.create(eClass.getName(), eClass));
			return Collections.emptyList();
		}

		public Iterable<IEObjectDescription> getExportedObjects(EClass clazz, String name) {
			if (eClass != null && EcoreUtil2.isAssignableFrom(clazz,eClass.eClass()) && eClass.getName().equals(name))
				return Collections.singleton(EObjectDescription.create(eClass.getName(), eClass));
			return Collections.emptyList();
		}
		
		public Iterable<IEObjectDescription> getExportedObjectsIgnoreCase(EClass clazz, String name) {
			if (eClass != null && EcoreUtil2.isAssignableFrom(clazz,eClass.eClass()) && eClass.getName().equalsIgnoreCase(name))
				return Collections.singleton(EObjectDescription.create(eClass.getName(), eClass));
			return Collections.emptyList();
		}

		public Iterable<IEObjectDescription> getExportedObjects(EClass clazz) {
			if (eClass != null && EcoreUtil2.isAssignableFrom(clazz,eClass.eClass()))
				return Collections.singleton(EObjectDescription.create(eClass.getName(), eClass));
			return Collections.emptyList();
		}

		public Iterable<IEObjectDescription> getExportedObjectsForEObject(EObject object) {
			if (eClass != null && object == eClass)
				return Collections.singleton(EObjectDescription.create(eClass.getName(), eClass));
			return Collections.emptyList();
		}

		public Iterable<String> getImportedNames() {
			fail("unexpected");
			return null;
		}

		public URI getURI() {
			return uri;
		}

		public Iterable<IReferenceDescription> getReferenceDescriptions() {
			return Collections.emptyList();
		}
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("SomeName");
		resourceDescription = new ResourceDescription();
		container = new ResourceDescriptionsBasedContainer(this);
	}
	
	public void testFindAllEObjectsByType_01() {
		Iterable<IEObjectDescription> iterable = container.findAllEObjects(EcorePackage.Literals.ECLASSIFIER);
		EObject eObject = Iterables.getOnlyElement(iterable).getEObjectOrProxy();
		assertSame(eClass, eObject);
	}
	
	public void testFindAllEObjectsByType_02() {
		eClass = null;
		Iterable<IEObjectDescription> iterable = container.findAllEObjects(EcorePackage.Literals.ECLASSIFIER);
		assertTrue(Iterables.isEmpty(iterable));
	}
	
	public void testFindAllEObjectsByName_01() {
		Iterable<IEObjectDescription> iterable = container.findAllEObjects(EcorePackage.Literals.ECLASSIFIER, "SomeName");
		EObject eObject = Iterables.getOnlyElement(iterable).getEObjectOrProxy();
		assertSame(eClass, eObject);
	}
	
	public void testFindAllEObjectsByName_02() {
		eClass = null;
		Iterable<IEObjectDescription> iterable = container.findAllEObjects(EcorePackage.Literals.ECLASSIFIER, "SomeName");
		assertTrue(Iterables.isEmpty(iterable));
	}
	
	public void testFindAllEObjectsByNameIgnoreCase_01() {
		Iterable<IEObjectDescription> iterable = container.findAllEObjectsIgnoreCase(EcorePackage.Literals.ECLASSIFIER, "SomeName".toUpperCase());
		EObject eObject = Iterables.getOnlyElement(iterable).getEObjectOrProxy();
		assertSame(eClass, eObject);
	}
	
	public void testFindAllEObjectsByNameIgnoreCase_02() {
		Iterable<IEObjectDescription> iterable = container.findAllEObjectsIgnoreCase(EcorePackage.Literals.ECLASSIFIER, "SomeName".toLowerCase());
		EObject eObject = Iterables.getOnlyElement(iterable).getEObjectOrProxy();
		assertSame(eClass, eObject);
	}
	
	public void testFindAllEObjectsByNameIgnoreCase_03() {
		eClass = null;
		Iterable<IEObjectDescription> iterable = container.findAllEObjectsIgnoreCase(EcorePackage.Literals.ECLASSIFIER, "SomeName");
		assertTrue(Iterables.isEmpty(iterable));
	}

	public Iterable<IResourceDescription> getAllResourceDescriptions() {
		return Collections.<IResourceDescription>singletonList(resourceDescription);
	}
	
	public IResourceDescription getResourceDescription(URI uri) {
		if (uri == this.uri)
			return resourceDescription;
		return null;
	}
	
}
