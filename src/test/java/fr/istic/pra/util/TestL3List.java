package fr.istic.pra.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.istic.pra.util.L3Iterator;
import fr.istic.pra.util.L3List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests du contrat de {@link L3List} et de son {@link L3Iterator}.
 *
 * <p>Les mêmes tests sont exécutés à la fois contre l'adaptateur fourni
 * (profil par défaut) et contre la liste à sentinelle de référence
 * (profil {@code -Pshadow}) : ils ne doivent dépendre que du contrat de
 * {@code L3Sequence} / {@code L3Iterator}, jamais d'un détail d'implémentation.</p>
 */
public class TestL3List {

	private static <T> List<T> collect(L3Iterator<T> it) {
		List<T> out = new ArrayList<>();
		while (!it.isOnFlag()) {
			out.add(it.getValue());
			it.goForward();
		}
		return out;
	}

	@Test
	public void testEmpty() {
		L3List<Integer> list = new L3List<>();
		assertTrue(list.isEmpty());
		L3Iterator<Integer> it = list.l3Iterator();
		assertTrue(it.isOnFlag(), "un itérateur sur liste vide doit être sur le flag");
		list.clear();
		assertTrue(list.isEmpty());
	}

	@Test
	public void testAddTailOrder() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		list.addTail(3);
		assertEquals(List.of(1, 2, 3), collect(list.l3Iterator()));
	}

	@Test
	public void testAddHeadOrder() {
		L3List<Integer> list = new L3List<>();
		list.addHead(1);
		list.addHead(2);
		list.addHead(3);
		assertEquals(List.of(3, 2, 1), collect(list.l3Iterator()), "addHead empile en tête");
	}

	@Test
	public void testGoForwardReachesFlagThenWraps() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		list.addTail(20);
		L3Iterator<Integer> it = list.l3Iterator();
		assertEquals(10, it.getValue());
		it.goForward();
		assertEquals(20, it.getValue());
		it.goForward();
		assertTrue(it.isOnFlag(), "après le dernier élément on est sur le flag");
		it.goForward();
		assertFalse(it.isOnFlag(), "avancer depuis le flag mène au premier élément");
		assertEquals(10, it.getValue());
	}

	@Test
	public void testGoBackward() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		list.addTail(20);
		list.addTail(30);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goBackward();
		assertTrue(it.isOnFlag(), "reculer depuis le premier élément atteint le flag");
		it.goBackward();
		assertFalse(it.isOnFlag(), "reculer depuis le flag mène au dernier élément");
		assertEquals(30, it.getValue());
		it.goBackward();
		assertEquals(20, it.getValue());
		it.goBackward();
		assertEquals(10, it.getValue());
	}

	@Test
	public void testRestart() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward();
		it.goForward();
		assertTrue(it.isOnFlag());
		it.restart();
		assertEquals(1, it.getValue(), "restart replace sur le premier élément");
	}

	@Test
	public void testNextValue() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		list.addTail(3);
		L3Iterator<Integer> it = list.l3Iterator();
		assertEquals(1, it.getValue());
		assertEquals(2, it.nextValue(), "nextValue passe à l'élément suivant");
		assertEquals(3, it.nextValue());
		it.goForward();
		assertTrue(it.isOnFlag());
	}

	@Test
	public void testAddLeft() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		list.addTail(30);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur 30
		it.addLeft(20);
		assertEquals(20, it.getValue(), "addLeft pose le curseur sur l'élément inséré");
		assertEquals(List.of(10, 20, 30), collect(list.l3Iterator()));
	}

	@Test
	public void testAddLeftAtHead() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		list.addTail(20);
		L3Iterator<Integer> it = list.l3Iterator(); // sur 10
		it.addLeft(0);
		assertEquals(List.of(0, 10, 20), collect(list.l3Iterator()));
	}

	@Test
	public void testAddRight() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		list.addTail(30);
		L3Iterator<Integer> it = list.l3Iterator(); // sur 10
		it.addRight(20);
		assertEquals(20, it.getValue(), "addRight pose le curseur sur l'élément inséré");
		assertEquals(List.of(10, 20, 30), collect(list.l3Iterator()));
	}

	@Test
	public void testAddLeftOnFlagAppends() {
		L3List<Integer> list = new L3List<>();
		list.addTail(10);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur le flag
		it.addLeft(20);
		assertEquals(20, it.getValue(), "addLeft sur le flag insère en queue");
		assertEquals(List.of(10, 20), collect(list.l3Iterator()));
	}

	@Test
	public void testRemove() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		list.addTail(3);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur 2
		it.remove();
		assertEquals(3, it.getValue(), "remove place le curseur sur l'élément suivant");
		assertEquals(List.of(1, 3), collect(list.l3Iterator()));
	}

	@Test
	public void testRemoveLast() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur 2
		it.remove();
		assertTrue(it.isOnFlag(), "retirer le dernier élément place sur le flag");
		assertEquals(List.of(1), collect(list.l3Iterator()));
	}

	@Test
	public void testRemoveOnlyElement() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		L3Iterator<Integer> it = list.l3Iterator();
		it.remove();
		assertTrue(list.isEmpty());
		assertTrue(it.isOnFlag());
	}

	@Test
	public void testRemoveFlagThrows() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur le flag
		assertThrows(IllegalStateException.class, it::remove,
				"retirer la sentinelle doit lever une exception IllegalStateException");
	}

	@Test
	public void testRemoveFlagOnEmptyThrows() {
		L3List<Integer> list = new L3List<>();
		assertThrows(IllegalStateException.class, list.l3Iterator()::remove,
				"retirer la sentinelle d'une liste vide doit lever une exception IllegalStateException");
	}

	@Test
	public void testSetValue() {
		L3List<Integer> list = new L3List<>();
		list.addTail(1);
		list.addTail(2);
		L3Iterator<Integer> it = list.l3Iterator();
		it.goForward(); // sur 2
		it.setValue(20);
		assertEquals(List.of(1, 20), collect(list.l3Iterator()));
	}

	@Test
	public void testMixedInsertRemove() {
		L3List<Integer> list = new L3List<>();
		L3Iterator<Integer> it = list.l3Iterator();
		it.addLeft(2);   // [2]
		it.addLeft(1);   // [1,2]
		it.addRight(3);  // [1,3,2]
		assertEquals(List.of(1, 3, 2), collect(list.l3Iterator()));
		it = list.l3Iterator(); // sur 1
		it.remove();            // [3,2]
		assertEquals(List.of(3, 2), collect(list.l3Iterator()));
	}
}
