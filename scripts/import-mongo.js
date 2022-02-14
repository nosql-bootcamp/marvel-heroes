var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");

var MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
// /!\ si vous utilisez une version cloud, la chaîne de connexion devrait ressembler à ça
// var mongoUrl = "mongodb+srv://mongo:<password>@<url>/<database>>?retryWrites=true&w=majority";
// vous trouvez cette chaine dans l'interface Atlas Connext > Conect to your application

const dbName = "marvel";
const collectionName = "heroes";

// TODO
console.log("TODO ;-)");