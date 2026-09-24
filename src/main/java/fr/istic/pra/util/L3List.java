package fr.istic.pra.util;

/**
 * liste en double chainage par references
 */
public class L3List<T> implements L3Sequence<T>{
	static {
		// Affichage d'un message pour indiquer que votre version de L3List est utilisée
		System.out.println("L3List : version étudiant");
	}

	/**
	 * Sentinelle de la liste
	 */
	private Element flag;

	/**
	 * Un élément d'une liste chainé connait sa valeur son voisin de gauche et son
	 * voisin de droite
	 */
	private class Element {
		public T value;
		public Element left, right;
	}

	/**
	 * Iterateur specifique au L3List
	 */
	private class L3ListIterator implements L3Iterator<T> {
		private Element current;

		/**
		 * Créer un nouvel itérateur positionné sur le premier élément de la liste (ou sur la sentinelle si la liste est vide)
		 */
		private L3ListIterator() {
			this.current = flag;
		}

		/**
		 * Passe au prochain élément de la liste
		 */
		@Override
		public void goForward() {
			this.current = this.current.right;
		}

		/**
		 * Passe a l'élément précédent de la liste
		 */
		@Override
		public void goBackward() {
			this.current = this.current.left;
		}

		/**
		 * Place l'itérateur sur le premier élément de la liste (ou sur la sentinelle si la liste est vide)
		 */
		@Override
		public void restart() {
			this.current = flag;
		}

		/**
		 * Vérifie si l'élément courant est le la sentinelle (flag)
		 * @return true si l'itérateur est sur la sentinelle, false sinon
		 */
		@Override
		public boolean isOnFlag() {
			return this.current == flag;
		}

		/**
		 * enlève un élement de la liste et déplace le curseur vers l'élément suivant
		 */
		@Override
		public void remove() {
			if (!isOnFlag()) {
				Element left = this.current.left;
				Element right = this.current.right;
				left.right = right;
				right.left = left;
				this.current = right;
			}
		}

		/**
		 * Retourne la valeur de l'élément courant
		 * @return le valeur de l'élément courant
		 */
		@Override
		public T getValue() {
			/* TODO: À vous de compléter ! (en attendant, on fait planter) */
			throw new UnsupportedOperationException("À vous de l'implémenter");
		}

		/**
		 * Avance le curseur sur l'élément suivant et retourne la valeur de l'élément courant après l'avancement
		 * @return la valeur de l'élément courant après l'avancement
		 */
		@Override
		public T nextValue() {
			/* TODO: À vous de compléter ! (en attendant, on fait planter) */
			throw new UnsupportedOperationException("À vous de l'implémenter");
		}

		/**
		 * Ajoute un élément à gauche de l'élément courant
		 * 
		 * @param var1 élement à ajouter
		 */
		public void addLeft(T v) {
			/* TODO: À vous de compléter ! (en attendant, on fait planter) */
			throw new UnsupportedOperationException("À vous de l'implémenter");
		}

		/**
		 * Ajoute un élément à droite de l'élément courant
		 * 
		 * @param var1 élement à ajouter
		 */
		public void addRight(T v) {
			/* TODO: À vous de compléter ! (en attendant, on fait planter) */
			throw new UnsupportedOperationException("À vous de l'implémenter");
		}

		/**
		 * initialise la valeur de l'élement courant
		 * 
		 * @param var1 valeur de l'élément à initialiser
		 */
		public void setValue(T v) {
			/* TODO: À vous de compléter ! (en attendant, on fait planter) */
			throw new UnsupportedOperationException("À vous de l'implémenter");
		}
	}

	/**
	 * Créer une nouvelle L3List vide (sentinelle seule)
	 */
	public L3List() {
		/* TODO: À vous de compléter ! */
	}

	/**
	 * Créer un nouvel iterateur sur this
	 * 
	 * @return un nouvel iterateur sur la tête de la liste (ou sur la sentinelle si la liste est vide)
	 */
	public L3Iterator<T> l3Iterator() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Vérifie si la liste est vide (c'est à dire si elle ne contient que la sentinelle)
	 * 
	 * @return true si la liste est vide, false sinon
	 */
	public boolean isEmpty() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Déreférence tous les éléments de la liste sauf la sentinelle
	 */
	public void clear() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Ajoute un élement en tête de liste
	 * 
	 * @param v élément à ajouter
	 */
	public void addHead(T v) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Ajoute un élément en queue de liste
	 * 
	 * @param v élément à ajouter
	 */
	public void addTail(T v) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	/**
	 * Initialise la valeur du flag
	 * @param v
	 */
	public void setFlag(T v) {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}

	@Override
	public String toString() {
		/* TODO: À vous de compléter ! (en attendant, on fait planter) */
		throw new UnsupportedOperationException("À vous de l'implémenter");
	}
}
