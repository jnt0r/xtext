package org.eclipse.xtend.ide.tests.imports

import com.google.inject.Inject
import org.eclipse.xtend.ide.tests.AbstractXtendUITestCase
import org.eclipse.xtend.ide.tests.WorkbenchTestHelper
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.xbase.imports.ImportOrganizer
import org.junit.Test

class OrganizeImportsTest extends AbstractXtendUITestCase {
	
	@Inject ImportOrganizer importOrganizer
	
	@Inject extension WorkbenchTestHelper
	
	def protected assertIsOrganizedTo(CharSequence model, CharSequence expected) {
		assertIsOrganizedTo(model, "Foo", expected)
	}
	
	def protected assertIsOrganizedTo(CharSequence model, String fileName, CharSequence expected) {
		assertFalse (expected.toString.contains("$"))
		val xtendFile = xtendFile(fileName, model.toString)
		
		val changes = importOrganizer.getOrganizedImportChanges(xtendFile.eResource as XtextResource)
		val builder = new StringBuilder(model)
		for(it: changes.sortBy[offset].reverse)
			builder.replace(offset, offset + length, text)
		assertEquals(expected.toString, builder.toString)
	}
	
	@Test def testUnresolvedType() {
		'''
			class Foo implements Serializable {
			}
		'''.assertIsOrganizedTo('''
			import java.io.Serializable
			
			class Foo implements Serializable {
			}
		''')
	}
	
	@Test def testUnresolvedNestedType_01() {
		'''
			class Foo implements Map$Entry {
			}
		'''.assertIsOrganizedTo('''
			import java.util.Map
			
			class Foo implements Map.Entry {
			}
		''')
	}
	
	@Test def testUnresolvedNestedType_02() {
		'''
			class Foo {
				def m() {
					Map$Entry::DoesNotMatter
				}
			}
		'''.assertIsOrganizedTo('''
			import java.util.Map
			
			class Foo {
				def m() {
					Map.Entry::DoesNotMatter
				}
			}
		''')
	}
	
	@Test def testUnresolvedNestedType_03() {
		'''
			class Foo implements Map.Entry {
			}
		'''.assertIsOrganizedTo('''
			import java.util.Map
			
			class Foo implements Map.Entry {
			}
		''')
	}
	
	@Test def testUnresolvedNestedType_04() {
		'''
			class Foo {
				def m() {
					Map.Entry::DoesNotMatter
				}
			}
		'''.assertIsOrganizedTo('''
			import java.util.Map
			
			class Foo {
				def m() {
					Map.Entry::DoesNotMatter
				}
			}
		''')
	}
	
	@Test def testUnresolvedNestedType_05() {
		'''
			class Foo {
				def m() {
					Map::Entry::DoesNotMatter
				}
			}
		'''.assertIsOrganizedTo('''
			import java.util.Map
			
			class Foo {
				def m() {
					Map.Entry::DoesNotMatter
				}
			}
		''')
	}

	@Test def testUnresolvedConstructorCall() {
		'''
			class Foo {
				Object bar = new ArrayList
			}
		'''.assertIsOrganizedTo('''
			import java.util.ArrayList
			
			class Foo {
				Object bar = new ArrayList
			}
		''')
	}
	
	@Test def testConstructorCallToEnum() {
		'''
			import java.lang.annotation.RetentionPolicy
			class Foo {
				Object bar = new RetentionPolicy
			}
		'''.assertIsOrganizedTo('''
			import java.lang.annotation.RetentionPolicy
			
			class Foo {
				Object bar = new RetentionPolicy
			}
		''')
	}
	
	@Test def testFullyQualifiedConstructorCallToEnum() {
		'''
			class Foo {
				Object bar = new java.lang.annotation.RetentionPolicy
			}
		'''.assertIsOrganizedTo('''
			import java.lang.annotation.RetentionPolicy
			
			class Foo {
				Object bar = new RetentionPolicy
			}
		''')
	}
	
	@Test def testUnresolvedConstructorCallToEnum() {
		'''
			class Foo {
				Object bar = new RetentionPolicy
			}
		'''.assertIsOrganizedTo('''
			import java.lang.annotation.RetentionPolicy
			
			class Foo {
				Object bar = new RetentionPolicy
			}
		''')
	}
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=398623
	@Test def testWildcardType_0() {
		'''
			class Foo {
				Class<?> bar
			}
		'''.assertIsOrganizedTo('''
			class Foo {
				Class<?> bar
			}
		''')
	}

	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=398623
	@Test def testWildcardType_1() {
		'''
			class Foo {
				Class<? extends Serializable> bar
			}
		'''.assertIsOrganizedTo('''
			import java.io.Serializable
			
			class Foo {
				Class<? extends Serializable> bar
			}
		''')
	}
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=398623
	@Test def testWildcardType_2() {
		'''
			class Foo {
				Class<Serializable> bar
			}
		'''.assertIsOrganizedTo('''
			import java.io.Serializable
			
			class Foo {
				Class<Serializable> bar
			}
		''')
	}
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=406301
	@Test def testStaticTypeUsedTwice() {
		'''
			import java.util.Collections
			import java.util.List
			class Foo {
				def m() {
					println(Collections::singleton)
					println(Collections::singleton)
					println(Collections::singleton)
				}
			}
		'''.assertIsOrganizedTo('''
			import java.util.Collections
			
			class Foo {
				def m() {
					println(Collections::singleton)
					println(Collections::singleton)
					println(Collections::singleton)
				}
			}
		''')
	}
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=412487
	@Test def testDontChangeDanglingFeatureCalls() {
		'''
			class Foo {
				def foo() {
					field.x
				}
			}
		'''.assertIsOrganizedTo('''
			class Foo {
				def foo() {
					field.x
				}
			}
		''')
	}
	
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=410752
	@Test def testKeepRequiredStaticExtensionImport() {
		'''
			package repro

			import static extension repro.B.*
			
			class A {
				def void m() {
					A.ext
				}
			}
			class B {
				def static void ext(Class<?> c) {}
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			package repro

			import static extension repro.B.*
			
			class A {
				def void m() {
					A.ext
				}
			}
			class B {
				def static void ext(Class<?> c) {}
			}
		''')
	}
	
	@Test def testJavaDocReference() {
		'''
			package repro
			
			/**
			 * @see java.lang.String
			 */
			@SupressWarnings('all')
			class Foo {}
		'''.assertIsOrganizedTo("repro/Foo", '''
			package repro
			
			/**
			 * @see String
			 */
			@SupressWarnings('all')
			class Foo {}
		''')
	}
	
	@Test def testJavaDocReference2() {
		'''
			package repro
			
			class Foo {
				
				/**
				 * @see java.lang.String
				 */
				@SupressWarnings('all')
				def void foo() {}
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			package repro
			
			class Foo {
				
				/**
				 * @see String
				 */
				@SupressWarnings('all')
				def void foo() {}
			}
		''')
	}
	
	@Test def implicitImport() {
		'''
			package repro
			
			class Foo {
			
				def Pair<Integer, Integer> m() {}
			
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			package repro
			
			class Foo {
			
				def Pair<Integer, Integer> m() {}
			
			}
		''')
	}
	
	@Test def implicitImport_1() {
		'''
			package repro
			
			import java.lang.Integer;
			import org.eclipse.xtext.xbase.lib.Pair;
			
			class Foo {
			
				def Pair<Integer, Integer> m() {}
			
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			package repro
			
			class Foo {
			
				def Pair<Integer, Integer> m() {}
			
			}
		''')
	}

	@Test def void testMemberImport_01() {
		'''
			import static java.lang.String.valueOf
			import static java.lang.Integer.valueOf
			
			class Foo {
			
				def foo() {
					valueOf(1)
				}
			
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			import static java.lang.Integer.valueOf
			import static java.lang.String.valueOf
			
			class Foo {
			
				def foo() {
					valueOf(1)
				}
			
			}
		''')
	}

	@Test def void testMemberImport_02() {
		'''
			import static java.lang.Integer.*
			import static java.lang.Integer.valueOf
			
			class Foo {
			
				def foo() {
					valueOf(1)
				}
			
			}
		'''.assertIsOrganizedTo("repro/Foo", '''
			import static java.lang.Integer.valueOf
			
			class Foo {
			
				def foo() {
					valueOf(1)
				}
			
			}
		''')
	}
	
	@Test def void testAnonymousClass_0() {
		'''
			class Foo {
				val bar = new Bar() {
					override bar() {}
				}
			}
			
			interface Bar {
				def bar() {}
			}
		'''.assertIsOrganizedTo('''
			class Foo {
				val bar = new Bar() {
					override bar() {}
				}
			}
			
			interface Bar {
				def bar() {}
			}
		''')
	}

	@Test def void testAnonymousClass_1() {
		'''
			class Foo {
				val bar = new Serializable() {
				}
			}
		'''.assertIsOrganizedTo('''
			import java.io.Serializable
			
			class Foo {
				val bar = new Serializable() {
				}
			}
		''')
	}

	@Test def void testAnonymousClass_2() {
		'''
			class Foo {
				val bar = new Iterable<Serializable>() {
					override Iterator<Serializable> iterator() { null }
				}
			}
		'''.assertIsOrganizedTo('''
			import java.io.Serializable
			import java.util.Iterator
			
			class Foo {
				val bar = new Iterable<Serializable>() {
					override Iterator<Serializable> iterator() { null }
				}
			}
		''')
	}

}