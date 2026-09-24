package fr.istic.pra.myset;

import fr.istic.pra.util.L3Set;
import fr.istic.pra.util.L3Iterator;
import fr.istic.pra.util.L3List;
import java.util.Iterator;

public class MySet implements L3Set<Integer> {

	/**
	 * Majorant pour les rangs des sous-ensembles.
	 */
	public static final int MAX_RANG = Integer.MAX_VALUE;

	/**
	 * Liste des sous-ensembles, triée par rang croissant.
	 */
	private L3List<SubSet> list;

	/**
	 * Constructeur par défaut, crée un ensemble vide.
	 * @implNote La liste est initialisée avec une sentinelle (flag) dont le rang est un majorant de tous les rangs possibles.
	 */
	public MySet() {
		this.list = new L3List<>();
		this.list.setFlag(new SubSet(MAX_RANG, new SmallSet()));
	}

	/**
	 * Constructeur de copie
	 * @param other l'ensemble à copier
	 */
	public MySet(MySet other) {
		this.list = new L3List<>();
		this.list.setFlag(new SubSet(MAX_RANG, new SmallSet()));
		L3Iterator<SubSet> it = other.list.l3Iterator();
		while (!it.isOnFlag()) {
			this.list.addTail(new SubSet(it.getValue()));
			it.goForward();
		}
	}

	/**
	 * Fabrique une copie de this.
	 * @return une copie de l'ensemble this.
	 */
	@Override
	public MySet copy() {
		return new MySet(this);
	}

	/**
	 * Fabrique un itérateur Java sur les éléments de this.
	 * @return un itérateur sur les éléments de this.
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new MySetIterator();
	}

	/**
	 * Itérateur pour parcourir les éléments d'un MySet.
	 */
	private class MySetIterator implements Iterator<Integer> {
		private L3Iterator<SubSet> it;
		private Iterator<Integer> currentSmallSetIterator;

		public MySetIterator() {
			this.it = list.l3Iterator();
			this.currentSmallSetIterator = null;
			if (!it.isOnFlag()) {
				currentSmallSetIterator = it.getValue().set.iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return !it.isOnFlag();
		}

		@Override
		public Integer next() {
			if (currentSmallSetIterator == null || it.isOnFlag()) {
				throw new java.util.NoSuchElementException();
			}
			int currentIndex = currentSmallSetIterator.next();
			int value = it.getValue().rank * SmallSet.SET_SIZE + currentIndex;
			if (!currentSmallSetIterator.hasNext()) {
				it.goForward();
				if (!it.isOnFlag()) {
					currentSmallSetIterator = it.getValue().set.iterator();
				} else {
					currentSmallSetIterator = null;
				}
			}
			return value;
		}
	}

	// -------------------------------------------------------------------------- //
	// --------------- Appartenance, Ajout, Suppression, Cardinal --------------- //
	// -------------------------------------------------------------------------- //


	/**
	 * @param value valeur à tester
	 * @return true si valeur appartient à l'ensemble, false sinon
	 */
	@Override
	public boolean contains(Object value) {
		if (!(value instanceof Integer)) {
			return false;
		}
		Integer intValue = (Integer) value;
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		return false;
	}

	/**
	 * Ajouter element à this,
	 *
	 * @param value valuer à ajouter.
	 */
	@Override
	public void add(Integer value) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Supprimer element de this.
	 * 
	 * @param element valeur à supprimer
	 */
	@Override
	public void remove(Integer value) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Vide l'ensemble this.
	 */
	@Override
	public void clear() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Retourne le nombre d'éléments dans l'ensemble this.
	 * @implSpec Cette méthode devrait normalement être implémentée en temps constant.
	 * @implNote Dans un premier temps, vous pouvez implémenter cette méthode en temps linéaire.
	 * @implNote Dans un second temps, vous pouvez implémenter cette méthode en temps constant en maintenant un compteur d'éléments dans l'ensemble (bonus).
	 * @apiNote Cette méthode est utilisée pour l'implémentation de isEmpty() dans l'interface L3Set.
	 * @return nombre de valeur dans l'ensemble this
	 */
	@Override
	public int size() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	// -------------------------------------------------------------------------- //
	// --------- Opérations ensemblistes avec modification en place ------------- //
	// -------------------------------------------------------------------------- //


	/**
	 * This devient la différence symétrique de this et set2.
	 * 
	 * @param otherSet deuxième ensemble
	 */
	@Override
	public void symmetricDifference(L3Set<Integer> otherSet) {
		if (otherSet instanceof MySet mySet2) {
			this.symmetricDifference(mySet2);
		} else {
			L3Set.super.symmetricDifference(otherSet);
		}
	}

	/**
	 * This devient la différence symétrique de this et set2.
	 * Version optimisée pour MySet.
	 * 
	 * @param otherSet deuxième ensemble
	 */
	public void symmetricDifference(MySet otherSet) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}


	/**
	 * This devient l'union de this et set2.
	 * 
	 * @param otherSet deuxième ensemble
	 */
	@Override
	public void union(L3Set<Integer> otherSet) {
		if (otherSet instanceof MySet mySet2) {
			this.union(mySet2);
		} else {
			L3Set.super.union(otherSet);
		}
	}

	/**
	 * This devient l'union de this et set2.
	 * Version optimisée pour MySet.
	 * 
	 * @param otherSet deuxième ensemble
	 */
	public void union(MySet otherSet) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	// ---------------------------------------------------------------------------- //
	// ---------------------- Egalite, Inclusion, toString ------------------------ //
	// ---------------------------------------------------------------------------- //


	
	/**
	 * Détermine si l'objet spécifié est égal à l'objet actuel.
	 * @param o deuxième ensemble
	 * @return true si les ensembles this et o sont égaux, false sinon
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof MySet set2) {
			return this.equals(set2);
		} else {
			return L3Set.equals(this, o);

		}
	}

	/**
	 * Détermine si l'ensemble this est égal à l'ensemble otherSet.
	 * Version optimisée pour MySet.
	 * @param otherSet deuxième ensemble
	 * @return true si les ensembles this et otherSet sont égaux, false sinon
	 */
	public boolean equals(MySet otherSet) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Retourne une représentation sous forme de chaîne de caractères de l'ensemble this.
	 * @return chaîne de caractères représentant les éléments de l'ensemble.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		L3Iterator<SubSet> it = this.list.l3Iterator();
		while (!it.isOnFlag()) {
			for (Integer elem : it.getValue().set) {
				result.append(it.getValue().rank * SmallSet.SET_SIZE + elem);
				result.append(" ");
			}
			it.goForward();
		}
		return result.toString();
	}


	/**
	 * Retourne une représentation détaillée de l'ensemble this.
	 * @return chaîne de caractères représentant les sous-ensembles et leurs rangs.
	 */
	public String printMySetDetails() {
		StringBuilder result = new StringBuilder();
		L3Iterator<SubSet> it = this.list.l3Iterator();
		result.append("| ");
		while (!it.isOnFlag()) {
			result.append( it.getValue().rank + "->");
			result.append(it.getValue().set.toString() + " | ");
			it.goForward();
		}
		return result.toString();
	}
}
