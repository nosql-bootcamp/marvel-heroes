const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
const esClient = new Client({ node: 'http://localhost:9200' })
const heroesIndexName = 'heroes'

const dataset = [];

fs.createReadStream('all-heroes.csv')
    .pipe(csv())
    .on('data', (data) => dataset.push(data))
    .on('end', () => {
        console.log(`Imported ${dataset.length} lines`);
        run().catch(console.error)
    });


async function run() {
    await esClient.indices.create({
        index: heroesIndexName,
        body: {
            mappings: {
                properties: {
                    id: { type: 'text' },
                    name: { type: 'text' },
                    description: { type: 'text' },
                    imageUrl: { type: 'text' },
                    backgroundImageUrl: { type: 'text' },
                    externalLink: { type: 'text' },
                    secretIdentities: { type: 'text' },
                    birthPlace: { type: 'text' },
                    occupation: { type: 'text' },
                    aliases: { type: 'text' },
                    alignment: { type: 'text' },
                    firstAppearance: { type: 'text' },
                    yearAppearance: { type: 'integer' },
                    universe: { type: 'text' },
                    gender: { type: 'text' },
                    race: { type: 'text' },
                    type: { type: 'text' },
                    height: { type: 'integer' },
                    weight: { type: 'integer' },
                    eyeColor: { type: 'text' },
                    hairColor: { type: 'text' },
                    teams: { type: 'text' },
                    powers: { type: 'text' },
                    partners: { type: 'text' },
                    intelligence: { type: 'integer' },
                    strength: { type: 'integer' },
                    speed: { type: 'integer' },
                    durability: { type: 'integer' },
                    power: { type: 'integer' },
                    combat: { type: 'integer' },
                    creators: { type: 'text' }
                }
            }
        }
    }, { ignore: [400] })

    const body = dataset.flatMap(doc => [{ index: { _index: heroesIndexName } }, doc])

    const { body: bulkResponse } = await esClient.bulk({ refresh: true, body })

    if (bulkResponse.errors) {
        const erroredDocuments = []
        // The items array has the same order of the dataset we just indexed.
        // The presence of the `error` key indicates that the operation
        // that we did for the document has failed.
        bulkResponse.items.forEach((action, i) => {
            const operation = Object.keys(action)[0]
            if (action[operation].error) {
                erroredDocuments.push({
                    // If the status is 429 it means that you can retry the document,
                    // otherwise it's very likely a mapping error, and you should
                    // fix the document before to try it again.
                    status: action[operation].status,
                    error: action[operation].error,
                    operation: body[i * 2],
                    document: body[i * 2 + 1]
                })
            }
        })
        console.log(erroredDocuments)
    }

    const { body: count } = await esClient.count({ index: heroesIndexName })
    console.log(count)
}

