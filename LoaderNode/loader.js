const fs = require('fs');
const path = require('path');
const csv = require('csv-parser');
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();
let questionId = 0; // Initialize global question ID

// Function to upload games data and images to Firestore
async function uploadAll() {

  const games = JSON.parse(fs.readFileSync(path.join(__dirname, 'data', 'games.json'), 'utf8'));

  for (const game of games) {
    try {
      // Read image file and convert to base64
      const gameData = initGameData(game);

      const docRef = await db.collection('games').doc(game.id.toString()).set(gameData);
      //console.log(`Inserted game: ${JSON.stringify(gameData)}`);
      //console.log(`Document URL: https://console.firebase.google.com/project/${serviceAccount.project_id}/firestore/data/games/${docRef.id}`);

      // Call uploadCSVData for each game
      await uploadCSVData(game.name_en);
    } catch (error) {
      console.error('Error inserting game data:', error);
    }
  }

 
}

function initGameData(game) {
  const imagePath = path.join(__dirname, 'data', 'images', `${game.name_en}.jpg`);
  //read image as base64 string
  // const imageBuffer = fs.readFileSync(imagePath);
  // const imageBase64 = imageBuffer.toString('base64');
  // const imageUrl = `data:image/jpeg;base64,${imageBase64}`;
// imageUrl: `data:image/jpeg;base64,${imageBase64}`

  // Add game data to "games" collection in Firestore
  const gameData = {
    id: game.id,
    name: game.name,
    name_en: game.name_en,
    description: game.description,
    imageUrl:game.imageUrl,
    isActive:true    //is games available for user

  };

  return gameData;
}

// Function to upload CSV data to Firestore
async function uploadCSVData(gameName) {
  const data = [];
  const csvFilePath = path.join(__dirname, 'data', 'csv',`${gameName}.csv`);

  // Check if the CSV file exists
 if (!fs.existsSync(csvFilePath)) {
  console.error(`CSV file ${csvFilePath} does not exist.`);
  return;
}

  // Read the CSV file in data[]
  fs.createReadStream(csvFilePath)
    .pipe(csv())
    .on('data', (row) => {
      //console.log('Parsed row:', row); // Log each parsed row
      data.push(row);
    })
    .on('end', () => {
      console.log(`CSV file ${gameName}.csv successfully processed. `);

      // Insert data into Firestore collection
      data.forEach(async (record) => {
        try {
          // Map CSV fields to Firestore document fields
          const docData = {           
            gameNumber: parseInt(record['GameNumber'], 10),
            question: record['Question'].replace(/"/g, '').trim(),
            answer1: record['Answer1'].replace(/"/g, '').trim(),
            answer2: record['Answer2'].replace(/"/g, '').trim(),
            answer3: record['Answer3'].replace(/"/g, '').trim(),
            answer4: record['Answer4'].replace(/"/g, '').trim(),
            correctAnswer: parseInt(record['CorrectAnswer'], 10),
            difficultyLevel: parseInt(record['DifficultyLevel'], 10),
            questionType: record['QuestionType'],
            answerType: record['AnswerType']
          };

          // Add data to "questions" collection in Firestore
          questionId++;
          const docRef = await db.collection('questions').doc(questionId.toString()).set(docData);
         
          //console.log(`Document URL: https://console.firebase.google.com/project/${serviceAccount.project_id}/firestore/data/${docRef.path}`);
          //console.log(`insert doc id ${questionId} in game ${gameName}`);         
          //console.log(`Inserted: ${JSON.stringify(docData)}`);
          //console.log(`Document URL: https://console.firebase.google.com/project/${serviceAccount.project_id}/firestore/data/${docRef.path}`);
        } catch (error) {
          console.error('Error inserting data:', error);
        }
      });
    })
    .on('error', (error) => {
      console.error(`Error reading CSV file ${gameName}.csv:`, error);
    });
}

// Run the functions
uploadAll();