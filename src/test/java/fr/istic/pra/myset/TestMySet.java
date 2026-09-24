package fr.istic.pra.myset;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import fr.istic.pra.myset.MySet;
import fr.istic.pra.myset.SmallSet;
import fr.istic.pra.myset.SubSet;
import fr.istic.pra.util.EnsLoader;
import fr.istic.pra.util.L3List;
import fr.istic.pra.util.L3Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMySet {
	public static final String ENS0 = "f0.ens";
	public static final String ENS1 = "f1.ens";
	public static final String ENS3 = "f3.ens";
	public static final String TEST_U01 = "test-u01.ens";
	public static final String TEST_D01 = "test-d01.ens";
	public static final String TEST_S01 = "test-s01.ens";
	public static final String TEST_D03 = "test-d03.ens";
	public static final String TEST_I03 = "test-i03.ens";
	public static final String TEST_U03 = "test-u03.ens";

	/**
	 * @param l1 premier ensemble
	 * @param l2 deuxième ensemble
	 * @return true si les ensembles l1 et l2 sont égaux, false sinon
	 */
	@SuppressWarnings("unchecked")
	public static boolean compareMySets(MySet l1, MySet l2) {
		// Rend le champ "list" accessible pour la comparaison
		try {
			Field listField = MySet.class.getDeclaredField("list");
			listField.setAccessible(true);
			L3List<SubSet> list1 = (L3List<SubSet>) listField.get(l1);
			L3List<SubSet> list2 = (L3List<SubSet>) listField.get(l2);
			return compareL3Lists(list1, list2);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean compareL3Lists(L3List<SubSet> l1, L3List<SubSet> l2) {
		L3Iterator<SubSet> it1 = l1.l3Iterator();
		L3Iterator<SubSet> it2 = l2.l3Iterator();
		boolean bool = true;
		while (!it1.isOnFlag() && bool) {
			SubSet s1 = it1.getValue();
			SubSet s2 = it2.getValue();
			if (!compareSubSets(s1, s2)) {
				bool = false;
			}
			it1.goForward();
			it2.goForward();
		}
		return bool && it1.isOnFlag() && it2.isOnFlag();
	}

	public static boolean compareSubSets(SubSet s1, SubSet s2) {
		// Compare les rangs et les SmallSets des deux SubSets.
		// Rend les champs "rank" et "set" accessibles pour la comparaison
		try {
			Field rankField = SubSet.class.getDeclaredField("rank");
			Field setField = SubSet.class.getDeclaredField("set");
			rankField.setAccessible(true);
			setField.setAccessible(true);
			int rank1 = (int) rankField.get(s1);
			int rank2 = (int) rankField.get(s2);
			SmallSet set1 = (SmallSet) setField.get(s1);
			SmallSet set2 = (SmallSet) setField.get(s2);
			return rank1 == rank2 && compareSmallSets(set1, set2);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean compareSmallSets(SmallSet s1, SmallSet s2) {
		return !(s1.size() == 0 || s2.size() == 0) && s1.toString().equals(s2.toString());
	}

	/**
	 * @param mySet ensemble à tester
	 * @return true si mySet est bien un ensemble creux
	 */
	@SuppressWarnings("unchecked")
	public static boolean testSparsity(MySet mySet) {
		// Test if the set is sparse, i.e., if it has empty SubSets.
		// Rend le champ "list" accessible pour la vérification
		try {
			Field listField = MySet.class.getDeclaredField("list");
			listField.setAccessible(true);
			L3List<SubSet> list = (L3List<SubSet>) listField.get(mySet);
			return testSparsityInL3List(list);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean testSparsityInL3List(L3List<SubSet> list) {
		L3Iterator<SubSet> it = list.l3Iterator();
		while (!it.isOnFlag()) {
			SubSet subset = it.getValue();
			// Utiliser la réflexion pour accéder au champ "set" de SubSet
			try {
				Field setField = SubSet.class.getDeclaredField("set");
				setField.setAccessible(true);
				SmallSet smallSet = (SmallSet) setField.get(subset);
				if (smallSet.size() == 0) {
					return false; // Found an empty SmallSet, not sparse
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
			it.goForward();
		}
		return it.isOnFlag();
	}

	private static MySet readFileToMySet(String resourceName) {
		MySet set = new MySet();
		EnsLoader.readInto(resourceName, set);
		return set;
	}

	@Test
	public void testSetCreation() {
		MySet mySet1 = readFileToMySet("test-desordre.ens");
		MySet mySet2 = readFileToMySet(ENS0);
		assertTrue(compareMySets(mySet1, mySet2), "set creation in disorder");
	}

	@Test
	public void testContainment1() {
		MySet mySet = readFileToMySet(ENS0);
		boolean bool1 = mySet.contains(128);
		boolean bool2 = mySet.contains(129);
		boolean bool3 = mySet.contains(32767);
		boolean bool4 = mySet.contains(22222);
		assertTrue(bool1 && !bool2 && bool3 && !bool4, "appartenance 1");
	}

	@Test
	public void testContainment2() {
		MySet mySet = readFileToMySet(ENS0);
		boolean bool = mySet.contains(32511);
		assertTrue(!bool, "appartenance 2");
	}

	@Test
	public void testSetAddition() {
		MySet mySet1 = readFileToMySet(ENS0);
		EnsLoader.readInto(ENS1, mySet1);
		MySet mySet2 = readFileToMySet(TEST_U01);
		assertTrue(compareMySets(mySet1, mySet2), "set addition f0 f1");
	}

	@Test
	public void testRemoval1() {
		MySet mySet1 = readFileToMySet(ENS0);
		mySet1.remove(64);
		mySet1.remove(32767);
		MySet mySet2 = readFileToMySet("test-d05.ens");
		assertTrue(testSparsity(mySet1), "deletion sparsity 1");
		assertTrue(compareMySets(mySet1, mySet2), "deletion 1");
	}

	@Test
	public void testRemoval2() {
		MySet mySet1 = new MySet();
		mySet1.add(0);
		mySet1.add(512);
		MySet mySet2 = new MySet();
		mySet2.add(0);
		mySet2.add(512);

		mySet1.remove(256);
		assertTrue(testSparsity(mySet1), "deletion sparsity 2");
		assertTrue(compareMySets(mySet1, mySet2), "deletion 2");
	}

	@Test
	public void testRemoval3() {
		MySet mySet1 = new MySet();
		mySet1.add(64);
		mySet1.remove(64);
		assertTrue(testSparsity(mySet1), "deletion sparsity 3");
		assertTrue(mySet1.isEmpty(), "deletion 3");
	}

	@Test
	public void testRemoval4() {
		MySet mySet1 = new MySet();
		mySet1.add(64);
		mySet1.add(3333);
		mySet1.remove(64);
		mySet1.remove(3333);
		assertTrue(testSparsity(mySet1), "deletion sparsity 4");
		assertTrue(mySet1.isEmpty(), "deletion 4");
	}

	@Test
	public void testRemoval5() {
		MySet mySet1 = readFileToMySet(ENS0);
		EnsLoader.readRemove(ENS1, mySet1);
		MySet mySet2 = readFileToMySet(TEST_D01);
		assertTrue(testSparsity(mySet1), "deletion sparsity 5");
		assertTrue(compareMySets(mySet1, mySet2), "deletion 5");
	}

	@Test
	public void testRemoval6() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet1.remove(4744);
		assertTrue(testSparsity(mySet1), "deletion sparsity 6");
		assertTrue(compareMySets(mySet1, mySet2), "deletion 6");
	}

	@Test
	public void testSize1() {
		MySet mySet = readFileToMySet(ENS0);
		int size = mySet.size();
		assertEquals(14, size);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSize2() {
		MySet mySet = new MySet();
		// Utiliser la reflexion pour accéder à l'itérateur et ajouter un élément directement dans le SmallSet de la sentinelle
		L3List<SubSet> list = null;
		try {
			Field listField = MySet.class.getDeclaredField("list");
			listField.setAccessible(true);
			list = (L3List) listField.get(mySet);
			// list.add(new SubSet(new SmallSet()));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		SubSet subSet = list.l3Iterator().getValue();
		// Utiliser la reflexion pour accéder au SmallSet et ajouter un élément directement
		try {
			Field setField = SubSet.class.getDeclaredField("set");
			setField.setAccessible(true);
			SmallSet smallSet = (SmallSet) setField.get(subSet);
			smallSet.add(22);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		int size = mySet.size();
		assertEquals(0, size);
	}

	@Test
	public void testSize3() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS3);
		mySet1.union(mySet2); // ne passera que si union est implémenté !
		int size = mySet1.size();
		assertEquals(23, size);
	}


	@Test
	public void testUnion1() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS3);
		MySet mySet3 = readFileToMySet(TEST_U03);
		mySet1.union(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "union f0 and f3");
	}

	@Test
	public void testUnion2() {
		MySet mySet1 = readFileToMySet(ENS3);
		MySet mySet2 = readFileToMySet(ENS0);
		MySet mySet3 = readFileToMySet(TEST_U03);
		mySet1.union(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "union f3 and f0");
	}

	@Test
	public void testUnion3() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();
		MySet mySet3 = new MySet();

		mySet1.add(100);
		mySet1.add(300);

		mySet2.add(100);

		mySet3.add(100);
		mySet3.add(300);

		mySet1.union(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "union 100+300 and 100");
	}

	@Test
	public void testUnion4() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();

		mySet1.add(100);

		mySet2.add(100);
		mySet2.add(301);

		mySet1.union(mySet2);
		assertTrue(compareMySets(mySet1, mySet2), "union 100 and 100+301");
	}

	@Test
	public void testUnion5() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS3);
		MySet mySet3 = readFileToMySet(TEST_U03);
		mySet1.union(mySet2);
		mySet2.add(8201);
		assertTrue(compareMySets(mySet1, mySet3), "union f0 and f3 (bis)");
	}

	@Test
	public void testSymmetricDifference1() {
		MySet mySet1 = readFileToMySet(ENS1);
		MySet mySet2 = readFileToMySet(ENS0);
		MySet mySet3 = readFileToMySet(TEST_S01);
		mySet1.symmetricDifference(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference f1 and f0");
	}

	@Test
	public void testSymmetricDifference2() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS1);
		MySet mySet3 = readFileToMySet(TEST_S01);
		mySet1.symmetricDifference(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference f0 and f1");
	}

	@Test
	public void testSymmetricDifference3() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();
		MySet mySet3 = new MySet();

		mySet1.add(100);
		mySet1.add(300);

		mySet2.add(100);

		mySet3.add(300);

		mySet1.symmetricDifference(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference 100+300 and 100");
	}

	@Test
	public void testSymmetricDifference4() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();
		MySet mySet3 = new MySet();

		mySet1.add(100);

		mySet2.add(100);
		mySet2.add(301);

		mySet3.add(301);

		mySet1.symmetricDifference(mySet2);
		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference 100 and 100+301");
	}

	@Test
	public void testSymmetricDifference5() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet1.symmetricDifference(mySet2);
		assertTrue(mySet1.isEmpty(), "symmetric difference f0 and f0 :version 1");
	}

	@Test
	public void testSymmetricDifference6() {
		MySet mySet1 = readFileToMySet(ENS0);
		mySet1.symmetricDifference(mySet1);
		assertTrue(mySet1.isEmpty(), "symmetric difference f0 and f0 : version 2");
	}

	@Test
	public void testSymmetricDifference7() {
		MySet mySet1 = readFileToMySet(ENS1);
		MySet mySet2 = readFileToMySet(ENS0);
		MySet mySet3 = readFileToMySet(TEST_S01);
		mySet1.symmetricDifference(mySet2);
		mySet2.add(5001);
		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference f1 and f0 (bis)");
	}

	@Test
	public void testSymmetricDifference8() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();
		MySet mySet3 = new MySet();
		mySet1.add(100);
		mySet1.add(300);

		mySet2.add(150);
		mySet2.add(800);

		mySet3.add(100);
		mySet3.add(150);
		mySet3.add(300);
		mySet3.add(800);

		mySet1.symmetricDifference(mySet2);

		assertTrue(compareMySets(mySet1, mySet3), "symmetric difference 100+300 and 150+800");
	}

	@Test
	public void testEquality1() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		assertEquals( mySet1, mySet2);
	}

	@Test
	public void testEquality2() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet2.add(8888);
		assertNotEquals(mySet1, mySet2);
	}

	@Test
	public void testEquality3() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet2.add(5001);
		assertNotEquals(mySet1, mySet2);
	}

	@Test
	public void testEquality4() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet2.add(8888);
		assertNotEquals(mySet2, mySet1);
	}

	@Test
	public void testEquality5() {
		MySet mySet1 = readFileToMySet(ENS0);
		MySet mySet2 = readFileToMySet(ENS0);
		mySet2.add(5001);
		assertNotEquals(mySet2, mySet1);
	}

	@Test
	public void testEquality6() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();
		mySet1.add(100);

		mySet2.add(100);
		mySet2.add(300);

		assertNotEquals(mySet2, mySet1);
	}

	@Test
	public void testEquality7() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();

		mySet1.add(100);

		mySet2.add(100);
		mySet2.add(300);

		assertNotEquals(mySet1, mySet2);
	}

	@Test
	public void testEquality8() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();

		mySet1.add(0);
		mySet1.add(1000);

		mySet2.add(256);
		mySet2.add(1000);

		assertNotEquals(mySet1, mySet2);
	}

	@Test
	public void testEquality9() {
		MySet mySet1 = new MySet();
		MySet mySet2 = new MySet();

		mySet1.add(0);
		mySet1.add(1000);

		mySet2.add(0);
		mySet2.add(256);

		assertNotEquals(mySet1, mySet2);
	}

	// @Test
	// public void testAddTail() {
	// 	MySet mySet1 = new MySet();
	// 	MySet mySet2 = new MySet();
	// 	int bigValue = 32000;

	// 	mySet1.add(100);
	// 	mySet1.add(10000);
	// 	mySet1.add(bigValue);

	// 	mySet2.add(100);
	// 	mySet2.add(10000);

	// 	SmallSet smallSet = new SmallSet();
	// 	smallSet.add(bigValue % 256);
	// 	SubSet subset = new SubSet(bigValue / 256, smallSet);
	// 	mySet2.addTail(subset);
	// 	assertTrue(compareMySets(mySet1, mySet2), "addTail");
	// }

}