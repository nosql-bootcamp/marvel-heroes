const {
    MongoClient
  } = require('mongodb');
  const csv = require('csv-parser');
  const fs = require('fs');
  const {
    mainModule
  } = require('process');

var MONGO_URL = "mongodb://localhost:27017";
// /!\ si vous utilisez une version cloud, la chaîne de connexion devrait ressembler à ça
// var mongoUrl = "mongodb+srv://mongo:<password>@<url>/<database>>?retryWrites=true&w=majority";
// vous trouvez cette chaine dans l'interface Atlas Connext > Conect to your application

const DB_NAME = "marvel";
const COLLECTION_NAME = "heroes";

const insertCalls = async function (db, callback) {
    const collection = db.collection(COLLECTION_NAME);
    await dropCollectionIfExists(db, collection);
  
    const heroes = [];
    fs.createReadStream('./all-heroes.csv')
      .pipe(csv())
      .on('data', data => {
  
        const hero = {
          id: data.id,
          name: data.name,
          description: data.description,
          imageUrl: data.imageUrl,
          backgroundImageUrl: data.backgroundImageUrl,
          externalLink: data.externalLink,
          identity: {
            secretIdentities: data.secretIdentities.split(','),
            birthPlace: data.birthPlace,
            occupation: data.occupation,
            aliases: data.aliases.split(','),
            alignment: data.alignment,
            firstAppearance: data.firstAppearance,
            yearAppearance: data.yearAppearance,
            universe: data.universe,
          },
          appearance: {
            gender: data.gender,
            race: data.race,
            type: data.type,
            height: data.height,
            weight: data.weight,
            eyeColor: data.eyeColor,
            hairColor: data.hairColor,
          },
          teams: data.teams.split(','),
          powers: data.powers.split(','),
          partners: data.partners.split(','),
          skills: {
            intelligence: data.intelligence,
            strength: data.strength,
            speed: data.speed,
            durability: data.durability,
            power: data.power,
            combat: data.combat,
          },
          creators: data.creators.split(','),
        };

        if(hero.name == 'Iron Man') {
          console.log(hero);
        }
  
        heroes.push(hero);
      })
      .on('end', () => {
        collection.insertMany(heroes, (err, result) => {
          callback(result)
        });
      });
  }
  
  
  MongoClient.connect(MONGO_URL, {
    useUnifiedTopology: true
  }, (err, client) => {
    if (err) {
      console.error(err);
      throw err;
    }
    const db = client.db(DB_NAME);
    insertCalls(db, result => {
      console.log(`${result.insertedCount} heroes inserted`);
      client.close();
    });
  
  });
  
  async function dropCollectionIfExists(db, collection) {
    const matchingCollections = await db.listCollections({
      name: COLLECTION_NAME
    }).toArray();
    if (matchingCollections.length > 0) {
      await collection.drop();
    }
  }