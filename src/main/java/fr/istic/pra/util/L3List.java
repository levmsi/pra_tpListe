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
			this.current = flag.right;
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
			this.current = flag.right;
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
			if (isOnFlag()) {
				throw new IllegalStateException("Impossible de supprimer la sentinelle");
			}
			Element left = this.current.left;
			Element right = this.current.right;
			left.right = right;
			right.left = left;
			this.current = right;
		}

		/**
		 * Retourne la valeur de l'élément courant
		 * @return le valeur de l'élément courant
		 */
		@Override
		public T getValue() {
			if (isOnFlag()) {
				throw new java.util.NoSuchElementException();
			}
			return this.current.value;
		}

		/**
		 * Avance le curseur sur l'élément suivant et retourne la valeur de l'élément courant après l'avancement
		 * @return la valeur de l'élément courant après l'avancement
		 */
		@Override
		public T nextValue() {
			goForward();
			return getValue();
		}

		/**
		 * Ajoute un élément à gauche de l'élément courant
		 * 
		 * @param var1 élement à ajouter
		 */
		public void addLeft(T v) {
			Element newElement = new Element();
			newElement.value = v;
			newElement.left = this.current.left;
			newElement.right = this.current;
			this.current.left.right = newElement;
			this.current.left = newElement;
			this.current = newElement;
		}

		/**
		 * Ajoute un élément à droite de l'élément courant
		 * 
		 * @param var1 élement à ajouter
		 */
		public void addRight(T v) {
			Element newElement = new Element();
			newElement.value = v;
			newElement.left = this.current;
			newElement.right = this.current.right;
			this.current.right.left = newElement;
			this.current.right = newElement;
			this.current = newElement;
		}

		/**
		 * initialise la valeur de l'élement courant
		 * 
		 * @param var1 valeur de l'élément à initialiser
		 */
		public void setValue(T v) {
			if (!isOnFlag()) {
				this.current.value = v;
			}
		}
	}

	/**
	 * Créer une nouvelle L3List vide (sentinelle seule)
	 */
	public L3List() {
		this.flag = new Element();
		this.flag.left = this.flag;
		this.flag.right = this.flag;
	}

	/**
	 * Créer un nouvel iterateur sur this
	 * 
	 * @return un nouvel iterateur sur la tête de la liste (ou sur la sentinelle si la liste est vide)
	 */
	public L3Iterator<T> l3Iterator() {
		return new L3ListIterator();
	}

	/**
	 * Vérifie si la liste est vide (c'est à dire si elle ne contient que la sentinelle)
	 * 
	 * @return true si la liste est vide, false sinon
	 */
	public boolean isEmpty() {
		return this.flag.left == this.flag && this.flag.right == this.flag; 
	}

	/**
	 * Déreférence tous les éléments de la liste sauf la sentinelle
	 */
	public void clear() {
		this.flag.left = this.flag;
		this.flag.right = this.flag;
	}

	/**
	 * Ajoute un élement en tête de liste
	 * 
	 * @param v élément à ajouter
	 */
	public void addHead(T v) {
		Element e = new Element();
		e.value = v;
		e.left = flag;
		e.right = flag.right;
		flag.right.left = e;
		flag.right = e;
	}

	/**
	 * Ajoute un élément en queue de liste
	 * 
	 * @param v élément à ajouter
	 */
	public void addTail(T v) {
		Element e = new Element();
		e.value = v;
		e.left = flag.left;
		e.right = flag;
		flag.left.right = e;
		flag.left = e;
	}

	/**
	 * Initialise la valeur du flag
	 * @param v
	 */
	public void setFlag(T v) {
		this.flag.value = v;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		L3Iterator<T> it = l3Iterator();
		while (!it.isOnFlag()) {
			sb.append(it.getValue()).append(" ");
			it.goForward();
		}
		return sb.toString();
	}
}
