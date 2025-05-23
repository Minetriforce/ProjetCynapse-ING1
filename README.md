

# CYNAPSE Groupe 7
## Sommaire
<img src="https://upload.wikimedia.org/wikipedia/commons/f/f8/CY_Tech.svg" align="right"
     alt="Size Limit logo by Anton Lovchikov" width="170">
* [Présentation](#présentation) : Présentation du projet et de ses membres
* [Pré-requis](#pré-requis) : Installation des dépendances
* [Outils utilisés](#outils-utilisés) : Outils qui ont servi au développement du projet
* [Fonctionnalités principales](#fonctionnalités-principales) : Liste des fonctionnalités
* [Lancer l'application](#lancer-lapplication) : Instructions pour lancer la version graphique ou terminale
* [Javadoc](#javadoc) : Générer la javadoc


## Présentation

**CYNAPSE** est le projet de développement logiciel de l'année 2024-2025 de CY Tech réalisé par : 
- Bari-Joris BOICOS
- Lorelle WENG
- Florianne PAN
- Jonathan NGO
- Junjie SHAO

Cette application JavaFX permet de générer, modifier, charger, sauvegarder et résoudre des labyrinthes via une interface graphique interactive. Elle propose plusieurs méthodes de génération et de résolution ainsi que la possibilité d’éditer les points de départ et d’arrivée et de modifier manuellement les murs. 




## Pré-requis

Installer **Maven** avec : 
```shell
sudo apt install mvn
```

Version de **Java**: **21.0.3** et version de **JavaFX** : **21.0**
```
sudo apt install openjdk-21-jdk
```

## Outils utilisés
- **Java** - langage de programmation (back-end)
- **JavaFX** - framework
- **VSCode** et **IntelliJ IDEA** - éditeur de code <br></br>


## Fonctionnalités principales

### Génération de labyrinthe
Remplissez les cases pour les dimensions et la graine aléatoire, choisissez une méthode de génération, puis cliquez sur **Generate**. Un mode pas à pas avec un délai en millisecondes est également disponible.

### Résolution de labyrinthe
La résolution de labyrinthe n’est accessible qu’une fois qu’un labyrinthe a été généré ou chargé. Sélectionnez une méthode de résolution, activez le mode pas à pas si désiré et cliquez sur **Solve** pour lancer la résolution animée. Les statistiques telles que la méthode utilisée, la longueur du chemin, le nombre de cases visitées et le temps sont affichées dans le panneau historique.

### Édition du labyrinthe
Utilisez le bouton **Change Start/End** pour modifier les positions du point de départ et d’arrivée. Cliquez d’abord sur la case de départ puis sur celle d’arrivée. Utilisez **Add or Remove Edge** pour ajouter ou supprimer des murs en cliquant sur deux cases adjacentes.

### Gestion des fichiers
Le bouton **Load** permet de charger un labyrinthe sauvegardé sous forme de fichier **.ser**. On pourra alors éditer et résoudre ce labyrinthe. On a aussi la possibilité de sauvegarder le labyrinthe courant dans un fichier en appuyant sur **Save**.

### Personnalisation visuelle
Le sélecteur **background** propose des images/thèmes prédéfinies ou la possibilité de choisir une image personnalisée depuis les fichiers de l'ordinateur. 


## Lancer l'application
### Version graphique
Pour lancer l'**interface graphique** JavaFX : 
```
mvn clean javafx:run
```

### Version terminale
Pour lancer la **version terminale** : 
```
mvn exec:java
```


## Javadoc 
Pour générer la **javadoc** : 
```
mvn javadoc:javadoc
```

Celle ci est générée dans "ProjetCynapse-ING1/target/reports/apidocs/allclasses-index.html".
