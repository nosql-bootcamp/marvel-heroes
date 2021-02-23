# Marvel Heroes 

L'objectif de cet exerice est de développer une application web permettant de découvrir des Héros de Comics (Marvel et DC Comics).

Les principales fonctionnalités de l'application sont : 
* Recherche de Héros
  * La recherche est une recherche full-text, se basant sur les éléments suivants des Héros, par ordre de priorité :
    * Nom
    * Alias et identité secrète
    * Description
    * Partenaires
* Suggestion de Héros
  * La suggestion doit tenir compte également des alias et identités secrètes des Héros
* Affichage de la fiche détaillée d'un Héros
* Affichage des 5 dernières fiches consultées
* Affichage du top 5 des fiches consultées
* Statistiques :
  * Répartition des Héros par univers (Marvel / DC Comics).
  * Répartition des Héros par année d'apparition et par univers.
  * Top 5 des super-pouvoirs.

Techniquement : 
* La recherche et la suggestion se basent sur Elasticsearch
* La fiche détaillée et les statistiques se basent sur MongoDB
* Les hits (5 dernières fiches + top 5) se basent sur Redis

Votre mission est la suivante : compléter le code manquant afin de faire fonctionner complètement l'application !

Les éléments à compléter se trouvent dans les fichiers suivants : 
* Scripts d'import des données
  * `scripts/import-elasticsearch.js`
  * `scripts/import-mongo.js`
* Application
  * `app/repository/ElasticRepository.java`
  * `app/repository/MongoDBRepository.java`
  * `app/repository/RedisRepository.java`


## Pré-requis

Datastores : 
* Elasticsearch (version 7.5.x)
* MongoDB (version 4.2.x)
* Redis (version 5.0.x)

Languages et tooling :
* Java (version 11)
* SBT (version 1.2.x ou >)
* Node (version 10.x)
* NPM (version 6.x)

Environnement de développement conseillé :
* Intellij Idea (pour la partie Play/Java)
  * + plugin SBT : https://plugins.jetbrains.com/plugin/5007-sbt
* VSCode (pour la partie Node)

## Lancer l'application

Dans un terminal, à la racine du projet, lancez la commande `sbt`, puis la commande `~run` dans l'invite de commandes sbt
```bash
➜  marvel-heroes git:(master) ✗ sbt
[info] Loading settings for project global-plugins from idea.sbt,metals.sbt ...
[info] Loading global plugins from /Users/guillaume/.sbt/1.0/plugins
[info] Loading settings for project marvel-heroes-build from plugins.sbt ...
[info] Loading project definition from /Users/guillaume/Developer/Cours/marvel-heroes/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to marvel-heroes (in build file:/Users/guillaume/Developer/Cours/marvel-heroes/)
[info] sbt server started at local:///Users/guillaume/.sbt/1.0/server/3dde21270dd620b18561/sock
[marvel-heroes] $ ~run
```

## Dataset

Les données sont disponibles dans le dossier `scripts/all-heroes.csv` et proviennent de plusieurs sources de données : 
* [API Marvel](https://developer.marvel.com/)
* [SuperheroDB](https://www.superherodb.com/)

## Exemples de documents

### MongoDB

Voici un exemple de document stocké dans MongoDB, obtenu grâce à la commande `db.heroes.findOne({"name": "Iron Man"})` : 

```json
{
  "_id": ObjectId("5e46b803a260708ef2636b8f"),
  "id": "1009368",
  "name": "Iron Man",
  "description": "Wounded, captured and forced to build a weapon by his enemies, billionaire industrialist Tony Stark instead created an advanced suit of armor to save his life and escape captivity. Now with a new outlook on life, Tony uses his money and intelligence to make the world a safer, better place as Iron Man.",
  "imageUrl": "https://i.annihil.us/u/prod/marvel/i/mg/9/c0/527bb7b37ff55.jpg",
  "backgroundImageUrl": "https://x.annihil.us/u/prod/marvel/i/mg/c/90/537bb1f94fa4f.gif",
  "externalLink": "https://marvel.com/universe/Iron_Man_(Anthony_Stark)",
  "identity": {
    "secretIdentities": [
      "Anthony Edward Stark",
      "Tony"
    ],
    "birthPlace": "Long Island, New York",
    "occupation": "Inventor, Industrialist; former United States Secretary of Defense",
    "aliases": [
      "Iron Man",
      "The Invincible Iron Man",
      "Tony Stark"
    ],
    "alignment": "good",
    "firstAppearance": "Tales of Suspence #39 (March, 1963)",
    "yearAppearance": 1963,
    "universe": "Marvel"
  },
  "appearance": {
    "gender": "Male",
    "race": "Human",
    "type": "",
    "height": 198,
    "weight": 191,
    "eyeColor": "blue",
    "hairColor": "Black"
  },
  "teams": [
    "Avengers",
    "Department of Defense",
    "Force Works",
    "Guardians of the Galaxy",
    "Guardians of the Galaxy (2008 team)",
    "Illuminati",
    "New Avengers",
    "S.H.I.E.L.D",
    "Stark Industries",
    "Stark Resilient",
    "The Mighty Avengers",
    "The New Avengers",
    "Thunderbolts",
    "United States Department of Defense",
    "West Coast Avengers"
  ],
  "powers": [
    "Accelerated Healing",
    "Durability",
    "Energy Absorption",
    "Flight",
    "Underwater breathing",
    "Marksmanship",
    "Super Strength",
    "Energy Blasts",
    "Stamina",
    "Super Speed",
    "Weapon-based Powers",
    "Energy Beams",
    "Reflexes",
    "Force Fields",
    "Power Suit",
    "Radiation Immunity",
    "Vision - Telescopic",
    "Magnetism",
    "Invisibility",
    "Vision - Night",
    "Vision - Thermal"
  ],
  "partners": [
    "Pepper Potts",
    "Rescue",
    "War Machine"
  ],
  "skills": {
    "intelligence": 100,
    "strength": 85,
    "speed": 58,
    "durability": 85,
    "power": 100,
    "combat": 64
  },
  "creators": [
    "Don Heck",
    "Jack Kirby",
    "Larry Lieber",
    "Stan Lee"
  ]
}
```

## Liens utiles

* Suggestion avec Elasticsearch : https://www.elastic.co/guide/en/elasticsearch/reference/7.5/search-suggesters.html#completion-suggester
* Query Boosting (Elasticsearch) : 
  * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#_boosting
  * https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-boost.html
* Aggrégations avec MongoDB : https://docs.mongodb.com/manual/meta/aggregation-quick-reference/

Drivers Java utilisés : 
* Driver Redis *Lettuce* : https://lettuce.io/
* Driver MongoDB réactif : https://mongodb.github.io/mongo-java-driver-reactivestreams/


## Pour rendre le travail

Une fois l'ensemble du code complété et que l'appli est fonctionnelle, faites une Pull Request !
