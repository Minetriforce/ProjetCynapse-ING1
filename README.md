# Description
Cette application JavaFX permet de générer, modifier, charger, sauvegarder et résoudre des labyrinthes via une interface graphique interactive. Elle propose plusieurs méthodes de génération et de résolution ainsi que la possibilité d’éditer les points de départ et d’arrivée et de modifier manuellement les murs. 

## Version de Java: 21.0.3 et version JavaFX : 21.0, Maven à installer avec "sudo apt install mvn" et interface utilisée : VSCode ou IntellJ. Pour générer la javadoc lancer la commande dans le terminal: **mvn javadoc:javadoc**



# Fonctionnalités principales

Pour lancer l'interface graphique JavaFX, il faut écrire **mvn clean javafx:run** dans le terminal.

## Génération de labyrinthe
Dès que l’interface est lancée, il faut remplir les cases correspondantes suivantes : le nombre de lignes (rows), colonnes (cols) et la graine aléatoire (seed) pour contrôler la taille et la génération. Sélectionnez ensuite une méthode de génération dans le menu déroulant. Si nécessaire, il est possible d’activer un mode pas à pas avec un contrôle du délai en millisecondes à renseigner dans la case “timestep”. Le bouton **Generate** lance la création du labyrinthe.

## Résolution de labyrinthe
La résolution de labyrinthe n’est accessible qu’une fois qu’un labyrinthe a été généré ou chargé. Sélectionnez une méthode de résolution, activez le mode pas à pas si désiré et cliquez sur **Solve** pour lancer la résolution animée. Les statistiques telles que la méthode utilisée, la longueur du chemin, le nombre de cases visitées et le temps sont affichées dans le panneau historique.

## Édition du labyrinthe
Utilisez le bouton **Change Start/End** pour modifier les positions du point de départ et d’arrivée. Cliquez d’abord sur la case de départ puis sur celle d’arrivée. Utilisez **Add or Remove Edge** pour ajouter ou supprimer des murs en cliquant sur deux cases adjacentes.

## Gestion des fichiers
Le bouton **Load** permet de charger un labyrinthe sauvegardé sous forme de fichier **.ser**. On pourra alors éditer et résoudre ce labyrinthe. On a aussi la possibilité de sauvegarder le labyrinthe courant dans un fichier avec le bouton **Save**.

## Personnalisation visuelle
Le sélecteur **background** propose des images prédéfinies ou la possibilité de choisir une image personnalisée depuis les fichiers de l'ordinateur. Les erreurs si on rencontre une exception lors de la saisie d’un champ sont signalées par des pop-ups.

## MainCLI
On peut aussi accéder à la version terminale, il suffit d'aller sur le fichier **MainCLI.java** et de le lancer à partir de l'IDE ou bien de taper la commande **mvn exec:java** dans le terminal. Il faudra ensuite utiliser le clavier numérique pour choisir les options voulues à chaque fois.
