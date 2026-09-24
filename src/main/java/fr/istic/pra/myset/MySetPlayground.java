package fr.istic.pra.myset;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.istic.pra.util.EnsLoader;

/**
 * Application graphique de test manuel de {@link MySet}.
 * <p>
 * Moitié haute : vue des ensembles. Moitié basse : une ligne par opération
 * (selecteurs d'opérandes + champs de saisie). Les opérations qui renvoient
 * un résultat (contains, size, equals, isIncludedIn) affichent ce dernier.
 */
public class MySetPlayground extends JFrame {

	private static final int SETS_COUNT = 4;
	private static final String FONT = "Verdana";

	private final java.util.List<MySet> sets = new ArrayList<>();

	private final JTextArea setsArea = new JTextArea();
	private final JLabel resultLabel = new JLabel("Résultat : ---");

	private final JComboBox<Integer> addDest = setSelector();
	private final JTextField addValues = new JTextField(12);

	private final JComboBox<Integer> removeDest = setSelector();
	private final JTextField removeValues = new JTextField(12);

	private final JComboBox<Integer> clearDest = setSelector();

	private final JComboBox<Integer> containsDest = setSelector();
	private final JTextField containsValues = new JTextField(12);

	private final JComboBox<Integer> sizeDest = setSelector();

	private final JComboBox<Integer> unionDest = setSelector();
	private final JComboBox<Integer> unionOther = setSelector();

	private final JComboBox<Integer> interDest = setSelector();
	private final JComboBox<Integer> interOther = setSelector();

	private final JComboBox<Integer> diffDest = setSelector();
	private final JComboBox<Integer> diffOther = setSelector();

	private final JComboBox<Integer> symdiffDest = setSelector();
	private final JComboBox<Integer> symdiffOther = setSelector();

	private final JComboBox<Integer> includeN1 = setSelector();
	private final JComboBox<Integer> includeN2 = setSelector();

	private final JComboBox<Integer> equalsN1 = setSelector();
	private final JComboBox<Integer> equalsN2 = setSelector();

	private final JComboBox<Integer> loadDest = setSelector();
	private final JComboBox<String> loadSource =
			new JComboBox<>(EnsLoader.sampleNames().toArray(new String[0]));

	private MySetPlayground() {
		super("MySetPlayground - Test manuel de MySet");
		for (int i = 0; i < SETS_COUNT; ++i) {
			sets.add(new MySet());
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(buildSetsPanel(), BorderLayout.CENTER);
		add(buildOperationsPanel(), BorderLayout.SOUTH);
		pack();
	}

	// ------------------------------------------------------------------
	//  Moitié haute : vue des ensembles
	// ------------------------------------------------------------------
	private JPanel buildSetsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Ensembles"));

		setsArea.setEditable(false);
		setsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		refreshSets();
		panel.add(new JScrollPane(setsArea), BorderLayout.CENTER);
		return panel;
	}

	private void refreshSets() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < SETS_COUNT; ++i) {
			MySet s = sets.get(i);
			sb.append("Ensemble ").append(i);
			try {
				sb.append(" (").append(s.size()).append(" élément(s)) : ");
				sb.append(s.isEmpty() ? "∅" : s).append('\n');
			} catch (Exception e) {
				sb.append(" (taille inconnue) : ").append(s).append('\n');
			}
			sb.append("   Détails : ").append(s.printMySetDetails()).append('\n');
			if (i < SETS_COUNT - 1) {
				sb.append('\n');
			}
		}
		setsArea.setText(sb.toString());
	}

	// ------------------------------------------------------------------
	//  Moitié basse : une ligne par opération
	// ------------------------------------------------------------------
	private JPanel buildOperationsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Opérations"));

		panel.add(row(new JLabel("Ajouter"),
				new JLabel("dans l'ensemble"), addDest,
				new JLabel("valeurs"), addValues,
				button("Ajouter", this::doAdd)));

		panel.add(row(new JLabel("Retirer"),
				new JLabel("de l'ensemble"), removeDest,
				new JLabel("valeurs"), removeValues,
				button("Retirer", this::doRemove)));

		panel.add(row(new JLabel("Vider l'ensemble"), clearDest,
				button("Vider", this::doClear)));

		panel.add(row(new JLabel("Contient"),
				new JLabel("l'ensemble"), containsDest,
				new JLabel("valeur(s)"), containsValues,
				button("Tester", this::doContains)));

		panel.add(row(new JLabel("Taille de l'ensemble"), sizeDest,
				button("Calculer", this::doSize)));

		panel.add(row(new JLabel("Union"),
				new JLabel("ensemble"), unionDest,
				new JLabel("avec"), unionOther,
				button("Union", this::doUnion)));



		panel.add(row(new JLabel("Diff. symétrique"),
				new JLabel("ensemble"), symdiffDest,
				new JLabel("avec"), symdiffOther,
				button("SymDiff", this::doSymDifference)));


		panel.add(row(new JLabel("Égalité"),
				new JLabel("ensemble"), equalsN1,
				new JLabel("et"), equalsN2,
				button("Tester", this::doEquals)));

		panel.add(row(new JLabel("Charger"),
				new JLabel("dans l'ensemble"), loadDest,
				new JLabel("fichier"), loadSource,
				button("Charger", this::doLoad)));

		resultLabel.setFont(new Font(FONT, Font.BOLD, 14));
		JPanel resultRow = new JPanel();
		resultRow.setLayout(new BoxLayout(resultRow, BoxLayout.LINE_AXIS));
		resultRow.add(Box.createHorizontalStrut(8));
		resultRow.add(resultLabel);
		panel.add(resultRow);

		return panel;
	}

	private JPanel row(javax.swing.JComponent... comps) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(Box.createHorizontalStrut(8));
		for (javax.swing.JComponent c : comps) {
			p.add(c);
			p.add(Box.createHorizontalStrut(6));
		}
		return p;
	}

	private JButton button(String text, Runnable action) {
		JButton b = new JButton(text);
		b.addActionListener(e -> action.run());
		return b;
	}

	// ------------------------------------------------------------------
	//  Opérations
	// ------------------------------------------------------------------
	private void doAdd() {
		MySet s = sets.get(addDest.getSelectedIndex());
		List<Integer> vals = parseSet(addValues);
		vals.forEach(s::add);
		showResult("Ajout de " + vals.size() + " valeur(s) dans l'ensemble "
				+ addDest.getSelectedIndex());
	}

	private void doRemove() {
		MySet s = sets.get(removeDest.getSelectedIndex());
		List<Integer> vals = parseSet(removeValues);
		vals.forEach(s::remove);
		showResult("Retrait de " + vals.size() + " valeur(s) de l'ensemble "
				+ removeDest.getSelectedIndex());
	}

	private void doClear() {
		int idx = clearDest.getSelectedIndex();
		sets.get(idx).clear();
		showResult("Ensemble " + idx + " vidé");
	}

	private void doContains() {
		int idx = containsDest.getSelectedIndex();
		MySet s = sets.get(idx);
		List<Integer> vals = parseSet(containsValues);
		StringBuilder sb = new StringBuilder("Ensemble " + idx + " : ");
		if (vals.isEmpty()) {
			sb.append("aucune valeur saisie");
		} else {
			for (int v : vals) {
				sb.append(v).append(s.contains(v) ? " présent, " : " absent, ");
			}
			sb.setLength(sb.length() - 2);
		}
		showResult(sb.toString());
	}

	private void doSize() {
		int idx = sizeDest.getSelectedIndex();
		showResult("Cardinal de l'ensemble " + idx + " : " + sets.get(idx).size());
	}

	private void doUnion() {
		sets.get(unionDest.getSelectedIndex()).union(sets.get(unionOther.getSelectedIndex()));
		showResult("Union faite : ensemble " + unionDest.getSelectedIndex()
				+ " <- " + unionDest.getSelectedIndex() + " ∪ " + unionOther.getSelectedIndex());
	}

	private void doIntersection() {
		sets.get(interDest.getSelectedIndex()).intersection(sets.get(interOther.getSelectedIndex()));
		showResult("Intersection faite : ensemble " + interDest.getSelectedIndex()
				+ " <- " + interDest.getSelectedIndex() + " ∩ " + interOther.getSelectedIndex());
	}

	private void doDifference() {
		sets.get(diffDest.getSelectedIndex()).difference(sets.get(diffOther.getSelectedIndex()));
		showResult("Différence faite : ensemble " + diffDest.getSelectedIndex()
				+ " <- " + diffDest.getSelectedIndex() + " \\ " + diffOther.getSelectedIndex());
	}

	private void doSymDifference() {
		sets.get(symdiffDest.getSelectedIndex()).symmetricDifference(sets.get(symdiffOther.getSelectedIndex()));
		showResult("Différence symétrique faite : ensemble " + symdiffDest.getSelectedIndex()
				+ " <- " + symdiffDest.getSelectedIndex() + " Δ " + symdiffOther.getSelectedIndex());
	}

	private void doInclude() {
		int n1 = includeN1.getSelectedIndex();
		int n2 = includeN2.getSelectedIndex();
		boolean b = sets.get(n1).isIncludedIn(sets.get(n2));
		showResult("Ensemble " + n1 + (b ? " EST inclus" : " n'est PAS inclus") + " dans l'ensemble " + n2);
	}

	private void doEquals() {
		int n1 = equalsN1.getSelectedIndex();
		int n2 = equalsN2.getSelectedIndex();
		boolean b = sets.get(n1).equals(sets.get(n2));
		showResult("Ensemble " + n1 + (b ? " EST égal" : " n'est PAS égal") + " à l'ensemble " + n2);
	}

	private void doLoad() {
		int idx = loadDest.getSelectedIndex();
		String file = (String) loadSource.getSelectedItem();
		EnsLoader.readInto(file, sets.get(idx));
		showResult("Ensemble " + idx + " chargé depuis " + file);
	}

	// ------------------------------------------------------------------
	//  Outils
	// ------------------------------------------------------------------
	private void showResult(String message) {
		resultLabel.setText("Résultat : " + message);
		refreshSets();
	}

	private static JComboBox<Integer> setSelector() {
		Integer[] options = new Integer[SETS_COUNT];
		for (int i = 0; i < SETS_COUNT; ++i) {
			options[i] = i;
		}
		return new JComboBox<>(options);
	}

	private static List<Integer> parseSet(JTextField field) {
		List<Integer> out = new ArrayList<>();
		try (Scanner sc = new Scanner(field.getText())) {
			while (sc.hasNextInt()) {
				out.add(sc.nextInt());
			}
		}
		return out;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new MySetPlayground().setVisible(true));
	}
}
