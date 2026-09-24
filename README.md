# PRA TP : Listes et ensembles - MySet

Ce TP est enseigné dans le cadre du cours de **PR**ogrammation **A**vancé en troisième année de licence à l'ISTIC (L3 Info).  
Le sujet du TP est disponible [ici](./pra-tp-listes.pdf) et sur la page Moodle du cours.

Vous devez compléter la classe `fr.istic.pra.myset.MySet` : implémentez les méthodes
marquées `TODO`. La bibliothèque `lib-tp-listes` vous fournit déjà les briques de base
(`L3Set`, `L3List`, `L3Iterator`, `SmallSet`, `SubSet`...).
Ces classes sont disponibles via le dépôt local `lib/` (dépendance déclarée dans `pom.xml`).

## Récupération du projet

Pour récupérer le projet il suffit de le cloner sur votre machine avec la commande :
``` bash
git clone https://gitlab2.istic.univ-rennes1.fr/pra/tp-listes.git
```
Puis de l'ouvrir avec l'IDE de votre choix :
- **VSCode** (recommandé): File > Open Folder... > [*votre dossier de travail*]/tp_list
- **Eclipse**: File > Import... > Maven > Existing Maven Projects > Browse... > [*votre dossier de travail*]/tp_list > Finish

## Utilisation du projet / Interface graphique

### Manipulation des ensembles (test manuel)

Vous disposez d'une interface graphique pour manipuler les structures de données que vous
développerez dans ce projet. Celle-ci permet notamment de tester manuellement les
opérations de `MySet` (saisie des opérandes, affichage des ensembles, etc.).

Pour la lancer, vous pouvez :
- **Depuis un IDE** : ouvrir la classe [MySetPlayground.java](./src/main/java/fr/istic/pra/myset/MySetPlayground.java)
  puis cliquer sur "run" au-dessus du `main` (VSCode) ou `Run As > Java Application`
  (Eclipse) ;
- **En ligne de commande Maven** :
  ``` bash
  mvn exec:java -Dexec.mainClass=fr.istic.pra.myset.MySetPlayground
  ```

### Benchmarking

Vous disposez également d'une application de benchmark pour mesurer les performances de vos
implémentations. Celle-ci permet notamment de générer des courbes de performances avec XChart

Pour la lancer, vous pouvez :
- **Depuis un IDE** : ouvrir la classe [BenchmarkApp.java](./src/main/java/fr/istic/pra/myset/BenchmarkApp.java)
  puis cliquer sur "run" au-dessus du `main` (VSCode) ou `Run As > Java Application`
  (Eclipse) ;
- **En ligne de commande Maven** :
  ``` bash
  mvn exec:java -Dexec.mainClass=fr.istic.pra.myset.BenchmarkApp
  ```

## Implémentation attendue

- `MySet` représente un ensemble d'entiers « creux » : une liste de sous-ensembles
  (`SubSet`) triée par rang croissant, chaque `SubSet` contenant un `SmallSet` sur 256 valeurs.
- Les méthodes déjà présentes vous guident sur la navigation (`L3Iterator`) et la gestion
  de la sentinelle (flag).
- Vous devez aussi (ré)implémenter par vous-même deux briques de base :

  - `fr.istic.pra.util.L3List` : une liste doublement chaînée à **sentinelle** (flag),
    telle que vue en TD (implémentation de `L3Sequence`, parcours par des `L3Iterator`);
  - `fr.istic.pra.myset.SmallSet` : un ensemble de 256 valeurs représenté par un **tableau de booléens**.

  Ces deux classes sont fournies en squelettes sous `src/main/java` **en `.txt`** :
  renommez-les en `.java` (dans leur dossier) pour les activer. Elles porteront alors le
  même nom pleinement qualifié que celles fournies dans `lib/` et les **shadowent** : ce
  sont vos implémentations qui seront exécutées (projet avant dépendance).
  Tant qu'elles restent en `.txt`, le projet compilera et tournera avec les versions
  fournies de `lib-tp-listes`.

## Vérifier votre travail

Les tests unitaires couvrent les opérations demandées. Lancez-les avec :

``` bash
mvn test
```

Pour lancer l'application graphique de test manuel :

``` bash
mvn exec:java -Dexec.mainClass=fr.istic.pra.myset.MySetPlayground
```

Pour lancer l'application de benchmark (courbes XChart) :

``` bash
mvn exec:java -Dexec.mainClass=fr.istic.pra.myset.BenchmarkApp
```
# pra_tpListe
