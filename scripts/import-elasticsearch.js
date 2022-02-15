const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
const { Readable } = require('stream');

const ELASTIC_SEARCH_URI = 'http://localhost:9200';
const HEROES_INDEX_NAME = 'heroes'

function getArrayOfString(data = '') {
    if (data == '') {
        return [];
    }
    return data.split(',').map(i => i.trim());
}

function formatData(data) {
    return {
        ...data,
        identity: {
            ...data.identity,
            secretIdentities: getArrayOfString(data.identity?.secretIdentities),
            aliases: getArrayOfString(data.identity?.aliases)
        },
        teams: getArrayOfString(data?.teams),
        powers: getArrayOfString(data?.powers),
        partners: getArrayOfString(data?.partners),
        creators: getArrayOfString(data?.creators),
        suggest: [ data.name, data.description, data.creators ]
    };
}

async function run() {
    const client = new Client({ node: ELASTIC_SEARCH_URI});

    // Drop index if exists
    await client.indices.delete({
        index: HEROES_INDEX_NAME,
        ignore_unavailable: true
    });

    await client.indices.create({
        index: HEROES_INDEX_NAME,
        body : {
            // TODO configurer l'index https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
            mappings: {
                properties: {
                    "suggest": {
                        "type": "completion",
                        "preserve_position_increments": false
                    },
                    "name": {
                        "type": "keyword"
                    }
                }
            }
        }
    });


    let heroes = [];
    fs.createReadStream('./all-heroes.csv')
        .pipe(csv())
        .on('data', data => heroes.push(formatData(data)))
        .on('end', async () => {
            const result = await client.helpers.bulk({
                datasource: Readable.from(heroes),
                onDocument (doc) {
                    return {
                        index: { _index: HEROES_INDEX_NAME },
                        create: { doc }
                    }
                }
            });

            console.log(result);
            await client.close();
        });
}

run().catch(console.log);