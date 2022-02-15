var mongodb = require('mongodb')
var csv = require('csv-parser')
var fs = require('fs')

var MongoClient = mongodb.MongoClient
var MONGO_URL = process.env.MONGO_URL
// /!\ si vous utilisez une version cloud, la chaîne de connexion devrait ressembler à ça
// var mongoUrl = "mongodb+srv://mongo:<password>@<url>/<database>>?retryWrites=true&w=majority";
// vous trouvez cette chaine dans l'interface Atlas Connext > Conect to your application

const DB_NAME = 'marvel'
const COLLECTION_NAME = 'heroes'

// TODO

const insertCalls = async function (db, callback) {
  const collection = db.collection(COLLECTION_NAME)
  await dropCollectionIfExists(db, collection)

  const calls = []
  fs.createReadStream('./all-heroes.csv')
    .pipe(csv())
    .on('data', data => {
      const call = {
        id: data.id,
        name: data.name,
        description: data.description,
        imageUrl: data.imageUrl,
        backgroundImageUrl: data.backgroundImageUrl,
        externalLink: data.externalLink,
        identity: {
          secretIdentities: data.secretIdentities.split(',').filter(x => x),
          birthPlace: data.birthPlace,
          occupation: data.occupation,
          aliases: data.aliases.split(',').filter(x => x),
          alignment: data.alignment,
          firstAppearance: data.firstAppearance,
          yearAppearance: data.yearAppearance ? +data.yearAppearance : undefined,
          universe: data.universe
        },
        appearance: {
          gender: data.gender,
          race: data.race,
          type: data.type,
          height: data.height ? +data.height : undefined,
          weight: data.weight ? +data.weight : undefined,
          eyeColor: data.eyeColor,
          hairColor: data.hairColor
        },
        teams: data.teams.split(',').filter(x => x),
        powers: data.powers.split(',').filter(x => x),
        partners: data.partners.split(',').filter(x => x),
        skills: {
          intelligence: data.intelligence ? +data.intelligence : undefined,
          strength: data.strength ? +data.strength : undefined,
          speed: data.speed ? +data.speed : undefined,
          durability: data.durability ? +data.durability : undefined,
          power: data.power ? +data.power : undefined,
          combat: data.combat ? +data.combat : undefined
        },
        creators: data.creators.split(',').filter(x => x)
      }

      // console.log(data)
      // console.log(call)
      // throw new Error('lsdfs')
      calls.push(call)
    })
    .on('end', () => {
      // console.log(calls.slice(0, 5))
      // throw new Error('✌')
      collection.insertMany(calls, (err, result) => {
        callback(result)
      })
    })
}

MongoClient.connect(
  MONGO_URL,
  {
    useUnifiedTopology: true
  },
  (err, client) => {
    if (err) {
      console.error(err)
      throw err
    }
    const db = client.db(DB_NAME)
    insertCalls(db, result => {
      console.log(`${result.insertedCount} calls inserted`)
      client.close()
    })
  }
)

async function dropCollectionIfExists(db, collection) {
  const matchingCollections = await db.listCollections({ name: COLLECTION_NAME }).toArray()
  if (matchingCollections.length > 0) {
    await collection.drop()
  }
}
