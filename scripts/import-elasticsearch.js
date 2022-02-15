const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
// Connection a elasticsearch via l'image docker
const esClient = new Client({
    node: 'http://0.0.0.0:9200',
    auth: {
        username: 'elastic',
        password: 'root'
    }
});

const heroesIndexName = 'heroes'

// Création de l'indice
esClient.indices.create({ index: heroesIndexName }, (err, resp) => {
    if (err) console.trace(err.message);
});

let heroes = [];
fs
    .createReadStream('all-heroes.csv')
    .pipe(csv())
    // Pour chaque ligne on créé un document JSON pour le héros correspondant
    .on('data', data => {
        heroes.push({
            id: data.id,
            name: data.name,
            imageUrl : data.imageUrl,
            description: data.description,
            universe : data.universe,
            gender : data.gender,
            aliases: data.aliases,
            secretIdentities: data.secretIdentities,
            partners: data.partners
        });
    })
    // A la fin on créé l'ensemble des acteurs dans ElasticSearch
    .on('end', () => {
        esClient.bulk(createBulkInsertQuery(heroes), (err, resp) => {
            if (err) console.trace(err.message);
            else console.log(`Inserted ${resp.body.items.length} heroes`);
            esClient.close();
        });
    });

// Fonction utilitaire permettant de formatter les données pour l'insertion "bulk" dans elastic
function createBulkInsertQuery(heroes) {
    const body = heroes.reduce((acc, heroe) => {
        const { id, name, description, aliases, secretIdentities, partners } = heroe;
        acc.push({ index: { _index: heroesIndexName, _type: '_doc', _id: heroe.id } })
        acc.push({ ...heroe })
        return acc
    }, []);

    return { body };
}