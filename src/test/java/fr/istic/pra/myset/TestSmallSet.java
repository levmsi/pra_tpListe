package fr.istic.pra.myset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.istic.pra.myset.SmallSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests du contrat de {@link SmallSet}.
 *
 * <p>Les mêmes tests sont exécutés à la fois contre l'implémentation fournie
 * (profils par défaut, {@code BitSet}) et contre l'implémentation de référence
 * (profil {@code -Pshadow}, {@code boolean[]}) : ils ne doivent dépendre que du
 * contrat public, jamais d'un détail d'implémentation.</p>
 */
public class TestSmallSet {

	private static List<Integer> collect(Iterator<Integer> it) {
		List<Integer> out = new ArrayList<>();
		it.forEachRemaining(out::add);
		return out;
	}

	@Test
	public void testInitiallyEmpty() {
		SmallSet s = new SmallSet();
		assertTrue(s.isEmpty());
		assertEquals(0, s.size());
	}

	@Test
	public void testAddContains() {
		SmallSet s = new SmallSet();
		s.add(5);
		s.add(255);
		assertTrue(s.contains(5));
		assertTrue(s.contains(255));
		assertEquals(2, s.size());
	}

	@Test
	public void testAddIdempotent() {
		SmallSet s = new SmallSet();
		s.add(7);
		s.add(7);
		s.add(7);
		assertEquals(1, s.size(), "ajouter plusieurs fois la même valeur ne doit compter qu'une fois");
	}

	@Test
	public void testRemove() {
		SmallSet s = new SmallSet();
		s.add(3);
		s.add(4);
		s.remove(3);
		assertFalse(s.contains(3));
		assertTrue(s.contains(4));
		assertEquals(1, s.size());
		s.remove(4);
		assertTrue(s.isEmpty());
	}

	@Test
	public void testClear() {
		SmallSet s = new SmallSet();
		s.add(1);
		s.add(2);
		s.clear();
		assertTrue(s.isEmpty());
		assertEquals(0, s.size());
	}

	@Test
	public void testOutOfRange() {
		SmallSet s = new SmallSet();
		assertFalse(s.contains(-1));
		assertFalse(s.contains(SmallSet.SET_SIZE));
		s.add(-1);
		s.add(SmallSet.SET_SIZE);
		assertFalse(s.contains(-1), "les valeurs hors domaine ne doivent pas être ajoutées");
		assertFalse(s.contains(SmallSet.SET_SIZE));
		assertEquals(0, s.size());
	}

	@Test
	public void testAddInterval() {
		SmallSet s = new SmallSet();
		s.addInterval(10, 12);
		assertTrue(s.contains(10));
		assertTrue(s.contains(11));
		assertTrue(s.contains(12));
		assertFalse(s.contains(9));
		assertFalse(s.contains(13));
		assertEquals(3, s.size());
	}

	@Test
	public void testRemoveInterval() {
		SmallSet s = new SmallSet();
		s.addInterval(0, 20);
		s.removeInterval(5, 9);
		assertFalse(s.contains(5));
		assertFalse(s.contains(9));
		assertTrue(s.contains(4));
		assertTrue(s.contains(10));
	}

	@Test
	public void testUnion() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(1);
		a.add(2);
		b.add(2);
		b.add(3);
		a.union(b);
		assertTrue(a.contains(1));
		assertTrue(a.contains(2));
		assertTrue(a.contains(3));
		assertEquals(3, a.size());
	}

	@Test
	public void testIntersection() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(1);
		a.add(2);
		b.add(2);
		b.add(3);
		a.intersection(b);
		assertTrue(a.contains(2));
		assertFalse(a.contains(1));
		assertFalse(a.contains(3));
		assertEquals(1, a.size());
	}

	@Test
	public void testDifference() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(1);
		a.add(2);
		b.add(2);
		b.add(3);
		a.difference(b);
		assertTrue(a.contains(1));
		assertFalse(a.contains(2));
		assertEquals(1, a.size());
	}

	@Test
	public void testSymmetricDifference() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(1);
		a.add(2);
		b.add(2);
		b.add(3);
		a.symmetricDifference(b);
		assertTrue(a.contains(1));
		assertTrue(a.contains(3));
		assertFalse(a.contains(2));
		assertEquals(2, a.size());
	}

	@Test
	public void testComplement() {
		SmallSet s = new SmallSet();
		s.addInterval(0, SmallSet.SET_SIZE - 1);
		s.complement();
		assertTrue(s.isEmpty(), "complément de l'univers entier = vide");
		s.complement();
		assertEquals(SmallSet.SET_SIZE, s.size(), "complément de l'ensemble vide = univers");
		assertEquals(List.of(0, 1, 2), collect(s.iterator()).subList(0, 3));
	}

	@Test
	public void testIsIncludedIn() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(1);
		a.add(2);
		b.add(1);
		b.add(2);
		b.add(3);
		assertTrue(a.isIncludedIn(b));
		assertFalse(b.isIncludedIn(a));
		assertTrue(new SmallSet().isIncludedIn(a), "l'ensemble vide est inclus dans tout");
	}

	@Test
	public void testEqualsIgnoresInsertionOrder() {
		SmallSet a = new SmallSet();
		SmallSet b = new SmallSet();
		a.add(4);
		a.add(5);
		b.add(5);
		b.add(4);
		assertTrue(a.equals(b), "l'égalité ne doit pas dépendre de l'ordre d'ajout");
		b.add(6);
		assertFalse(a.equals(b));
	}

	@Test
	public void testCopyIsolation() {
		SmallSet a = new SmallSet();
		a.add(1);
		SmallSet b = (SmallSet) a.copy();
		assertTrue(b.contains(1));
		b.add(2);
		b.remove(1);
		assertTrue(a.contains(1), "la copie ne doit pas partager l'état de l'original");
		assertFalse(a.contains(2));
		assertEquals(1, a.size());
	}

	@Test
	public void testIteratorSorted() {
		SmallSet s = new SmallSet();
		s.add(9);
		s.add(2);
		s.add(7);
		s.add(2);
		assertEquals(List.of(2, 7, 9), collect(s.iterator()), "l'itérateur produit des valeurs triées et uniques");
	}
}
